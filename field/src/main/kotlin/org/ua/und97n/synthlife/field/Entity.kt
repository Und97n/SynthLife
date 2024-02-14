package org.ua.und97n.synthlife.field

abstract class Entity {
    internal var replaceTo: Entity? = this // used to have proper connections management

    val connections: EntityConnections = EntityConnections.none()

    abstract fun update(handle: CellHandle)

    internal fun updateInternal(handle: CellHandle) {
        refreshConnections()
        update(handle)
    }

    /**
     * Should not be used when entities are on field (because of concurrency.
     */
    fun connectUnsafe(direction: Direction, entity: Entity) {
        connections.connect(direction, entity)
        entity.connections.connect(direction.mirror(), this)
    }

    fun disconnect(direction: Direction) {
        this.connections.disconnect(direction)
    }

    fun replaceThis(cellHandle: CellHandle, replaceTo: Entity?, preserveConnections: Boolean = true) {
        cellHandle.replaceEntityTo(replaceTo)
        this.replaceTo = replaceTo

        if (replaceTo != null && preserveConnections) {
            connections.iterateExistent { entity, direction ->
                replaceTo.connections.connect(direction, entity)
            }
        }
    }

    fun disconnectAll() {
        this.connections.clear()
        Direction.entries.forEach(this::disconnect)
    }

    private fun refreshConnections() {
        connections.iterateExistent { entity, direction ->
            var e = entity.replaceTo
            if (e?.connections?.getConnected(direction.mirror()) != this)  e = null
            connections.setConnected(direction, e)
        }
    }
}