package org.wolflink.minecraft.plugin.eclipticstructure.event.structure

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure

/**
 * 可被取消的建筑耐久损伤事件
 * TODO 丑陋的伤害修改器
 * 伤害来源类型：Player、List<Monster>、Block、Entity、EnergySource
 */
class StructureDurabilityDamageEvent(val structure: Structure, val damageSourceType: Structure.DamageSource, val damageSource: Any?, val damage: Int, var damageMultiple: Double = 1.0): Event(),Cancellable {
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