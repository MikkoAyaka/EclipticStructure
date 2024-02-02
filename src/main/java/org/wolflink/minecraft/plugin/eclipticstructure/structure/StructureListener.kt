package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.event.Listener
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureInitializedEvent

object StructureListener: Listener,IStructureListener {
    override fun initialized(e: StructureInitializedEvent) {
        e.structure.handler.initialized(e)
        e.structure.decorator.initialized(e)
    }

    override fun completed(e: StructureCompletedEvent) {
        e.structure.handler.completed(e)
        e.structure.decorator.completed(e)
    }

    override fun destroyed(e: StructureDestroyedEvent) {
        e.structure.handler.destroyed(e)
        e.structure.decorator.destroyed(e)
    }
}