package org.wolflink.minecraft.plugin.eclipticstructure.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

class BuilderStartedEvent(val builder: Builder, val player: Player): Event() {

    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}