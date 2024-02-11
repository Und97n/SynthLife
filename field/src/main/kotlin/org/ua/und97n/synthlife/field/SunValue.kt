package org.ua.und97n.synthlife.field

import kotlin.math.min

@JvmInline
value class SunValue(val innerModel: Double) {
    init {
        require(innerModel >= 0)
    }

    operator fun compareTo(value: SunValue): Int =
        this.innerModel.compareTo(value.innerModel)

    fun isCritical(): Boolean =
        this >= CRITICAL

    fun asUnsignedByte(): Int =
        min(255.0, 255.0*innerModel/ CRITICAL.innerModel).toInt()

    companion object {
        val CRITICAL = SunValue(10.0)

        fun Double.asSunValue(): SunValue =
            SunValue(this)
    }
}