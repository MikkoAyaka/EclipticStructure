package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * 区域对象
 */
data class Zone(
    val worldName: String,
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    companion object {
        fun of(points: Pair<Location, Location>): Zone {
            if (points.first.world.name != points.second.world.name) throw IllegalArgumentException("尝试在不同世界中创建区域")

            val xRange = if (points.first.blockX <= points.second.blockX) {
                points.first.blockX..points.second.blockX
            } else {
                points.second.blockX..points.first.blockX
            }

            val yRange = if (points.first.blockY <= points.second.blockY) {
                points.first.blockY..points.second.blockY
            } else {
                points.second.blockY..points.first.blockY
            }

            val zRange = if (points.first.blockZ <= points.second.blockZ) {
                points.first.blockZ..points.second.blockZ
            } else {
                points.second.blockZ..points.first.blockZ
            }

            return Zone(points.first.world.name, xRange, yRange, zRange)
        }
    }

    operator fun contains(point: Location) =
        point.world.name == worldName
                && point.blockX in xRange
                && point.blockY in yRange
                && point.blockZ in zRange

    operator fun contains(player: Player) =
        contains(player.location)
}