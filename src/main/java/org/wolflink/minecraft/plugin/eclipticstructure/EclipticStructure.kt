package org.wolflink.minecraft.plugin.eclipticstructure

import kotlinx.coroutines.cancel
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.extension.register
import org.wolflink.minecraft.plugin.eclipticstructure.file.FileFolders
import org.wolflink.minecraft.plugin.eclipticstructure.papi.BuilderPapi
import org.wolflink.minecraft.plugin.eclipticstructure.papi.StructurePapi
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.BuilderListener
import org.wolflink.minecraft.plugin.eclipticstructure.structure.StructureDurabilityListener
import org.wolflink.minecraft.plugin.eclipticstructure.structure.StructureListener

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
        BuilderPapi.register()
        StructurePapi.register()
        // 注册监听器
        BuilderListener.register(this)
        StructureDurabilityListener.register(this)
        StructureListener.register(this)
    }

    override fun onDisable() {
        EStructureScope.cancel()
        BuilderPapi.unregister()
        StructurePapi.unregister()
    }
}
