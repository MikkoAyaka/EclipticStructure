package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.*
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureZoneRelationRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository

class StructureHandler(private val structure: Structure): IStructureListener {
    private fun insertToRepo() {
        println("+ ${structure.id} -> ${structure.blueprint.structureName}")
        StructureRepository.insert(structure)
        ZoneRepository.insert(structure.zone)
        StructureZoneRelationRepository.associateByValue(structure,structure.zone)
    }
    private fun deleteFromRepo() {
        println("- ${structure.id} -> ${structure.blueprint.structureName}")
        StructureRepository.deleteByValue(structure)
        ZoneRepository.deleteByValue(structure.zone)
        StructureZoneRelationRepository.dissociateByValue(structure,structure.zone)
    }
    override fun initialized(e: StructureInitializedEvent) {
        insertToRepo()
    }

    override fun completed(e: StructureCompletedEvent) {
        e.structure.available = true
    }

    override fun destroyed(e: StructureDestroyedEvent) {
        deleteFromRepo()
    }
}