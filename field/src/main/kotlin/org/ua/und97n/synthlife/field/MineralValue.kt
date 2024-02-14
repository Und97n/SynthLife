package org.ua.und97n.synthlife.field

import kotlin.math.max
import kotlin.math.min

@JvmInline
value class MineralValue(val innerModel: Double) {
    init {
        require(innerModel >= 0)
    }

    operator fun plus(value: MineralValue): MineralValue =
        MineralValue(this.innerModel + value.innerModel)

    operator fun minus(value: MineralValue): MineralValue =
        MineralValue(max(0.0, this.innerModel - value.innerModel))

    operator fun compareTo(value: MineralValue): Int =
        this.innerModel.compareTo(value.innerModel)

    fun byFlowRate(flowRate: Double): MineralValue {
        require(flowRate in 0.0..1.0)
        val limit = CRITICAL.innerModel*flowRate
        return MineralValue(min(limit, flowRate*innerModel))
    }

    fun update(): MineralValue =
        MineralValue(innerModel + (TARGET_MINERAL_VALUE.innerModel - innerModel)/1000.0)

    fun isCritical(): Boolean =
        this >= CRITICAL

    fun asUnsignedByte(): Int =
        min(255.0, 255.0*innerModel/ CRITICAL.innerModel).toInt()

    companion object {
        val TARGET_MINERAL_VALUE = MineralValue(1.0)

        val CRITICAL = MineralValue(50.0)
        val ZERO = MineralValue(0.0)

        fun zero(): MineralValue = ZERO

        fun Double.asMineralValue(): MineralValue =
            MineralValue(this)
    }
}