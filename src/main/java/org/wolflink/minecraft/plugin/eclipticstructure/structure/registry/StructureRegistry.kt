package org.wolflink.minecraft.plugin.eclipticstructure.structure.registry

import org.bukkit.plugin.Plugin
import org.wolflink.minecraft.plugin.eclipticstructure.esLogger
import org.wolflink.minecraft.plugin.eclipticstructure.structure.blueprint.Blueprint
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

object StructureRegistry {
    private val structureRegistryItems = mutableSetOf<StructureRegistryItem>()
    fun forEach(block: (structureRegistryItem: StructureRegistryItem)->Unit) {
        structureRegistryItems.forEach(block)
    }
    fun register(plugin: Plugin, structureTypeName: String, blueprints: List<Blueprint>, structureSupplier:(Int, Builder)-> Structure) {
        val structureRegistryItem = StructureRegistryItem(plugin,structureTypeName,blueprints,structureSupplier)
        if(contains(structureTypeName)) {
            esLogger.warning("$structureTypeName 该建筑结构名已经被 ${plugin.name} 插件注册了，无法重复注册，请更改结构名称")
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
            esLogger.warning("$structureTypeName 建筑结构不存在于注册列表中，无法取消注册")
        } else structureRegistryItems.remove(structureRegistryItem)
    }
    fun get(structureTypeName: String) = structureRegistryItems.firstOrNull { it.structureTypeName == structureTypeName } ?: throw NullPointerException("$structureTypeName 未被注册")
    fun contains(structureRegistryItem: StructureRegistryItem) = structureRegistryItems.contains(structureRegistryItem)
    fun contains(structureTypeName: String) = structureRegistryItems.map { it.structureTypeName.uppercase() }.contains(structureTypeName.uppercase())
}