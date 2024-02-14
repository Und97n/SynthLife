package org.ua.und97n.synthlife.field

@JvmInline
value class DirectionSet(private val data: Int) {

    fun connect(direction: Direction): DirectionSet =
        DirectionSet(data or (1 shl direction.ordinal))

    fun disconnect(direction: Direction): DirectionSet =
        DirectionSet(data and ((1 shl direction.ordinal).inv()))

    fun isConnected(direction: Direction): Boolean =
        (data and (1 shl direction.ordinal)) != 0

    fun numOfConnected(): Int =
        data.countOneBits()

    fun isEmpty(): Boolean =
        data == 0

    companion object {
        fun of(directions: Collection<Direction>): DirectionSet {
            var ret = DirectionSet(0)
            directions.forEach {
                ret = ret.connect(it)
            }

            return ret
        }
    }
}