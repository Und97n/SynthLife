package org.ua.und97n.synthlife.field

/**
 * Just a syntactic sugar.
 */
@JvmInline
value class CellContext internal constructor(private val updateContext: EntityUpdateContext) {
    val sun: SunValue
        get() = updateContext.sun

    val minerals: MineralValue
        get() = updateContext.nextMineralValue

    val organics: OrganicValue
        get() = updateContext.nextOrganicValue

    fun tryMove(direction: Direction): Boolean =
        updateContext.entity.connections.isEmpty() && updateContext.tryMoveTo(direction)

    fun isDirectionAvailable(direction: Direction): Boolean =
        updateContext.isDirectionAvailable(direction)

    fun spawnEntities(entities: List<Pair<Direction, Entity>>): Boolean =
        updateContext.putEntitiesToSpawn(entities)

    /**
     *  Spread some minerals to nearby cells
     **/
    fun spreadMinerals(mineralValue: MineralValue) {
        updateContext.spreadMinerals(mineralValue)
    }

    /**
     *  Spread some organics to nearby cells
     **/
    fun spreadOrganics(organicValue: OrganicValue) {
        updateContext.spreadOrganics(organicValue)
    }

    fun despawnEntity() {
        replaceEntityTo(null)
    }

    fun replaceEntityTo(newEntity: Entity?) {
        updateContext.entity.replaceTo = newEntity
        updateContext.replaceEntity(newEntity)
    }

    fun tryTakeMineral(flowRate: Double): MineralValue =
        updateContext.tryTakeMineral(flowRate)

    fun tryTakeOrganic(flowRate: Double): OrganicValue =
        updateContext.tryTakeOrganic(flowRate)

    fun getEntity(direction: Direction): Entity? =
        updateContext.field.getEntity(direction.moveX(updateContext.x), direction.moveY(updateContext.y))
}
