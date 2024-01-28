package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Bukkit
import org.bukkit.event.Event

// 触发事件
fun Event.call() {
    Bukkit.getPluginManager().callEvent(this)
}