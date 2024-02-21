package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.*

object StructureListener: Listener,IStructureListener {
    @EventHandler
    override fun initialized(e: StructureInitializedEvent) {
        e.structure.handler.initialized(e)
        e.structure.customListeners.forEach { it.initialized(e) }
        e.structure.decorator.initialized(e)
    }
    @EventHandler
    override fun completed(e: StructureCompletedEvent) {
        e.structure.handler.completed(e)
        e.structure.customListeners.forEach { it.completed(e) }
        e.structure.decorator.completed(e)
    }
    @EventHandler
    override fun destroyed(e: StructureDestroyedEvent) {
        e.structure.handler.destroyed(e)
        e.structure.customListeners.forEach { it.destroyed(e) }
        e.structure.decorator.destroyed(e)
    }
    @EventHandler
    override fun onAvailable(e: StructureAvailableEvent) {
        e.structure.handler.onAvailable(e)
        e.structure.customListeners.forEach { it.onAvailable(e) }
        e.structure.decorator.onAvailable(e)
    }
    @EventHandler
    override fun onUnavailable(e: StructureUnavailableEvent) {
        e.structure.handler.onUnavailable(e)
        e.structure.customListeners.forEach { it.onUnavailable(e) }
        e.structure.decorator.onUnavailable(e)
    }
    @EventHandler
    override fun onDurabilityDamage(e: StructureDurabilityDamageEvent) {
        e.structure.handler.onDurabilityDamage(e)
        e.structure.customListeners.forEach { it.onDurabilityDamage(e) }
        e.structure.decorator.onDurabilityDamage(e)
    }
}