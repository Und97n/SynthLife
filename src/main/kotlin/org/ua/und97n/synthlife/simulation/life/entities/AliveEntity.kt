package org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities

import org.ua.und97n.org.ua.und97n.synthlife.simulation.WorldContextImpl
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.DeathReason
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.synthlife.field.CellHandle
import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.Entity
import org.ua.und97n.synthlife.field.MineralValue

abstract class AliveEntity(
    initialEnergy: EnergyValue,
    initialGenome: Genome,
) : Entity() {
    var energy: EnergyValue = initialEnergy
        protected set

    val genome: Genome = initialGenome

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

    abstract fun updateAliveEntity(cellHandle: CellHandle)

    final override fun update(cellHandle: CellHandle) {
        energy -= baseEnergyConsumption

        if (energy > maxEnergy) {
            val diff = energy - maxEnergy
            energy -= diff
            cellHandle.spreadMinerals(MineralValue(diff.innerModel))
        }

        when {
            energy.isZero() -> die(cellHandle, DeathReason.NO_ENERGY)
            cellHandle.sun.isCritical() && aliveUnderCriticalSun.not() ->
                die(cellHandle, DeathReason.TOO_MUCH_SUN)

            cellHandle.minerals.isCritical() && aliveUnderCriticalMinerals.not() ->
                die(cellHandle, DeathReason.TOO_MUCH_MINERALS)

            cellHandle.organics.isCritical() && aliveUnderCriticalOrganics.not() ->
                die(cellHandle, DeathReason.TOO_MUCH_ORGANICS)

            else -> updateAliveEntity(cellHandle)
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

    protected fun die(cellHandle: CellHandle, deathReason: DeathReason) {
        cellHandle.getWorldContext<WorldContextImpl>().registerDeath(deathReason, this)

        cellHandle.spreadOrganics(Bot.ORGANIC_COST)
        cellHandle.despawnEntity()
    }

    override fun toString(): String =
        "${this.javaClass.simpleName}(energy=$energy)"
}