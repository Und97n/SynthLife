package org.ua.und97n.org.ua.und97n.synthlife.simulation

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities.Bot
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.synthlife.field.Field
import org.ua.und97n.synthlife.field.Direction
import org.ua.und97n.synthlife.field.SunValue
import kotlin.random.Random.Default.nextBytes
import kotlin.random.Random.Default.nextInt

class World(
    width: Int,
    height: Int,
    val field: Field = Field(width, height, SunValue(2.0))
) {

    init {
//        val bot = Bot(
//            EnergyValue(40.0),
//            Direction.DOWN,
//
//            byteArrayOf(3, 4, 72, 5),
//        )
//        field.putEntity(5, 5, bot)
    }

    fun startSimulation() {
        while (true) {
            synchronized(this) {
                field.update()
            }

            Thread.sleep(1)
        }
    }

    fun putRandomBots() {
        synchronized(this) {
            for (i in (0..400)) {
                val b = Bot(
                    EnergyValue(40.0),
                    Direction.UP,
                    Genome.random()
                )

                for(aa in 0..100) {
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