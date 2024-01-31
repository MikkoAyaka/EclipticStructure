package org.wolflink.minecraft.plugin.eclipticstructure.structure

abstract class Structure(val blueprint: StructureBlueprint,val builder: StructureBuilder) {
    var available = false
}