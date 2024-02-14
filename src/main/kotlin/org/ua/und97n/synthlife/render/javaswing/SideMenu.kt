package org.ua.und97n.org.ua.und97n.synthlife.render.javaswing

import org.ua.und97n.org.ua.und97n.synthlife.simulation.World
import java.awt.Component
import java.awt.Font
import java.awt.Label
import java.util.*
import javax.swing.Box
import javax.swing.JSlider
import javax.swing.JTextArea

class SideMenu {
    private val info: JTextArea = JTextArea()
    private val tickDelaySlider: JSlider = JSlider(0, 500)

    private val font = Font("Helvetica", Font.PLAIN, 20)

    fun build(): Component {
        val menuBox = Box.createVerticalBox()
        menuBox.add(Label("Tick delay (ms)"))
        menuBox.add(tickDelaySlider)
        menuBox.add(Label("Info"))
        menuBox.add(info)

        info.isEditable = false
        info.isFocusable = false
        tickDelaySlider.isFocusable = false

        menuBox.font = font
        info.font = font

        return menuBox
    }

    fun update(world: World, renderMode: RenderMode) {
        world.context.tickDelay = tickDelaySlider.value.toLong()
        info.text = """
Render mode: $renderMode
Actual TPS: ${formatTps(world.actualTps)}


        """.trimIndent()+ world.context.toString()
    }

    private fun formatTps(tps: Double?): String =
        tps?.let {
            String.format(Locale.US, "%.1f", it)
        } ?: "???"
}