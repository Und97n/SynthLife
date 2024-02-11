package org.ua.und97n.org.ua.und97n.synthlife.simulation

import org.ua.und97n.org.ua.und97n.synthlife.simulation.life.DeathReason
import org.ua.und97n.synthlife.field.Entity
import org.ua.und97n.synthlife.field.WorldContext
import java.util.concurrent.atomic.AtomicLong

class WorldContextImpl : WorldContext {
    var tickDelay: Long = 1
    var mutationProbability = 0.25

    val deathStatistics: Map<DeathReason, AtomicLong> = DeathReason.entries.associateWith { AtomicLong(0) }

    fun registerDeath(deathReason: DeathReason, entity: Entity) {
        deathStatistics[deathReason]!!.incrementAndGet()
    }
}