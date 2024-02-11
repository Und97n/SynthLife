package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.*
import org.ua.und97n.synthlife.field.Utils.getByIndexSafe
import org.ua.und97n.synthlife.field.Utils.normalizeAsIndex

class Bot(
    initialEnergy: EnergyValue = INIT_ENERGY,
    initialDirection: Direction,
    initialGenome: ByteArray,
) : AliveEntity(initialEnergy) {

    override val baseEnergyConsumption: EnergyValue = EnergyValue(0.5)

    private val genome: ByteArray = initialGenome.copyOf(GENOME_SIZE)
    private var genomePointer: Int = 0

    private var direction: Direction = initialDirection

    override fun updateAliveEntity() {
        var timeUnits = 1.0

        while (timeUnits > 0) {
            val command = Command.of(nextGenome())
            spendEnergy(command.energy)
            timeUnits -= command.timeUnits

            when (command) {
                Command.NOOP -> {
                }

                Command.MOVE -> {
                    cellContext.tryMove(direction)
                }

                Command.TURN_CLOCKWISE -> {
                    direction += 1
                }

                Command.TURN_COUNTER_CLOCKWISE -> {
                    direction -= 1
                }

                Command.GROW -> {
                    grow()
                }

                Command.RESTART_PROGRAM -> {
                    genomePointer = 0
                }
            }
        }
    }

    override fun toString(): String =
        "Bot(energy=$energy, direction=$direction)"

    private fun nextGenome(): Int =
        genome[(genomePointer++).normalizeAsIndex(genome.size)].toUByte().toInt()

    private fun grow() {
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
            (child1 == ChildrenCases.NONE || cellContext.isDirectionAvailable(direction - 1)) &&
            (child2 == ChildrenCases.NONE || cellContext.isDirectionAvailable(direction)) &&
            (child3 == ChildrenCases.NONE || cellContext.isDirectionAvailable(direction + 1))
        ) {

            val ents = listOfNotNull(
                child1.provideEntity(this, direction - 1),
                child2.provideEntity(this, direction),
                child3.provideEntity(this, direction + 1),
            )

            if (cellContext.spawnEntities(ents)) {
                val freeEnergy = EnergyValue(totalBudget.innerModel - required.innerModel)

                // turn bot into a sprig and connect everything to it
                val sprigConnections = this.connections + EntityConnections.ofMany(ents)
                val sprigTargetConnections = EntityConnections.ofMany(ents.filter { it.second is Bot })
                val sprig = Sprig(Sprig.INITIAL_ENERGY + freeEnergy, sprigTargetConnections)
                sprig.initConnections(sprigConnections)

                ents.forEach {
                    it.second.initConnections(
                        EntityConnections.ofSingle(it.first.mirror(), this)
                    )
                }

                cellContext.replaceEntityTo(sprig)
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
            direction: Direction
        ): Pair<Direction, Entity>? {
            val ent = when (this) {
                NONE -> {
                    null
                }

                LEAF -> Leaf(Leaf.INIT_ENERGY)

                BOT -> Bot(
                    initialEnergy = INIT_ENERGY,
                    initialDirection = direction,
                    initialGenome = caller.genome,
                )

                M_ROOT -> MineralRoot(MineralRoot.INIT_ENERGY)
                O_ROOT -> OrganicRoot(OrganicRoot.INIT_ENERGY)
            }

            return ent?.let { direction to it }
        }

        companion object {
            fun of(value: Int): ChildrenCases =
                ChildrenCases.entries.getByIndexSafe(value)
        }
    }

    enum class Command(val energy: EnergyValue, val timeUnits: Double) {
        NOOP(EnergyValue.ZERO, 1.0),
        MOVE(EnergyValue(0.5), 1.0),
        TURN_CLOCKWISE(EnergyValue(0.15), 1.0),
        TURN_COUNTER_CLOCKWISE(EnergyValue(0.15), 1.0),
        GROW(EnergyValue(0.5), Double.POSITIVE_INFINITY),
        RESTART_PROGRAM(EnergyValue.ZERO, 0.5),
        ;

        companion object {
            fun of(genome: Int): Command =
                Command.entries.getByIndexSafe(genome)
        }
    }

    companion object {
        val ORGANIC_COST = OrganicValue(10.0)
        val INIT_ENERGY = EnergyValue(10.0)

        private const val GENOME_SIZE = 64
    }
}