package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.DeathReason
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.synthlife.field.*

class Sprig private constructor(
    initialEnergy: EnergyValue,
    genome: Genome,
    private var targets: DirectionSet
) : AliveEntity(initialEnergy, genome) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.1)

    override val organicCost: OrganicValue
        get() = ORGANIC_COST

    override val aliveUnderCriticalMinerals: Boolean
        get() = true

    override val aliveUnderCriticalOrganics: Boolean
        get() = true

    override fun canAbsorbEnergyFromConnection(direction: Direction): Boolean =
        // cannot absorb from targets
        targets.isConnected(direction).not()

    override fun updateAliveEntity(cellHandle: CellHandle) {
        connections.iterateEmpty { targets = targets.disconnect(it) }
        val num = targets.numOfConnected()

        if (num == 0) {
            // try to connect to other guys
            connections.iterateExistent { entity, direction ->
                (entity as? AliveEntity)?.let {
                    if (it.canAbsorbEnergyFromConnection(direction.mirror())) {
                        targets = targets.connect(direction)
                    }
                }

            }

            if (targets.isEmpty()) {
                die(cellHandle, DeathReason.NO_ENERGY_OUTPUT)
            }
        } else {
            val toShare = (energy - MINIMAL_ENERGY_TO_CONTAIN).splitBy(num.toDouble()*4)

            connections.iterateExistent { entity, direction ->
                if (targets.isConnected(direction) && entity is AliveEntity) {
                    energy -= entity.enrichWithEnergyFromConnection(direction.mirror(), toShare)
                }
            }
        }
    }

    companion object {
        private val MINIMAL_ENERGY_TO_CONTAIN = EnergyValue(0.5)

        val INITIAL_ENERGY = EnergyValue(0.5)
        val ORGANIC_COST = OrganicValue(0.5)

        fun of(
            genome: Genome,
            targetConnections: DirectionSet,
            additionalEnergy: EnergyValue = EnergyValue.ZERO
        ): Sprig =
            Sprig(OrganicRoot.INIT_ENERGY + additionalEnergy, genome, targetConnections)
    }
}