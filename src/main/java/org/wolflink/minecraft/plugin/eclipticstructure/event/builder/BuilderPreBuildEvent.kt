package org.wolflink.minecraft.plugin.eclipticstructure.event.builder

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

/**
 *  建造者开始建造检查逻辑之前触发该事件
 */
class BuilderPreBuildEvent(val builder: Builder,val player: Player): Event(), Cancellable {
    private var cancelled = false
    override fun getHandlers() = handlerList
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList() = handlerList
    }
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}