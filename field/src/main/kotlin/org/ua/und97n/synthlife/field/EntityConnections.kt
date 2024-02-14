package org.ua.und97n.synthlife.field

class EntityConnections private constructor(
    var up: Entity?,
    var right: Entity?,
    var down: Entity?,
    var left: Entity?,
) {
    inline fun iterate(crossinline iterator: (Entity?) -> Unit) {
        iterator(up)
        iterator(right)
        iterator(down)
        iterator(left)
    }

    inline fun iterateExistent(crossinline iterator: (Entity, Direction) -> Unit) {
        up?.also { iterator(it, Direction.UP) }
        right?.also { iterator(it, Direction.RIGHT) }
        down?.also { iterator(it, Direction.DOWN) }
        left?.also { iterator(it, Direction.LEFT) }
    }

    fun isEmpty(): Boolean =
        numberOfConnected() == 0

    fun numberOfConnected(): Int {
        var ret = 0
        iterateExistent { _, _ -> ret++ }
        return ret
    }

    fun clear() {
        up = null
        right = null
        down = null
        left = null
    }

    internal fun getConnected(direction: Direction): Entity? =
        when (direction) {
            Direction.UP -> up
            Direction.RIGHT -> right
            Direction.DOWN -> down
            Direction.LEFT -> left
        }

    private fun setConnected(direction: Direction, entity: Entity?) {
        when (direction) {
            Direction.UP -> up = entity
            Direction.RIGHT -> right = entity
            Direction.DOWN -> down = entity
            Direction.LEFT -> left = entity
        }
    }

    fun clearConnected(direction: Direction) {
        setConnected(direction, null)
    }

    fun isConnectedTo(direction: Direction): Boolean = getConnected(direction) != null

    operator fun plus(more: EntityConnections): EntityConnections {
        val nw = this.copy()

        Direction.entries.forEach {
            val e = more.getConnected(it)

            if (e != null) {
                require(getConnected(it) == null) {
                    "Malformed entity connections sum"
                }

                nw.setConnected(it, e)
            }
        }

        return nw
    }

    fun refreshEntities() {
        up = up?.replaceTo
        right = right?.replaceTo
        down = down?.replaceTo
        left = left?.replaceTo
    }

    private fun copy(): EntityConnections =
        EntityConnections(up, right, down, left)

    companion object {
        fun none(): EntityConnections =
            EntityConnections(null, null, null, null)

        fun ofSingle(direction: Direction, entity: Entity): EntityConnections =
            none().apply {
                setConnected(direction, entity)
            }

        fun ofMany(lst: List<Pair<Direction, Entity>>): EntityConnections =
            none().apply {
                lst.forEach {
                    setConnected(it.first, it.second)
                }
            }
    }

}