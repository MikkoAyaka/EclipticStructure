package org.wolflink.minecraft.plugin.eclipticstructure.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.wolflink.minecraft.plugin.eclipticstructure.display.DurabilityBar
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureRepository

object StructurePapi: PlaceholderExpansion() {
    override fun getIdentifier() = "esstructure"

    override fun getAuthor() = "MikkoAyaka"

    override fun getVersion() = "1.0.0"

    // 格式例如：%esbuilder_114_progress%
    override fun onRequest(player: OfflinePlayer?, params: String): String {
        val args = params.split("_")
        val structureId = args.getOrNull(0)?.toIntOrNull() ?: return ""
        val structure = StructureRepository.find(structureId) ?: return ""
        if(args.getOrNull(1) == "durabilitybar") {
            val durabilityPercent = structure.durability / structure.blueprint.maxDurability.toDouble()
            val bar = DurabilityBar(durabilityPercent)
            return bar.getBar()
        }
        if(args.getOrNull(1) == "structurename") {
            return structure.blueprint.structureName
        }
        return ""
    }
}