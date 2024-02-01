package org.wolflink.minecraft.plugin.eclipticstructure.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.StructureBuilder

class StructureBuilderStartEvent(val structureBuilder: StructureBuilder,val player: Player): Event() {

    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}