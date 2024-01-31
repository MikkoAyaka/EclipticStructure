package org.wolflink.minecraft.plugin.eclipticstructure.library

import net.byteflux.libby.BukkitLibraryManager
import net.byteflux.libby.Library
import org.bukkit.plugin.Plugin

object DynamicLibrary {
    private val managers = mutableMapOf<Plugin,BukkitLibraryManager>()
    fun loadLibrary(library: Library,plugin: Plugin) {
        val manager = managers.getOrPut(plugin) {
            val m = BukkitLibraryManager(plugin)
            m.addMavenCentral()
            m.addJitPack()
            m
        }
        manager.loadLibrary(library)
    }
}