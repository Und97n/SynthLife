package org.ua.und97n.org.ua.und97n.synthlife.render.javaswing

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.PassiveEnergyProducer
import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.entities.*
import org.ua.und97n.synthlife.field.*
import java.awt.Color
import java.awt.Graphics2D

data class CustomGraphics(
    private val gg: Graphics2D,
    private val canvasWidth: Int,
    private val canvasHeight: Int,

    private val renderMode: RenderMode,

    private val offsetX: Double = 0.0,
    private val offsetY: Double = 0.0,
    private val scaleX: Double = 1.0,
    private val scaleY: Double = 1.0,
) {

    fun clear() {
        when (renderMode) {
            RenderMode.ENERGY -> gg.color = Color.BLACK
            else -> gg.color = Color.WHITE
        }
        gg.drawRect(0, 0, canvasWidth, canvasHeight)
    }

    fun drawCell(x: Int, y: Int, sunValue: SunValue, mineralValue: MineralValue, organicValue: OrganicValue) {
        gg.color = resolveCellColor(sunValue, mineralValue, organicValue)
        gg.fillRect(x.toScreenX(), y.toScreenY(), 1.toScreenSizeX(), 1.toScreenSizeY())
    }

    fun drawEntity(x: Int, y: Int, entity: Entity) {
        when (entity) {
            is Bot -> drawBot(entity, x, y)

            is PassiveEnergyProducer -> drawPassiveEnergyProducer(entity, x, y)

            is Sprig -> drawSprig(entity, x, y)

            else -> {
                gg.color = Color.PINK
                gg.fillOval(x.toScreenX(), y.toScreenY(), 1.toScreenSizeX(), 1.toScreenY())
            }
        }
    }

    private fun drawBot(bot: Bot, x: Int, y: Int) {
        when (renderMode) {
            RenderMode.ENERGY ->
                gg.color = Color(bot.energy.asUnsignedByte(), bot.energy.asUnsignedByte(), 0)

            else -> gg.color = BOT_COLOR
        }

        gg.fillOval(x.toScreenX(), y.toScreenY(), 1.toScreenSizeX(), 1.toScreenSizeY())
    }

    private fun drawPassiveEnergyProducer(entity: PassiveEnergyProducer, x: Int, y: Int) {
        val energyByte = entity.energy.asUnsignedByte()

        when (entity) {
            is Leaf -> {
                when (renderMode) {
                    // black - green
                    RenderMode.ENERGY -> gg.color = Color(0, energyByte, 0)

                    else -> gg.color = LEAF_COLOR
                }
                gg.fillOval(x.toScreenX(), y.toScreenY(), 1.toScreenSizeX(), 1.toScreenSizeY())
            }
            is MineralRoot -> {
                when (renderMode) {
                    // black - blue
                    RenderMode.ENERGY -> gg.color = Color(0, 0, energyByte)

                    else -> gg.color = M_ROOT_COLOR
                }
                gg.fillRect((x + 0.10).toScreenX(), (y + 0.10).toScreenY(), 0.8.toScreenSizeX(), 0.8.toScreenSizeY())
            }
            is OrganicRoot -> {
                when (renderMode) {
                    // black - blue
                    RenderMode.ENERGY -> gg.color = Color(0, 0, energyByte)

                    else -> gg.color = O_ROOT_COLOR
                }
                gg.fillRect((x + 0.10).toScreenX(), (y + 0.10).toScreenY(), 0.8.toScreenSizeX(), 0.8.toScreenSizeY())
            }
        }
    }

    private fun drawSprig(sprig: Sprig, x: Int, y: Int) {

        when (renderMode) {
            // black - red
            RenderMode.ENERGY -> gg.color = Color(sprig.energy.asUnsignedByte(), 0, 0)
            else -> gg.color = SPRIG_COLOR
        }
        val sprigSize = 0.25

        Direction.entries.forEach { direction ->
            if (sprig.connections.isConnectedTo(direction)) {
                when (direction) {
                    Direction.DOWN -> gg.fillRect(
                        (x + 0.5 - sprigSize / 2).toScreenX(),
                        (y + 0.5 - sprigSize / 2).toScreenY(),
                        sprigSize.toScreenSizeX(),
                        (0.5 + sprigSize / 2).toScreenSizeY(),
                    )

                    Direction.UP -> gg.fillRect(
                        (x + 0.5 - sprigSize / 2).toScreenX(),
                        y.toScreenY(),
                        sprigSize.toScreenSizeX(),
                        (0.5 + sprigSize / 2).toScreenSizeY(),
                    )

                    Direction.LEFT -> gg.fillRect(
                        x.toScreenX(),
                        (y + 0.5 - sprigSize / 2).toScreenY(),
                        (0.5 + sprigSize / 2).toScreenSizeX(),
                        sprigSize.toScreenSizeY(),
                    )

                    Direction.RIGHT -> gg.fillRect(
                        (x + 0.5 - sprigSize / 2).toScreenX(),
                        (y + 0.5 - sprigSize / 2).toScreenY(),
                        (0.5 + sprigSize / 2).toScreenSizeX(),
                        sprigSize.toScreenSizeY(),
                    )
                }
            }
        }
    }

    private fun resolveCellColor(sunValue: SunValue, mineralValue: MineralValue, organicValue: OrganicValue): Color =

        if (sunValue.isCritical() || mineralValue.isCritical() || organicValue.isCritical()) {
            Color.BLACK
        } else {
            when (renderMode) {
                RenderMode.DEFAULT -> Color.WHITE
                RenderMode.SUN -> Color(255, 255, 255 - sunValue.asUnsignedByte())
                RenderMode.ENERGY -> Color.WHITE
                RenderMode.MINERALS -> Color(
                    255 - mineralValue.asUnsignedByte(),
                    255 - mineralValue.asUnsignedByte(),
                    255,
                )

                RenderMode.ORGANICS -> Color(
                    255 - organicValue.asUnsignedByte(),
                    255,
                    255,
                )
            }
        }

    private inline fun Number.toScreenX(): Int =
        (this.toDouble() * scaleX + offsetX).toInt()

    private inline fun Number.toScreenY(): Int =
        (this.toDouble() * scaleY + offsetY).toInt()

    private inline fun Number.toScreenSizeX(): Int =
        (this.toDouble() * scaleX).toInt()

    private inline fun Number.toScreenSizeY(): Int =
        (this.toDouble() * scaleY).toInt()

    companion object {
        private val M_ROOT_COLOR = Color(100, 50, 70)
        private val SPRIG_COLOR = Color(0, 0, 0)
        private val O_ROOT_COLOR = Color(100, 80, 0)
        private val LEAF_COLOR = Color(100, 200, 100)
        private val BOT_COLOR = Color(200, 200, 200)
    }
}