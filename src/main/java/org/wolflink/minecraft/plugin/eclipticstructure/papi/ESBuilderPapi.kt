package org.wolflink.minecraft.plugin.eclipticstructure.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.wolflink.minecraft.plugin.eclipticstructure.display.TextProgressBar
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureBuilderRepository
import org.wolflink.minecraft.plugin.eclipticstructure.structure.StructureBuilder

object ESBuilderPapi: PlaceholderExpansion() {
    override fun getIdentifier() = "esbuilder"

    override fun getAuthor() = "MikkoAyaka"

    override fun getVersion() = "1.0.0"

    // 格式例如：%esbuilder_114_progress%
    override fun onRequest(player: OfflinePlayer?, params: String): String {
        if(player == null) return ""
        val args = params.split("_")
        val builderId = args.getOrNull(0)?.toIntOrNull() ?: return ""
        val structureBuilder = StructureBuilderRepository.find(builderId) ?: return ""
        if(args.getOrNull(1) == "progress") {
            return TextProgressBar(structureBuilder.getBuildProgress()).getBar()
        }
        if(args.getOrNull(1) == "timeleft") {
            return "${structureBuilder.getBuildTimeLeft()} 秒"
        }
        if(args.getOrNull(1) == "structurename") {
            return structureBuilder.blueprint.structureDisplayName
        }
        return ""
    }
}