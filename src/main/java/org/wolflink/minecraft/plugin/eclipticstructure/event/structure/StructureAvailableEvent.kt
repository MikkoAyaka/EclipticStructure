package org.wolflink.minecraft.plugin.eclipticstructure.event.structure

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure

class StructureAvailableEvent(val structure: Structure): Event() {
    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}