package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.BuilderCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.BuilderDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.BuilderStartedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.BuilderStatusEvent

interface IBuilderListener {
    fun completed(e: BuilderCompletedEvent)
    fun started(e: BuilderStartedEvent)
    fun toggleStatus(e: BuilderStatusEvent)
    fun destroyed(e: BuilderDestroyedEvent)
}