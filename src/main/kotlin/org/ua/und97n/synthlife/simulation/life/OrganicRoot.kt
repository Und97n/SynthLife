package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.OrganicValue

class OrganicRoot(initialEnergy: EnergyValue) : PassiveEnergyProducer(initialEnergy) {

    override val baseEnergyConsumption: EnergyValue
        get() = EnergyValue(0.25)

    override val minimalEnergyToContain: EnergyValue
        get() = EnergyValue(1.0)

    override val aliveUnderCriticalOrganics: Boolean
        get() = true

    override fun produceEnergy(): EnergyValue =
        EnergyValue(cellContext.tryTakeOrganic(1.0).innerModel)

    companion object {
        val INIT_ENERGY = EnergyValue(1.0)
        val ORGANIC_COST = OrganicValue(1.5)
    }
}