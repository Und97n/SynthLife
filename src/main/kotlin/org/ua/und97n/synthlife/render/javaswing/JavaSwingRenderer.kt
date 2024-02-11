package org.ua.und97n.org.ua.und97n.synthlife.render.javaswing

import org.ua.und97n.org.ua.und97n.synthlife.render.Renderer
import org.ua.und97n.org.ua.und97n.synthlife.simulation.World
import org.ua.und97n.synthlife.field.*
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.random.Random.Default.nextInt

class JavaSwingRenderer : Renderer {
    private val frame = JFrame("SynthLife")
    private val panel = Panel()

    private lateinit var world: World

    private lateinit var renderThread: Thread

    private var renderMode: RenderMode = RenderMode.DEFAULT

    override fun start(world: World) {
        this.world = world

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(1300, 1300)
        frame.add(panel)
        frame.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                when (e?.keyCode) {
                    KeyEvent.VK_1 -> {
                        renderMode = RenderMode.DEFAULT
                        println("Current render mode = $renderMode")
                    }

                    KeyEvent.VK_2 -> {
                        renderMode = RenderMode.SUN
                        println("Current render mode = $renderMode")
                    }
                    KeyEvent.VK_3 -> {
                        renderMode = RenderMode.ENERGY
                        println("Current render mode = $renderMode")
                    }

                    KeyEvent.VK_4 -> {
                        renderMode = RenderMode.MINERALS
                        println("Current render mode = $renderMode")
                    }
                    KeyEvent.VK_5 -> {
                        renderMode = RenderMode.ORGANICS
                        println("Current render mode = $renderMode")
                    }
                    KeyEvent.VK_SPACE -> {
                        world.putRandomBots()
                    }
                }
            }
        })

        frame.isVisible = true

        renderThread = Thread(
            null,
            this::renderLoop,
            "java-swing-renderer"
        )
        renderThread.start()
    }

    override fun dispose() {
        frame.isVisible = false
        frame.dispose()
        renderThread.join()
    }

    private fun renderLoop() {
        while (frame.isVisible) {
            panel.repaint()
            Thread.sleep(20)
        }
    }

    private fun render(graphics: CustomGraphics) {
        graphics.prefill(Color.WHITE)

        val iterator = object : CellIterator {
            override fun execute(
                x: Int,
                y: Int,
                entity: Entity?,
                sun: SunValue,
                mineral: MineralValue,
                organic: OrganicValue
            ) {
                graphics.drawCell(
                    x = x,
                    y = y,
                    sunValue = sun,
                    mineralValue = mineral,
                    organicValue = organic,
                )
                entity?.let {
                    graphics.drawEntity(x, y, it)
                }
            }
        }
        world.field.iterateAll(iterator)
    }

    private inner class Panel : JPanel() {
        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)
            val gg = g as? Graphics2D

            gg?.let {

                val pixelsPerCellWidth = this.width / world.field.width.toDouble()
                val pixelsPerCellHeight = this.height / world.field.height.toDouble()

                var offsetX: Double
                var offsetY: Double
                var scale: Double

                if (pixelsPerCellWidth < pixelsPerCellHeight) {
                    scale = pixelsPerCellWidth
                    offsetY = (pixelsPerCellHeight-pixelsPerCellWidth)*world.field.width / 2
                    offsetX = 0.0
                } else {
                    scale = pixelsPerCellHeight
                    offsetY = 0.0
                    offsetX = (pixelsPerCellWidth-pixelsPerCellHeight)*world.field.height / 2
                }

                val custom = CustomGraphics(
                    gg = gg,
                    canvasWidth = this.width,
                    canvasHeight = this.height,
                    renderMode = renderMode,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    scaleX = scale,
                    scaleY = scale
                )
                render(custom)
            }
        }
    }


}