package org.wolflink.minecraft.plugin.eclipticstructure.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

class BuilderCompletedEvent(val builder: Builder,val structure: Structure): Event() {

    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}