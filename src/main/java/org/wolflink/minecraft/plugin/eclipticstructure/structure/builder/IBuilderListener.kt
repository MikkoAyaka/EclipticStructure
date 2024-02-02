package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderStartedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderStatusEvent

interface IBuilderListener {
    fun completed(e: BuilderCompletedEvent)
    fun started(e: BuilderStartedEvent)
    fun toggleStatus(e: BuilderStatusEvent)
    fun destroyed(e: BuilderDestroyedEvent)
}