package org.wolflink.minecraft.plugin.eclipticstructure.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

class BuilderStatusEvent(
    val builder: Builder,
    val from: Builder.Status,
    val to: Builder.Status
): Event() {

    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}