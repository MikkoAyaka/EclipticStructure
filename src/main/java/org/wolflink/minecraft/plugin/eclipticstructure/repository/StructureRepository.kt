package org.wolflink.minecraft.plugin.eclipticstructure.repository

import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Zone
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository

/**
 * 建筑结构仓库，区域对象 -> 建筑结构对象
 */
object StructureRepository: MapRepository<Zone, Structure>() {
    override fun getPrimaryKey(p0: Structure) = p0.builder.zone
}