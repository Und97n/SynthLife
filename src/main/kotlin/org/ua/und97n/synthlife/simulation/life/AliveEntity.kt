package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.Entity
import org.ua.und97n.synthlife.field.MineralValue

abstract class AliveEntity(
    initialEnergy: EnergyValue
) : Entity() {
    var energy: EnergyValue = initialEnergy
        protected set

    abstract val baseEnergyConsumption: EnergyValue

    open val maxEnergy: EnergyValue
        get() = EnergyValue.CRITICAL

    open val aliveUnderCriticalSun: Boolean
        get() = false

    open val aliveUnderCriticalMinerals: Boolean
        get() = false

    open val aliveUnderCriticalOrganics: Boolean
        get() = false

    open fun canAbsorbEnergyFromConnection(direction: Direction): Boolean = true

    /**
     * Return consumed amount.
     */
    fun enrichWithEnergyFromConnection(direction: Direction, energyValue: EnergyValue): EnergyValue {
        if (canAbsorbEnergyFromConnection(direction)) {
            energy += energyValue
            return energyValue
        } else {
            return EnergyValue.ZERO
        }
    }

    abstract fun updateAliveEntity()

    final override fun update() {
        energy -= baseEnergyConsumption

        if (energy > maxEnergy) {
            val diff = energy - maxEnergy
            energy -= diff
            cellContext.spreadMinerals(MineralValue(diff.innerModel))
        }

        when {
            energy.isZero() -> die(DeathReason.NO_ENERGY)
            cellContext.sun.isCritical() && aliveUnderCriticalSun.not() ->
                die(DeathReason.TOO_MUCH_SUN)

            cellContext.minerals.isCritical() && aliveUnderCriticalMinerals.not() ->
                die(DeathReason.TOO_MUCH_MINERALS)

            cellContext.organics.isCritical() && aliveUnderCriticalOrganics.not() ->
                die(DeathReason.TOO_MUCH_ORGANICS)

            else -> updateAliveEntity()
        }
    }

    protected fun trySpendEnergy(amount: EnergyValue): Boolean =
        if (this.energy <= amount) {
            false
        } else {
            this.energy -= amount
            true
        }

    protected fun spendEnergy(amount: EnergyValue): Boolean {
        this.energy -= amount
        return this.energy.isZero()
    }

    protected fun die(deathReason: DeathReason) {
        cellContext.spreadOrganics(Bot.ORGANIC_COST)
        cellContext.despawnEntity()
    }

    override fun toString(): String =
        "${this.javaClass.simpleName}(energy=$energy)"
}