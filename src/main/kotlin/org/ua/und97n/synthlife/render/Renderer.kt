package org.ua.und97n.org.ua.und97n.synthlife.render

import org.ua.und97n.org.ua.und97n.synthlife.simulation.World

interface Renderer {
    fun start(world: World)

    fun dispose()
}