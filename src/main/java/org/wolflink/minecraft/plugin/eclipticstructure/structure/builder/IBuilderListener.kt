package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.*

interface IBuilderListener {
    fun preBuild(e: BuilderPreBuildEvent)
    fun completed(e: BuilderCompletedEvent)
    fun started(e: BuilderStartedEvent)
    fun toggleStatus(e: BuilderStatusEvent)
    fun destroyed(e: BuilderDestroyedEvent)
}