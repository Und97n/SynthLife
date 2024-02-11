package org.ua.und97n.org.ua.und97n.synthlife.simulation.life

import org.ua.und97n.org.ua.und97n.synthlife.simulation.WorldContextImpl
import org.ua.und97n.synthlife.field.Utils.normalizeAsIndex
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class Genome private constructor(
    private val genome: ByteArray,
    private val iterationNum: Long,
    private val identifier: Long,
) {
    operator fun get(index: Int): Int =
        genome[index.normalizeAsIndex(genome.size)].toUByte().toInt()

    fun produceChild(worldContextImpl: WorldContextImpl, random: Random = Random.Default): Genome =
        if (random.nextDouble(0.0, 1.0) < worldContextImpl.mutationProbability) {
            mutate(random)
        } else {
            this
        }

    private fun mutate(random: Random = Random.Default): Genome {
        val genome = this.genome.copyOf()
        genome[random.nextInt(0, genome.size)] = random.nextInt().toByte()

        return Genome(
            genome = genome,
            iterationNum = iterationNum + 1,
            identifier = identifier+1
        )
    }

    fun isInRelationTo(genome: Genome): Boolean =
        abs(this.identifier - genome.identifier) < 3

    override fun hashCode(): Int = identifier.toInt()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Genome

        if (!genome.contentEquals(other.genome)) return false
        if (iterationNum != other.iterationNum) return false
        if (identifier != other.identifier) return false

        return true
    }

    @kotlin.ExperimentalStdlibApi
    override fun toString(): String =
        "Genome(id=${identifier.toHexString()}, iteration=$iterationNum)"

    companion object {
        const val GENOME_SIZE = 64

        fun of(vararg commands: Int): Genome =
            Genome(
                genome = commands.map { it.toByte() }.toByteArray(),
                iterationNum = 0,
                identifier = Arrays.hashCode(commands).toLong(),
            )

        fun of(vararg commands: GenomeCommand): Genome =
            Genome(
                genome = commands.map { it.ordinal.toByte() }.toByteArray(),
                iterationNum = 0,
                identifier = Arrays.hashCode(commands).toLong(),
            )

        fun random(random: Random = Random.Default): Genome {
            val array = random.nextBytes(GENOME_SIZE)

            return Genome(
                genome = array,
                iterationNum = 0,
                identifier = random.nextLong(),
            )
        }
    }
}