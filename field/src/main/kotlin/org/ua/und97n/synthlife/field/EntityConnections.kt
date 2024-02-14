package org.ua.und97n.synthlife.field

class EntityConnections private constructor(
    var up: Entity?,
    var right: Entity?,
    var down: Entity?,
    var left: Entity?,
) {
    inline fun iterateEmpty(crossinline iterator: (Direction) -> Unit) {
        if (up == null) iterator(Direction.UP)
        if (right == null) iterator(Direction.RIGHT)
        if (down == null) iterator(Direction.DOWN)
        if (left == null) iterator(Direction.LEFT)
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

    internal fun clear() {
        up = null
        right = null
        down = null
        left = null
    }

    internal inline fun getConnected(direction: Direction): Entity? =
        when (direction) {
            Direction.UP -> up
            Direction.RIGHT -> right
            Direction.DOWN -> down
            Direction.LEFT -> left
        }

    internal inline fun setConnected(direction: Direction, entity: Entity?) {
        when (direction) {
            Direction.UP -> up = entity
            Direction.RIGHT -> right = entity
            Direction.DOWN -> down = entity
            Direction.LEFT -> left = entity
        }
    }

    internal fun connect(direction: Direction, entity: Entity) {
        when (direction) {
            Direction.UP -> {
                require(up == null); up = entity
            }

            Direction.RIGHT -> {
                require(right == null); right = entity
            }

            Direction.DOWN -> {
                require(down == null); down = entity
            }

            Direction.LEFT -> {
                require(left == null); left = entity
            }
        }
    }

    fun disconnect(direction: Direction) {
        setConnected(direction, null)
    }

    fun isConnectedTo(direction: Direction): Boolean = getConnected(direction) != null

    fun intersectWith(other: EntityConnections) {
        if (other.up == null) this.up = null else require(this.up == null || this.up == other.up)
        if (other.right == null) this.right = null else require(this.right == null || this.right == other.right)
        if (other.down == null) this.down = null else require(this.down == null || this.down == other.down)
        if (other.left == null) this.left = null else require(this.left == null || this.left == other.left)
    }

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