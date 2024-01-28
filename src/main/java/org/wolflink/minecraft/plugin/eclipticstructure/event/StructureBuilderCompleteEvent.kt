package org.wolflink.minecraft.plugin.eclipticstructure.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.StructureBuilder

class StructureBuilderCompleteEvent(val structureBuilder: StructureBuilder): Event() {

    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        fun getHandlerList() = handlerList
    }
}