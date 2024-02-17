package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.structure.blueprint.Blueprint
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

abstract class StructureCompanion<T: Structure> {
    abstract val blueprints: List<Blueprint>
    abstract val clazz: Class<T>
    abstract fun supplier(blueprint: Blueprint, builder: Builder): T
    fun create(structureLevel: Int, builder: Builder): T {
        val blueprint = blueprints.getOrNull(structureLevel-1)
            ?: throw IllegalArgumentException("不支持的建筑等级：${structureLevel}")
        return supplier(blueprint,builder)
    }
}