package org.ua.und97n.synthlife.field

import org.ua.und97n.synthlife.field.MineralValue.Companion.asMineralValue
import org.ua.und97n.synthlife.field.OrganicValue.Companion.asOrganicValue
import org.ua.und97n.synthlife.field.SunValue.Companion.asSunValue
import org.ua.und97n.synthlife.field.Utils.normalizeAsIndex
import java.util.*

class Field(
    val width: Int,
    val height: Int,
    defaultSun: SunValue,
) {
    private val sun: DoubleArray = DoubleArray(width * height) { defaultSun.innerModel }
    private var mineral: DoubleArray = DoubleArray(width * height) { 0.0 }
    private var organic: DoubleArray = DoubleArray(width * height) { 0.0 }
    private var entities: Array<Entity?> = Array(width * height) { null }

    private var mineralNext: DoubleArray = DoubleArray(width * height) { 0.0 }
    private var organicNext: DoubleArray = DoubleArray(width * height) { 0.0 }
    private var entitiesNext: Array<Entity?> = Array(width * height) { null }

    fun getEntity(x: Int, y: Int): Entity? =
        entities[getIndex(x, y)]

    fun isCellFree(x: Int, y: Int): Boolean {
        val index = getIndex(x, y)
        return entities[index] == null && entitiesNext[index] == null
    }

    /*

       fun getSun(x: Int, y: Int): SunValue =
           sun[getIndex(x, y)].asSunValue()

       fun getMineral(x: Int, y: Int): MineralValue =
           mineral[getIndex(x, y)].asMineralValue()

       fun getOrganic(x: Int, y: Int): OrganicValue =
           organic[getIndex(x, y)].asOrganicValue()
   */

    fun putEntity(x: Int, y: Int, entity: Entity) {
        entities[getIndex(x, y)] = entity
    }

    fun iterateAll(action: CellIterator) {
        iterateOverField { x, y ->
            val index = getIndex(x, y)

            action.execute(
                x = x,
                y = y,
                entity = entities[index],
                sun = sun[index].asSunValue(),
                mineral = mineral[index].asMineralValue(),
                organic = organic[index].asOrganicValue()
            )
        }
    }

    fun update() {
        for (y in (0 until height)) {
            for (x in (0 until width)) {
                val index = getIndex(x, y)

                val currentEntity = entities[index]
                val currentSun = sun[index].asSunValue()
                val currentMinerals = mineral[index].asMineralValue()
                val currentOrganic = organic[index].asOrganicValue()

                if (currentEntity != null) {
//                    println("Going to update $currentEntity")

                    val updateContext = EntityUpdateContext(
                        x = x,
                        y = y,
                        sun = currentSun,
                        mineral = currentMinerals,
                        organic = currentOrganic,
                        field = this,
                        entity = currentEntity,
                    )

                    currentEntity.updateInternal(updateContext)

                    // Yes. Plus. Because plus is atomic.
                    mineralNext[index] += updateContext.nextMineralValue.innerModel
                    organicNext[index] += updateContext.nextOrganicValue.innerModel

                    updateContext.mineralToSpread?.let {
                        val toAdd = it.innerModel / 9.0
                        iterateAround(x, y, 1) { xx, yy ->
                            mineralNext[getIndex(xx, yy)] += toAdd
                        }
                    }

                    updateContext.organicToSpread?.let {
                        val toAdd = it.innerModel / 9.0
                        iterateAround(x, y, 1) { xx, yy ->
                            organicNext[getIndex(xx, yy)] += toAdd
                        }
                    }

                    val movedToIndex = getIndex(updateContext.newX, updateContext.newY)

                    val nextEntity = updateContext.nextEntity

                    if (nextEntity != null) {
                        if (movedToIndex != index) {
                            synchronized(this) {
                                tryPlaceEntity(updateContext.newX, updateContext.newY, nextEntity)
                            }
                        } else {
                            entitiesNext[index] = updateContext.nextEntity
                        }
                    }

                    val toSpawn = updateContext.toSpawn

                    if (toSpawn?.isNotEmpty() == true) {
                        synchronized(this) {
                            toSpawn.forEach {
                                tryPlaceEntity(it.first, it.second, it.third)
                            }
                        }
                    }
                } else {
                    mineralNext[index] += currentMinerals.innerModel
                    organicNext[index] += currentOrganic.innerModel
                }
            }
        }

        swapBuffers()
    }

    private fun tryPlaceEntity(
        x: Int,
        y: Int,
        entity: Entity,
        fallbackX: Int? = null,
        fallbackY: Int? = null,
    ) {
        val index = getIndex(x, y)
        if (entitiesNext[index] != null) {
            if (fallbackX == null || fallbackY == null) {
                error("Cannot place entity: $entity")
            } else {
                tryPlaceEntity(fallbackX, fallbackY, entity)
            }
        } else {
            entitiesNext[index] = entity
        }
    }

    private fun swapBuffers() {
        val t = this.entities
        this.entities = this.entitiesNext
        this.entitiesNext = t
        Arrays.fill(t, null)

        val tt = this.mineral
        this.mineral = this.mineralNext
        this.mineralNext = tt
        Arrays.fill(tt, 0.0)

        val ttt = this.organic
        this.organic = this.organicNext
        this.organicNext = ttt
        Arrays.fill(ttt, 0.0)

        iterateOverField { x, y ->
            val index = getIndex(x, y)
            mineral[index] = MineralValue(mineral[index]).update().innerModel
        }
    }

    private inline fun iterateOverField(action: (Int, Int) -> Unit) {
        for (y in (0 until height)) {
            for (x in (0 until width)) {
                action(x, y)
            }
        }
    }

    private inline fun iterateAround(x: Int, y: Int, range: Int, action: (Int, Int) -> Unit) {
        for (yy in (y - 1..y + 1)) {
            for (xx in (x - 1..x + 1)) {
                action(xx, yy)
            }
        }
    }

    private inline fun getIndex(x: Int, y: Int): Int =
        x.normalizeAsIndex(width) + y.normalizeAsIndex(height) * width
}