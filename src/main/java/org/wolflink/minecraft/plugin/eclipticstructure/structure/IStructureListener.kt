package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.event.block.BlockBreakEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.*

interface IStructureListener {
    fun initialized(e: StructureInitializedEvent) {}
    fun completed(e: StructureCompletedEvent) {}
    fun destroyed(e: StructureDestroyedEvent) {}
    fun onAvailable(e: StructureAvailableEvent) {}
    fun onUnavailable(e: StructureUnavailableEvent) {}
    fun onDurabilityDamage(e: StructureDurabilityDamageEvent) {}

    /**
     * 破坏该建筑区域内的方块
     */
    fun onBlockBreak(e: BlockBreakEvent) {}
}