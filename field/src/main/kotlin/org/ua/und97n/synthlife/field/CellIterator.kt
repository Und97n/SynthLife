package org.ua.und97n.synthlife.field

fun interface CellIterator {
    fun execute(x: Int, y: Int, entity: Entity?, sun: SunValue, mineral: MineralValue, organic: OrganicValue)
}