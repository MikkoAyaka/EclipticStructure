package org.wolflink.minecraft.plugin.eclipticstructure.structure.registry

import org.bukkit.plugin.Plugin
import org.wolflink.minecraft.plugin.eclipticstructure.logger
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Blueprint
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

object StructureRegistry {
    private val structureRegistryItems = mutableSetOf<StructureRegistryItem>()
    fun forEach(block: (structureRegistryItem: StructureRegistryItem)->Unit) {
        structureRegistryItems.forEach(block)
    }
    fun register(plugin: Plugin, structureTypeName: String, blueprint: Blueprint, structureSupplier:(Builder)-> Structure) {
        val structureRegistryItem = StructureRegistryItem(plugin,structureTypeName,blueprint,structureSupplier)
        if(contains(structureTypeName)) {
            logger.warning("$structureTypeName 该建筑结构名已经被 ${plugin.name} 插件注册了，无法重复注册，请更改结构名称")
        } else structureRegistryItems.add(structureRegistryItem)
    }
    fun unregister(structureTypeName: String) {
        var structureRegistryItem: StructureRegistryItem? = null
        forEach {
            if(it.structureTypeName == structureTypeName) {
                structureRegistryItem = it
                return@forEach
            }
        }
        if(structureRegistryItem == null) {
            logger.warning("$structureTypeName 建筑结构不存在于注册列表中，无法取消注册")
        } else structureRegistryItems.remove(structureRegistryItem)
    }
    fun get(structureTypeName: String) = structureRegistryItems.firstOrNull { it.structureTypeName == structureTypeName }
    fun contains(structureRegistryItem: StructureRegistryItem) = structureRegistryItems.contains(structureRegistryItem)
    fun contains(structureTypeName: String) = structureRegistryItems.map { it.structureTypeName.uppercase() }.contains(structureTypeName.uppercase())
}