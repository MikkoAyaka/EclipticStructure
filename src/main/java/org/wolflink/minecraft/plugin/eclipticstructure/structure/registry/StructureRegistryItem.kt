package org.wolflink.minecraft.plugin.eclipticstructure.structure.registry

import org.bukkit.plugin.Plugin
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Blueprint
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

/**
 * @param fromPlugin        注册该建筑结构的插件
 * @param structureTypeName 建筑结构类型名(来自枚举类，而非结构展示名称)
 * @param blueprints        结构蓝图列表
 * @param structureSupplier 结构创建函数
 */
class StructureRegistryItem(
    val fromPlugin: Plugin,
    val structureTypeName:String,
    val blueprints: List<Blueprint>,
    val structureSupplier: (Int,Builder)-> Structure
)
