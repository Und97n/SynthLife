package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.DeathReason
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.EntityConnections
import org.ua.und97n.synthlife.field.OrganicValue
import kotlin.concurrent.fixedRateTimer

class Sprig private constructor(
    initialEnergy: EnergyValue,
    genome: Genome,
    private var targetConnections: EntityConnections
) : AliveEntity(initialEnergy, genome) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.1)

    override fun canAbsorbEnergyFromConnection(direction: Direction): Boolean =
        // cannot absorb from targets
        targetConnections.isConnectedTo(direction).not()

    override fun updateAliveEntity() {
        targetConnections.refreshEntities()

        val num = targetConnections.numberOfConnected()

        if (num == 0) {
            var changed = false

            // try to connect to other guys
            Direction.entries.forEach { direction ->
                (connections.getConnected(direction) as? AliveEntity)?.let {
                    if (it.canAbsorbEnergyFromConnection(direction.mirror())) {
                        targetConnections = EntityConnections.ofSingle(direction, it)
                        changed = true
                    }
                }
            }

            if (!changed) {
                die(DeathReason.NO_ENERGY_OUTPUT)
            }
        } else {
            var toShare = (energy - MINIMAL_ENERGY_TO_CONTAIN).splitBy(num.toDouble())

            Direction.entries.forEach { direction ->
                val e = targetConnections.getConnected(direction) as? AliveEntity

                e?.let {
                    energy -= it.enrichWithEnergyFromConnection(direction.mirror(), toShare)
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
            targetConnections: EntityConnections,
            additionalEnergy: EnergyValue = EnergyValue.ZERO
        ): Sprig =
            Sprig(OrganicRoot.INIT_ENERGY, genome, targetConnections)
    }
}