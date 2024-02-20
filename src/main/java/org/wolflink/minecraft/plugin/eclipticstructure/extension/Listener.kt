package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure

fun Listener.register(plugin: Plugin) {
    Bukkit.getPluginManager().registerEvents(this,plugin)
}
fun Listener.unregister() = HandlerList.unregisterAll(this)