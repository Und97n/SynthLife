package org.ua.und97n.synthlife.field

abstract class CellHandle {

    abstract val sun: SunValue

    abstract val minerals: MineralValue

    abstract val organics: OrganicValue

    abstract fun <T: WorldContext> getWorldContext(): T

    abstract fun tryMove(direction: Direction): Boolean

    abstract fun isDirectionAvailable(direction: Direction): Boolean

    abstract fun spawnEntities(entities: List<Pair<Direction, Entity>>): Boolean

    /**
     *  Spread some minerals to nearby cells
     **/
    abstract fun spreadMinerals(mineralValue: MineralValue)

    /**
     *  Spread some organics to nearby cells
     **/
    abstract fun spreadOrganics(organicValue: OrganicValue)

    abstract fun tryTakeMineral(flowRate: Double): MineralValue

    abstract fun tryTakeOrganic(flowRate: Double): OrganicValue

    abstract fun getEntity(direction: Direction): Entity?

    internal abstract fun replaceEntityTo(newEntity: Entity?)
}