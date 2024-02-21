package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure

/**
 * 该事件会在 Bukkit 主线程的任务环境中触发
 */
fun Event.call() {
    EclipticStructure.runTask {
        Bukkit.getPluginManager().callEvent(this)
    }
}