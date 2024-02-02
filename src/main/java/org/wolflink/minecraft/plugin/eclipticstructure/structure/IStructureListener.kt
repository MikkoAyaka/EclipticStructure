package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureInitializedEvent

interface IStructureListener {
    fun initialized(e: StructureInitializedEvent)
    fun completed(e: StructureCompletedEvent)
    fun destroyed(e: StructureDestroyedEvent)
}