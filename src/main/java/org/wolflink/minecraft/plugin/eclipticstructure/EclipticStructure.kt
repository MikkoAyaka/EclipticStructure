package org.wolflink.minecraft.plugin.eclipticstructure

import eu.decentsoftware.holograms.api.DHAPI
import kotlinx.coroutines.cancel
import net.byteflux.libby.BukkitLibraryManager
import net.byteflux.libby.Library
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.file.FileFolders
import org.wolflink.minecraft.plugin.eclipticstructure.library.DynamicLibrary
import org.wolflink.minecraft.plugin.eclipticstructure.papi.ESBuilderPapi

/**
 * 建筑结构库
 * 动态建造建筑结构、收取材料、判断玩家是否处于建筑区域等
 */
class EclipticStructure : JavaPlugin() {
    companion object {
        lateinit var instance: EclipticStructure
        fun runTask(block: ()->Unit) {
            Bukkit.getScheduler().runTask(instance,block)
        }
    }
    override fun onLoad() {
        instance = this
    }
    override fun onEnable() {
        FileFolders.init()
        ESBuilderPapi.register()
    }

    override fun onDisable() {
        EStructureScope.cancel()
        ESBuilderPapi.unregister()
    }
}
