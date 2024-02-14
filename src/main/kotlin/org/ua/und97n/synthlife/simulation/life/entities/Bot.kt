package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.WorldContextImpl
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.GenomeCommand
import org.ua.und97n.synthlife.field.*
import org.ua.und97n.synthlife.field.Utils.getByIndexSafe

class Bot(
    initialEnergy: EnergyValue = INIT_ENERGY,
    initialDirection: Direction,
    genome: Genome,
    private var genomePointer: Int = 0,
) : AliveEntity(initialEnergy, genome) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(1.0)

    override val organicCost: OrganicValue
        get() = ORGANIC_COST

    private var direction: Direction = initialDirection

    override fun onEnergyOverflow(cellHandle: CellHandle, overflowValue: EnergyValue) {
        disconnectAll()
    }

    override fun updateAliveEntity(cellHandle: CellHandle) {
        var timeUnits = 1.0

        while (timeUnits > 0) {
            val command = GenomeCommand.of(nextGenome())
            spendEnergy(command.energy)
            timeUnits -= command.timeUnits

            when (command) {
                GenomeCommand.NOOP -> {
                }

                GenomeCommand.MOVE -> {
                    if (this.connections.isEmpty()) {
                        cellHandle.tryMove(direction)
                    }
                }

                GenomeCommand.TURN_CLOCKWISE -> {
                    direction += 1
                }

                GenomeCommand.TURN_COUNTER_CLOCKWISE -> {
                    direction -= 1
                }

                GenomeCommand.GROW -> {
                    grow(cellHandle)
                }

                GenomeCommand.RESTART_PROGRAM -> {
                    genomePointer = 0
                }
            }
        }
    }

    override fun toString(): String =
        "Bot(energy=$energy, direction=$direction)"

    private fun nextGenome(): Int =
        genome[genomePointer++]

    private fun grow(cellHandle: CellHandle) {
        val specification = nextGenome()

        val child1 = ChildrenCases.of((specification and 0b111))
        var child2 = ChildrenCases.of(((specification ushr 3) and 0b111))
        val child3 = ChildrenCases.of(((specification ushr 6) and 0b111))

        if (child1 == child2 && child2 == child3) {
            child2 = child2.next()
        }

        if (child1 != ChildrenCases.BOT && child2 != ChildrenCases.BOT && child3 != ChildrenCases.BOT) {
            child2 = ChildrenCases.BOT
        }

        val totalBudget = energy.convertToOrganic() + ORGANIC_COST

        val required =
            child1.cost + child2.cost + child3.cost + Sprig.ORGANIC_COST + Sprig.INITIAL_ENERGY.convertToOrganic()

        if (
            totalBudget >= required &&
            (child1 == ChildrenCases.NONE || cellHandle.isDirectionAvailable(direction - 1)) &&
            (child2 == ChildrenCases.NONE || cellHandle.isDirectionAvailable(direction)) &&
            (child3 == ChildrenCases.NONE || cellHandle.isDirectionAvailable(direction + 1))
        ) {

            val ctx = cellHandle.getWorldContext<WorldContextImpl>()

            val ents = listOfNotNull(
                child1.provideEntity(this, ctx, direction - 1),
                child2.provideEntity(this, ctx, direction),
                child3.provideEntity(this, ctx, direction + 1),
            )

            if (cellHandle.spawnEntities(ents)) {
                // turn bot into a sprig and connect everything to it
                val freeEnergy = EnergyValue(totalBudget.innerModel - required.innerModel)
                val sprigTargetConnections = DirectionSet.of(ents.filter { it.second is Bot }.map { it.first })
                val sprig = Sprig.of(genome, sprigTargetConnections, freeEnergy)

                ents.forEach {
                    sprig.connectUnsafe(it.first, it.second)
                }

                replaceThis(cellHandle, sprig, preserveConnections = true)
            }
        }
    }

    enum class ChildrenCases(val cost: OrganicValue) {
        NONE(OrganicValue.ZERO),
        LEAF(Leaf.ORGANIC_COST + Leaf.INIT_ENERGY.convertToOrganic()),
        BOT(ORGANIC_COST + INIT_ENERGY.convertToOrganic()),
        M_ROOT(MineralRoot.ORGANIC_COST + MineralRoot.INIT_ENERGY.convertToOrganic()),
        O_ROOT(OrganicRoot.ORGANIC_COST + OrganicRoot.INIT_ENERGY.convertToOrganic()),
        ;

        fun next(): ChildrenCases = ChildrenCases.entries.getByIndexSafe(this.ordinal + 1)

        fun provideEntity(
            caller: Bot,
            worldContextImpl: WorldContextImpl,
            direction: Direction
        ): Pair<Direction, Entity>? {
            val ent = when (this) {
                NONE -> {
                    null
                }

                LEAF -> Leaf.of(caller.genome)

                BOT -> Bot(
                    initialEnergy = INIT_ENERGY,
                    initialDirection = direction,
                    genome = caller.genome.produceChild(worldContextImpl),
                    genomePointer = caller.genomePointer
                )

                M_ROOT -> MineralRoot.of(caller.genome)
                O_ROOT -> OrganicRoot.of(caller.genome)
            }

            return ent?.let { direction to it }
        }

        companion object {
            fun of(value: Int): ChildrenCases =
                ChildrenCases.entries.getByIndexSafe(value)
        }
    }

    companion object {
        val ORGANIC_COST = OrganicValue(10.0)
        val INIT_ENERGY = EnergyValue(10.0)
    }
}