package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.EntityConnections
import org.ua.und97n.synthlife.field.OrganicValue

class Sprig(
    initialEnergy: EnergyValue,
    private var targetConnections: EntityConnections
) : AliveEntity(initialEnergy) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.05)

    override fun canAbsorbEnergyFromConnection(direction: Direction): Boolean =
        // cannot absorb from targets
        targetConnections.isConnectedTo(direction).not()

    override fun updateAliveEntity() {
        targetConnections.refreshEntities()

        val num = targetConnections.numberOfConnected()

        if (num == 0) {
            die(DeathReason.NO_ENERGY_OUTPUT)
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
    }
}