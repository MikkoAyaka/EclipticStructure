package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.plugin.Plugin
import org.wolflink.minecraft.plugin.eclipticstructure.logger

object StructureRegistry {
    private val structureMetas = mutableSetOf<StructureMeta>()
    fun forEach(block: (structureMeta: StructureMeta)->Unit) {
        structureMetas.forEach(block)
    }
    fun register(plugin: Plugin,structureTypeName: String,blueprint: StructureBlueprint,structureSupplier:(StructureBuilder)->Structure) {
        val structureMeta = StructureMeta(plugin,structureTypeName,blueprint,structureSupplier)
        if(contains(structureTypeName)) {
            logger.warning("$structureTypeName 该建筑结构名已经被 ${plugin.name} 插件注册了，无法重复注册，请更改结构名称")
        } else structureMetas.add(structureMeta)
    }
    fun unregister(structureTypeName: String) {
        var structureMeta: StructureMeta? = null
        forEach {
            if(it.structureTypeName == structureTypeName) {
                structureMeta = it
                return@forEach
            }
        }
        if(structureMeta == null) {
            logger.warning("$structureTypeName 建筑结构不存在于注册列表中，无法取消注册")
        } else structureMetas.remove(structureMeta)
    }
    fun get(structureTypeName: String) = structureMetas.firstOrNull { it.structureTypeName == structureTypeName }
    fun contains(structureMeta: StructureMeta) = structureMetas.contains(structureMeta)
    fun contains(structureTypeName: String) = structureMetas.map { it.structureTypeName.uppercase() }.contains(structureTypeName.uppercase())
}