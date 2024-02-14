package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.PassiveEnergyProducer
import org.ua.und97n.synthlife.field.CellHandle
import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.OrganicValue

class Leaf private constructor(
    initialEnergy: EnergyValue,
    genome: Genome
) : PassiveEnergyProducer(initialEnergy, genome) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.25)

    override val minimalEnergyToContain: EnergyValue
        get() = EnergyValue(1.0)

    override val organicCost: OrganicValue
        get() = ORGANIC_COST

    override fun produceEnergy(cellHandle: CellHandle): EnergyValue =
        if (hasLeafsNearby(cellHandle)) {
            EnergyValue.ZERO
        } else {
            EnergyValue(cellHandle.sun.innerModel)
        }

    private fun hasLeafsNearby(cellHandle: CellHandle): Boolean =
        Direction.entries.any {
            cellHandle.getEntity(it) is Leaf
        }

    companion object {
        val INIT_ENERGY = EnergyValue(1.0)
        val ORGANIC_COST = OrganicValue(1.0)

        fun of(genome: Genome): Leaf =
            Leaf(INIT_ENERGY, genome)
    }
}