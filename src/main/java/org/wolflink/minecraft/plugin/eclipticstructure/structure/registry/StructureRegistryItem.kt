package org.wolflink.minecraft.plugin.eclipticstructure.structure.registry

import org.bukkit.plugin.Plugin
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Blueprint
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder

class StructureRegistryItem(
    val fromPlugin: Plugin,
    val structureTypeName:String,
    val blueprint: Blueprint,
    val structureSupplier: (Builder)-> Structure
)
