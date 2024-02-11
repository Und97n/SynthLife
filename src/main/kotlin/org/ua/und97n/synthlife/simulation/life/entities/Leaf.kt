package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.PassiveEnergyProducer
import org.ua.und97n.synthlife.field.OrganicValue

class Leaf private constructor(
    initialEnergy: EnergyValue,
    genome: Genome
) : PassiveEnergyProducer(initialEnergy, genome) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.25)

    override val minimalEnergyToContain: EnergyValue
        get() = EnergyValue(1.0)

    override fun produceEnergy(): EnergyValue =
        EnergyValue(cellContext.sun.innerModel)

    companion object {
        val INIT_ENERGY = EnergyValue(1.0)
        val ORGANIC_COST = OrganicValue(1.0)

        fun of(genome: Genome): Leaf =
            Leaf(INIT_ENERGY, genome)
    }
}