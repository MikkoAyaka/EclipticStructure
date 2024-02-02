package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderStartedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderStatusEvent

object BuilderListener: Listener, IBuilderListener {
    @EventHandler
    override fun started(e: BuilderStartedEvent) {
        e.builder.decorator.started(e)
    }
    @EventHandler
    override fun toggleStatus(e: BuilderStatusEvent) {
        e.builder.decorator.toggleStatus(e)
    }
    @EventHandler
    override fun destroyed(e: BuilderDestroyedEvent) {
        e.builder.decorator.destroyed(e)
    }

    @EventHandler
    override fun completed(e: BuilderCompletedEvent) {
        e.builder.decorator.completed(e)
    }
}