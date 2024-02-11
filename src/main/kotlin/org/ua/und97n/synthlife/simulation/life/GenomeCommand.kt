package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.synthlife.field.Utils.getByIndexSafe

enum class GenomeCommand(val energy: EnergyValue, val timeUnits: Double) {
    NOOP(EnergyValue.ZERO, 1.0),
    MOVE(EnergyValue(0.5), 1.0),
    TURN_CLOCKWISE(EnergyValue(0.15), 1.0),
    TURN_COUNTER_CLOCKWISE(EnergyValue(0.15), 1.0),
    GROW(EnergyValue(0.5), Double.POSITIVE_INFINITY),
    RESTART_PROGRAM(EnergyValue.ZERO, 0.5),
    ;

    companion object {
        fun of(genome: Int): GenomeCommand =
            GenomeCommand.entries.getByIndexSafe(genome)
    }
}