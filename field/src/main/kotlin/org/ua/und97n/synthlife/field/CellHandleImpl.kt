package org.ua.und97n.synthlife.field

internal data class CellHandleImpl(
    val x: Int,
    val y: Int,
    override val sun: SunValue,

    val initialMineral: MineralValue,
    val initialOrganic: OrganicValue,

    val entity: Entity,
    val field: Field,
    var newX: Int = x,
    var newY: Int = y,
    var nextEntity: Entity? = entity,
    var nextMineralValue: MineralValue = initialMineral,
    var nextOrganicValue: OrganicValue = initialOrganic,

    var mineralToSpread: MineralValue? = null,
    var organicToSpread: OrganicValue? = null,

    var toSpawn: MutableList<Triple<Int, Int, Entity>>? = null, // do not create list if not needed
) : CellHandle {

    override val minerals: MineralValue
        get() = nextMineralValue

    override val organics: OrganicValue
        get() = nextOrganicValue

    @Suppress("UNCHECKED_CAST")
    override fun <T : WorldContext> getWorldContext(): T =
        field.worldContext as T

    override fun tryMove(direction: Direction): Boolean {
        if (isDirectionAvailable(direction)) {
            this.newX = direction.moveX(x)
            this.newY = direction.moveY(y)

            return true
        } else {
            return false
        }
    }

    override fun replaceEntityTo(newEntity: Entity?) {
        this.entity.replaceTo = newEntity
        this.nextEntity = newEntity
    }

    override fun isDirectionAvailable(direction: Direction): Boolean =
        field.isCellFree(direction.moveX(x), direction.moveY(y))

    override fun spawnEntities(entities: List<Pair<Direction, Entity>>): Boolean {
        if (entities.all { isDirectionAvailable(it.first) }) {
            // consider synchronization is not needed here
            if (toSpawn == null) {
                toSpawn = ArrayList(4)
            }

            entities.forEach {
                toSpawn!!.add(
                    Triple(
                        it.first.moveX(x),
                        it.first.moveY(y),
                        it.second,
                    )
                )
            }

            return true
        } else {
            return false
        }
    }

    override fun getEntity(direction: Direction): Entity? =
        field.getEntity(direction.moveX(x), direction.moveY(y))

    override fun tryTakeMineral(flowRate: Double): MineralValue {
        val amount = nextMineralValue.byFlowRate(flowRate)
        nextMineralValue -= amount
        return amount
    }

    override fun tryTakeOrganic(flowRate: Double): OrganicValue {
        val amount = nextOrganicValue.byFlowRate(flowRate)
        nextOrganicValue -= amount
        return amount
    }

    override fun spreadMinerals(mineralValue: MineralValue) {
        mineralToSpread = (mineralToSpread ?: MineralValue.ZERO) + mineralValue
    }

    override fun spreadOrganics(organicValue: OrganicValue) {
        organicToSpread = (organicToSpread ?: OrganicValue.ZERO) + organicValue
    }
}