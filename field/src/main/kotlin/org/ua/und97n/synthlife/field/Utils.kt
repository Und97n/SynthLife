package org.ua.und97n.synthlife.field

import kotlin.enums.EnumEntries

object Utils {
    fun Int.normalizeAsIndex(bound: Int): Int =
        ((this % bound) + bound) % bound

    fun<T: Enum<T>> EnumEntries<T>.getByIndexSafe(index: Int) =
        this[index.normalizeAsIndex(this.size)]
}