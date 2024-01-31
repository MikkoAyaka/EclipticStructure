package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.plugin.Plugin

class StructureMeta(
    val fromPlugin: Plugin,
    val structureTypeName:String,
    val blueprint: StructureBlueprint,
    val structureSupplier: (StructureBuilder)->Structure
)
