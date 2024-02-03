package org.wolflink.minecraft.plugin.eclipticstructure.event.builder

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

class BuilderDestroyedEvent(val builder: Builder): Event() {

    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}