package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.display.ESHologram
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureInitializedEvent

class StructureDecorator(val structure: Structure): IStructureListener {
    private val hologram = ESHologram(structure.uniqueName,structure.builder.buildLocation,listOf(
        "§r%${structure.uniqueName}_structurename%",
        "§r",
        "§f%${structure.uniqueName}_durabilitybar%",
        "§f",
    ))
    override fun initialized(e: StructureInitializedEvent) {

    }

    override fun completed(e: StructureCompletedEvent) {
        hologram.create()
    }

    override fun destroyed(e: StructureDestroyedEvent) {
        hologram.delete()
    }
}