package org.ua.und97n.synthlife.field

interface CellHandle {

    val sun: SunValue

    val minerals: MineralValue

    val organics: OrganicValue

    fun <T: WorldContext> getWorldContext(): T

    fun tryMove(direction: Direction): Boolean

    fun isDirectionAvailable(direction: Direction): Boolean

    fun spawnEntities(entities: List<Pair<Direction, Entity>>): Boolean

    /**
     *  Spread some minerals to nearby cells
     **/
    fun spreadMinerals(mineralValue: MineralValue)

    /**
     *  Spread some organics to nearby cells
     **/
    fun spreadOrganics(organicValue: OrganicValue)

    fun despawnEntity() = replaceEntityTo(null)

    fun replaceEntityTo(newEntity: Entity?)

    fun tryTakeMineral(flowRate: Double): MineralValue

    fun tryTakeOrganic(flowRate: Double): OrganicValue

    fun getEntity(direction: Direction): Entity?
}