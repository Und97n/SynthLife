package org.ua.und97n.synthlife.field

import kotlin.math.max
import kotlin.math.min

@JvmInline
value class OrganicValue(val innerModel: Double) {
    init {
        require(innerModel >= 0)
    }

    operator fun plus(value: OrganicValue): OrganicValue =
        OrganicValue(this.innerModel + value.innerModel)

    operator fun minus(value: OrganicValue): OrganicValue =
        OrganicValue(max(0.0, this.innerModel - value.innerModel))

    operator fun compareTo(value: OrganicValue): Int =
        this.innerModel.compareTo(value.innerModel)

    fun byFlowRate(flowRate: Double): OrganicValue {
        require(flowRate in 0.0..1.0)
        val limit = CRITICAL.innerModel*flowRate
        return OrganicValue(min(flowRate * innerModel, limit))
    }

    fun isCritical(): Boolean =
        this >= CRITICAL

    fun asUnsignedByte(): Int =
        min(255.0, 255.0*innerModel/ CRITICAL.innerModel).toInt()

    companion object {
        val CRITICAL = OrganicValue(50.0)
        val ZERO = OrganicValue(0.0)

        fun zero(): OrganicValue = ZERO

        fun Double.asOrganicValue(): OrganicValue =
            OrganicValue(this)
    }
}