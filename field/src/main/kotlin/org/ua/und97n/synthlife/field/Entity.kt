package org.ua.und97n.synthlife.field

abstract class Entity {
    private var entityConnections: EntityConnections = EntityConnections.none()

    internal var replaceTo: Entity? = this // used to have proper connections management

    val connections: EntityConnections
        get() = this.entityConnections

    internal fun updateInternal(handle: CellHandle) {
        entityConnections.refreshEntities()
        update(handle)
    }

    /**
     * DO NOT USE WHEN ENTITY IS ON FIELD
     */
    fun initConnections(connections: EntityConnections) {
        entityConnections = connections
    }

    abstract fun update(handle: CellHandle)
}