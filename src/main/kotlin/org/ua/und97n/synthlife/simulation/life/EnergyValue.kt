package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.MineralValue
import org.ua.und97n.synthlife.field.OrganicValue
import org.ua.und97n.synthlife.field.SunValue
import kotlin.math.E
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@JvmInline
value class EnergyValue(val innerModel: Double) {

    operator fun plus(value: EnergyValue): EnergyValue =
        EnergyValue(this.innerModel + value.innerModel)

    operator fun minus(value: EnergyValue): EnergyValue =
        EnergyValue(max(0.0, this.innerModel - value.innerModel))

    operator fun compareTo(value: EnergyValue): Int =
        this.innerModel.compareTo(value.innerModel)

    fun splitBy(divisor: Double): EnergyValue =
        EnergyValue(innerModel / divisor)

    fun convertToOrganic(): OrganicValue =
        OrganicValue(this.innerModel)

    fun convertToMinerals(): MineralValue =
        MineralValue(this.innerModel)

    fun isZero(): Boolean =
        innerModel == 0.0

    fun asUnsignedByte(): Int =
        min(255.0, 255.0 * sqrt(innerModel / CRITICAL.innerModel)).toInt()

    override fun toString(): String = innerModel.toString()

    companion object {
        val ZERO = EnergyValue(0.0)
        val CRITICAL = EnergyValue(200.0)

        fun fromOrganic(organicValue: OrganicValue): EnergyValue =
            EnergyValue(organicValue.innerModel*1)

        fun fromMinerals(mineralValue: MineralValue): EnergyValue =
            EnergyValue(mineralValue.innerModel*1)

        fun fromSun(sunValue: SunValue): EnergyValue =
            EnergyValue(sunValue.innerModel)
    }
}