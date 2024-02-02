package org.wolflink.minecraft.plugin.eclipticstructure.repository

import org.wolflink.minecraft.plugin.eclipticstructure.structure.Structure
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Zone
import org.wolflink.minecraft.wolfird.framework.database.repository.RelationRepository

object StructureZoneRelationRepository: RelationRepository<Int, Structure,Int,Zone>() {
    override fun getPrimaryKey1(value: Structure) = value.id

    override fun getValue1(key: Int): Structure = StructureRepository.find(key)

    override fun getValue2(key: Int): Zone = ZoneRepository.find(key)

    override fun getPrimaryKey2(value: Zone) = value.id
}