package org.ua.und97n.org.ua.und97n.synthlife.render.javaswing

import org.ua.und97n.org.ua.und97n.synthlife.simulation.World
import java.awt.Component
import java.awt.Font
import java.awt.Label
import java.util.*
import javax.swing.Box
import javax.swing.JTextArea

class SideMenu {
    private val statistics: JTextArea = JTextArea()

    private val font = Font("Consolas", Font.PLAIN, 20)

    fun build(): Component {
        val menuBox = Box.createVerticalBox()
        menuBox.add(Label("Statistics"))
        menuBox.add(statistics)

        statistics.isEditable = false

        menuBox.font = font
        statistics.font = font

        return menuBox
    }

    fun update(world: World) {
        val deathStatistics = world.context.deathStatistics.entries.joinToString(separator = "\n") {
            "${it.key}: ${it.value.get()}"
        }

        statistics.text = "Ticks per second: ${formatTps(world.actualTps)}\n\n$deathStatistics"
    }

    private fun formatTps(rps: Double?): String =
        rps?.let {
            String.format(Locale.US, "%.1f", it)
        } ?: "???"
}