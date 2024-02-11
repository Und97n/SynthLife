package org.ua.und97n.synthlife.field

internal data class EntityUpdateContext(
    val x: Int,
    val y: Int,
    val sun: SunValue,

    val mineral: MineralValue,
    val organic: OrganicValue,

    val entity: Entity,
    val field: Field,
    var newX: Int = x,
    var newY: Int = y,
    var nextEntity: Entity? = entity,
    var nextMineralValue: MineralValue = mineral,
    var nextOrganicValue: OrganicValue = organic,

    var mineralToSpread: MineralValue? = null,
    var organicToSpread: OrganicValue? = null,

    var toSpawn: MutableList<Triple<Int, Int, Entity>>? = null, // do not create list if not needed
) {

    fun tryMoveTo(direction: Direction): Boolean {
        if (isDirectionAvailable(direction)) {
            this.newX = direction.moveX(x)
            this.newY = direction.moveY(y)

            return true
        } else {
            return false
        }
    }

    fun replaceEntity(next: Entity?) {
        nextEntity = next
    }

    fun isDirectionAvailable(direction: Direction): Boolean =
        field.isCellFree(direction.moveX(x), direction.moveY(y))

    fun putEntitiesToSpawn(entities: List<Pair<Direction, Entity>>): Boolean {
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

    fun tryTakeMineral(flowRate: Double): MineralValue {
        val amount = nextMineralValue.byFlowRate(flowRate)
        nextMineralValue -= amount
        return amount
    }

    fun tryTakeOrganic(flowRate: Double): OrganicValue {
        val amount = nextOrganicValue.byFlowRate(flowRate)
        nextOrganicValue -= amount
        return amount
    }

    fun spreadMinerals(amount: MineralValue) {
        mineralToSpread = (mineralToSpread ?: MineralValue.ZERO) + amount
    }

    fun spreadOrganics(amount: OrganicValue) {
        organicToSpread = (organicToSpread ?: OrganicValue.ZERO) + amount
    }
}