package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure

// 触发事件
fun Event.call() {
    EclipticStructure.runTask {
        Bukkit.getPluginManager().callEvent(this)
    }
}