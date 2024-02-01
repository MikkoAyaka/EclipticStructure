package org.wolflink.minecraft.plugin.eclipticstructure.repository

import org.wolflink.minecraft.plugin.eclipticstructure.structure.StructureBuilder
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository

object StructureBuilderRepository: MapRepository<Int,StructureBuilder>() {
    override fun getPrimaryKey(p0: StructureBuilder) = p0.id
}