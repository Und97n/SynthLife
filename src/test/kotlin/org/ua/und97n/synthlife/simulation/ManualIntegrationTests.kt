package org.ua.und97n.synthlife.simulation

import org.junit.jupiter.api.Test
import org.ua.und97n.org.ua.und97n.synthlife.render.javaswing.JavaSwingRenderer
import org.ua.und97n.org.ua.und97n.synthlife.simulation.World
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.EnergyValue
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.Genome
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.GenomeCommand
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities.Bot
import org.ua.und97n.synthlife.field.Direction

class ManualIntegrationTests {
    @Test
    fun simpleMovementTest() {
        val bot = Bot(
            EnergyValue.CRITICAL,
            Direction.RIGHT,
            Genome.of(
                GenomeCommand.MOVE, GenomeCommand.MOVE, GenomeCommand.MOVE, GenomeCommand.MOVE, GenomeCommand.MOVE,
                GenomeCommand.TURN_CLOCKWISE, GenomeCommand.MOVE, GenomeCommand.TURN_CLOCKWISE,
                GenomeCommand.MOVE, GenomeCommand.MOVE, GenomeCommand.MOVE, GenomeCommand.MOVE, GenomeCommand.MOVE,
                GenomeCommand.TURN_COUNTER_CLOCKWISE, GenomeCommand.MOVE, GenomeCommand.TURN_COUNTER_CLOCKWISE,
                GenomeCommand.RESTART_PROGRAM
            )
        )


        val world = World(10, 10)
        world.putEntity(0, 0, bot)
        world.context.tickDelay = 200
        world.context.mutationProbability = 0.0

        val renderer = JavaSwingRenderer()
        renderer.start(world)
        world.startSimulation()
    }

    @Test
    fun snakeGrowTest() {
        val bot = Bot(
            EnergyValue.CRITICAL,
            Direction.RIGHT,
            Genome.of(
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.MOVE, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.MOVE, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.TURN_CLOCKWISE,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.TURN_CLOCKWISE,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.MOVE, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.MOVE, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.TURN_COUNTER_CLOCKWISE,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.GROW, GenomeCommand.NOOP, GenomeCommand.NOOP,
                GenomeCommand.TURN_COUNTER_CLOCKWISE,
                GenomeCommand.RESTART_PROGRAM
            )
        )

        val world = World(10, 10)
        world.putEntity(0, 1, bot)
        world.context.tickDelay = 200
        world.context.mutationProbability = 0.0

        val renderer = JavaSwingRenderer()
        renderer.start(world)
        world.startSimulation()
    }

    @Test
    fun splitTest() {
        val bot = Bot(
            EnergyValue(90.0),
            Direction.DOWN,
            Genome.of(
                GenomeCommand.GROW.ordinal, 145,
                GenomeCommand.NOOP.ordinal,
                GenomeCommand.GROW.ordinal, 0,
                GenomeCommand.NOOP.ordinal,
                GenomeCommand.RESTART_PROGRAM.ordinal
            )
        )

        val world = World(10, 20)
        world.putEntity(5, 1, bot)
        world.context.tickDelay = 200
        world.context.mutationProbability = 0.0

        val renderer = JavaSwingRenderer()
        renderer.start(world)
        world.startSimulation()
    }

    @Test
    fun overflowTest() {
        val bot = Bot(
            EnergyValue.CRITICAL,
            Direction.DOWN,
            Genome.of(
                GenomeCommand.GROW.ordinal, 129,
                GenomeCommand.GROW.ordinal, 129,
                GenomeCommand.NOOP.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
                GenomeCommand.MOVE.ordinal,
            )
        )

        val world = World(10, 10)
        world.putEntity(5, 2, bot)
        world.context.tickDelay = 200
        world.context.mutationProbability = 0.0

        val renderer = JavaSwingRenderer()
        renderer.start(world)
        world.startSimulation()
    }
}