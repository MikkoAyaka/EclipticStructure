package org.wolflink.minecraft.plugin.eclipticstructure.event.structure

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure

/**
 * 刚开始建造
 */
class StructureInitializedEvent(val structure: Structure): Event() {
    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
}