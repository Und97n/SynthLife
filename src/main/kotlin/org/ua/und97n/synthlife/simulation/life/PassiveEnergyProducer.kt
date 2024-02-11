package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.Direction

abstract class PassiveEnergyProducer(initialEnergy: EnergyValue) : AliveEntity(initialEnergy) {
    abstract fun produceEnergy(): EnergyValue

    protected abstract val minimalEnergyToContain: EnergyValue

    final override fun updateAliveEntity() {
        val connected = connections.numberOfConnected()

        if (connected == 0) {
            die(DeathReason.NO_ENERGY_OUTPUT)
        } else if(hasProducersNearby()) {
            die(DeathReason.NO_SPACE)
        } else {
            energy += produceEnergy()

            var toShare = (energy - minimalEnergyToContain).splitBy(connected.toDouble())

            if (toShare > EnergyValue.ZERO) {
                Direction.entries.forEach { direction ->
                    (connections.getConnected(direction) as? AliveEntity)?.let {
                        energy -= it.enrichWithEnergyFromConnection(direction.mirror(), toShare)
                    }
                }
            }
        }
    }

    private fun hasProducersNearby(): Boolean =
        Direction.entries.any {
            val other = cellContext.getEntity(it) as? PassiveEnergyProducer
            // Delete only single one, not both
            other?.let { it.hashCode() > this.hashCode() } ?: false
        }
}