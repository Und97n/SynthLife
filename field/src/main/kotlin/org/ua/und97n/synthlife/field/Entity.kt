package org.ua.und97n.synthlife.field

abstract class Entity {
    private var entityConnections: EntityConnections = EntityConnections.none()

    internal var replaceTo: Entity? = this // used to have proper connections management
    private var updateContext: EntityUpdateContext? = null

    val connections: EntityConnections
        get() = this.entityConnections

    val cellContext: CellContext
        get() = CellContext(updateContext!!)

    internal fun updateInternal(context: EntityUpdateContext) {
        this.updateContext = context
        entityConnections.refreshEntities()
        update()
        this.updateContext = null
    }

    /**
     * DO NOT USE WHEN ENTITY IS ON FIELD
     */
    fun initConnections(connections: EntityConnections) {
        entityConnections = connections
    }

    abstract fun update()
}