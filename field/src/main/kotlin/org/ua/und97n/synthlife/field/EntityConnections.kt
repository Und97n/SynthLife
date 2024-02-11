package org.ua.und97n.synthlife.field

@JvmInline
value class EntityConnections private constructor(
    private val data: Array<Entity?>
) {
    fun isEmpty(): Boolean =
        numberOfConnected() == 0

    fun numberOfConnected(): Int =
        data.count { it != null }

    fun getConnected(direction: Direction): Entity? = data[direction.ordinal]

    fun isConnectedTo(direction: Direction): Boolean = data[direction.ordinal] != null

    operator fun plus(more: EntityConnections): EntityConnections {
        val nw = this.copy()

        Direction.entries.forEach {
            val e = more.getConnected(it)

            if (e != null) {
                require(getConnected(it) == null) {
                    "Malformed entity connections sum"
                }

                nw.data[it.ordinal] = e
            }
        }

        return nw
    }

    fun refreshEntities() {
        Direction.entries.forEachIndexed { index, direction ->
            data[index] = data[index]?.replaceTo
        }
    }

    private fun copy(): EntityConnections =
        EntityConnections(this.data.copyOf())

    companion object {
        val NONE: EntityConnections = EntityConnections(Array(Direction.entries.size) { null })

        fun none(): EntityConnections =
            NONE

        fun ofSingle(direction: Direction, entity: Entity): EntityConnections {
            val array = Array<Entity?>(Direction.entries.size) { null }

            array[direction.ordinal] = entity

            return EntityConnections(array)
        }

        fun ofMany(lst: List<Pair<Direction, Entity>>): EntityConnections {
            val array = Array<Entity?>(Direction.entries.size) { null }

            lst.forEach {
                array[it.first.ordinal] = it.second
            }

            return EntityConnections(array)
        }
    }

}