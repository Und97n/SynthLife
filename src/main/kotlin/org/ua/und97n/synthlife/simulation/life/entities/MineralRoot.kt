package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.PassiveEnergyProducer
import org.ua.und97n.synthlife.field.CellHandle
import org.ua.und97n.synthlife.field.OrganicValue

class MineralRoot private constructor(
    initialEnergy: EnergyValue,
    genome: Genome,
) : PassiveEnergyProducer(initialEnergy, genome) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.25)

    override val minimalEnergyToContain: EnergyValue
        get() = EnergyValue(1.0)

    override val aliveUnderCriticalOrganics: Boolean
        get() = true

    override val aliveUnderCriticalMinerals: Boolean
        get() = true

    override val organicCost: OrganicValue
        get() = ORGANIC_COST

    override fun produceEnergy(cellHandle: CellHandle): EnergyValue =
        EnergyValue.fromMinerals(cellHandle.tryTakeMineral(0.1))

    companion object {
        val INIT_ENERGY = EnergyValue(1.0)
        val ORGANIC_COST = OrganicValue(1.5)

        fun of(genome: Genome): MineralRoot =
            MineralRoot(INIT_ENERGY, genome)
    }
}