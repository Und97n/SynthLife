package org.ua.und97n.org.ua.und97n.synthlife.simulation

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities.Bot
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.synthlife.field.Field
import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.Entity
import org.ua.und97n.synthlife.field.SunValue
import kotlin.math.abs
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

class World(
    width: Int,
    height: Int,
    sunOverride: SunValue? = null,
    val context: WorldContextImpl = WorldContextImpl(),
    val field: Field = Field(width, height, context),
    var actualTps: Double? = null,
) {
    init {
        val a = PerlinNoise(nextDouble())

        field.iterateAll { x, y, _, _, _, _ ->

            val sun = sunOverride ?: SunValue(1 + 1 * abs(a.noise(x.toDouble(), y.toDouble())))
            field.setSun(x, y, sun)
        }
    }

    fun putEntity(x: Int, y: Int, entity: Entity) {
        synchronized(this) {
            if (field.isCellFree(x, y)) {
                field.putEntity(x, y, entity)
            } else {
                error("Cannot place $entity to $x,$y")
            }
        }
    }

    fun startSimulation() {
        var lastRpsMeasure = System.nanoTime()
        var tickCounter = 0L

        val oneSecond = 1_000_000_000.0

        while (true) {
            tickCounter++

            synchronized(this) {
                if (context.pause.not()) {
                    field.update()
                } else {
                    Thread.sleep(100)
                }
            }

            Thread.sleep(context.tickDelay)

            val currentTime = System.nanoTime()

            if (currentTime - lastRpsMeasure >= oneSecond) {
                actualTps = oneSecond * tickCounter / (currentTime - lastRpsMeasure)
                lastRpsMeasure = currentTime
                tickCounter = 0
            }
        }
    }

    fun putRandomBots() {
        synchronized(this) {
            for (i in (0..(field.width * field.height / 100))) {
                val b = Bot(
                    EnergyValue(40.0),
                    Direction.UP,
                    Genome.random(),
                )

                for (aa in 0..100) {
                    val x = nextInt()
                    val y = nextInt()
                    if (field.getEntity(x, y) == null) {
                        field.putEntity(x, y, b)
                        break
                    }
                }
            }
        }
    }
}