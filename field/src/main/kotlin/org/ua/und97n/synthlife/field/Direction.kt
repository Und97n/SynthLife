package org.ua.und97n.synthlife.field

import org.ua.und97n.synthlife.field.Utils.getByIndexSafe

enum class Direction(private val xDiff: Int, private val yDiff: Int) {
//    UP_LEFT(-1,1 -1),
    UP(0, -1),
//    UP_RIGHT(+1, -1),
    RIGHT(+1, 0),
//    DOWN_RIGHT(+1, +1),
    DOWN(0, +1),
//    DOWN_LEFT(-1, +1),
    LEFT(-1, 0),
    ;

    operator fun plus(v: Int): Direction =
        Direction.entries.getByIndexSafe(this.ordinal+v)

    operator fun minus(v: Int): Direction =
        Direction.entries.getByIndexSafe(this.ordinal-v)

    fun mirror(): Direction =
        when (this) {
            UP -> DOWN
            DOWN -> UP
            RIGHT -> LEFT
            LEFT -> RIGHT
        }

    fun moveX(x: Int): Int = x + xDiff

    fun moveY(y: Int): Int = y + yDiff
}