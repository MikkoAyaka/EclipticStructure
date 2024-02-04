package org.wolflink.minecraft.plugin.eclipticstructure.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.wolflink.minecraft.plugin.eclipticstructure.display.BuildProgressBar
import org.wolflink.minecraft.plugin.eclipticstructure.repository.BuilderRepository

object BuilderPapi: PlaceholderExpansion() {
    override fun getIdentifier() = "esbuilder"

    override fun getAuthor() = "MikkoAyaka"

    override fun getVersion() = "1.0.0"

    // 格式例如：%esbuilder_114_progress%
    override fun onRequest(player: OfflinePlayer?, params: String): String {
        val args = params.split("_")
        val builderId = args.getOrNull(0)?.toIntOrNull() ?: return ""
        val structureBuilder = BuilderRepository.find(builderId) ?: return ""
        if(args.getOrNull(1) == "status") {
            return structureBuilder.status.msg
        }
        if(args.getOrNull(1) == "progress") {
            return BuildProgressBar(structureBuilder.buildProgress).getBar()
        }
        if(args.getOrNull(1) == "timeleft") {
            return "${structureBuilder.buildTimeLeft} 秒"
        }
        if(args.getOrNull(1) == "structurename") {
            return structureBuilder.blueprint.structureName
        }
        return ""
    }
}