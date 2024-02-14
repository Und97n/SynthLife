package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities.AliveEntity
import org.ua.und97n.synthlife.field.CellHandle
import org.ua.und97n.synthlife.field.Direction

abstract class PassiveEnergyProducer(
    initialEnergy: EnergyValue,
    genome: Genome
) : AliveEntity(initialEnergy, genome) {
    abstract fun produceEnergy(cellHandle: CellHandle): EnergyValue

    protected abstract val minimalEnergyToContain: EnergyValue

    override fun canAbsorbEnergyFromConnection(direction: Direction): Boolean = false

    final override fun updateAliveEntity(cellHandle: CellHandle) {
        val connected = connections.numberOfConnected()

        if (connected == 0) {
            die(cellHandle, DeathReason.NO_ENERGY_OUTPUT)
        } else {
            energy += produceEnergy(cellHandle)

            var toShare = (energy - minimalEnergyToContain).splitBy(connected.toDouble())

            if (toShare > EnergyValue.ZERO) {
                connections.iterateExistent { entity, direction ->
                    (entity as? AliveEntity)?.let {
                        energy -= it.enrichWithEnergyFromConnection(direction.mirror(), toShare)
                    }
                }
            }
        }
    }
}