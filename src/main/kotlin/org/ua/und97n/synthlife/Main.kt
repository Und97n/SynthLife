package org.ua.und97n.org.ua.und97n.synthlife

import org.ua.und97n.org.ua.und97n.synthlife.render.javaswing.JavaSwingRenderer
import org.ua.und97n.org.ua.und97n.synthlife.simulation.World

fun main() {
    val world = World(200, 200)
    val renderer = JavaSwingRenderer()
    renderer.start(world)
    world.startSimulation()
}