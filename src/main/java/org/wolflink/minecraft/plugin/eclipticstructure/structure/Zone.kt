package org.wolflink.minecraft.plugin.eclipticstructure.structure

import com.sk89q.worldedit.extent.clipboard.Clipboard
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope

/**
 * 区域对象
 * 区域是可部分重叠，不可完全重叠的，玩家可能同时处在多个区域中
 */
data class Zone (
    val worldName: String,
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    val players: Set<Player>
        get() = Bukkit.getOnlinePlayers().filter { it.location in this }.toSet()
    val minLocation: Location
        get() = Location(Bukkit.getWorld(worldName),xRange.first.toDouble(),yRange.first.toDouble(),zRange.first.toDouble())
    val maxLocation: Location
        get() = Location(Bukkit.getWorld(worldName),xRange.last.toDouble(),yRange.last.toDouble(),zRange.last.toDouble())
    companion object {
        //最小点 -465 -60 558
        //最大点 -461 -55 562
        fun create(world: World, relative: Location, clipboard: Clipboard): Zone {
            val minPoint = clipboard.minimumPoint
            val maxPoint = clipboard.maximumPoint
            return Zone(
                world.name,
                (minPoint.blockX+relative.blockX)..(maxPoint.blockX+relative.blockX),
                (minPoint.blockY+relative.blockY)..(maxPoint.blockY+relative.blockY),
                (minPoint.blockZ+relative.blockZ)..(maxPoint.blockZ+relative.blockZ),
                )
        }
        fun create(points: Pair<Location, Location>): Zone {
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

    /**
     * 获取长方体区域的8个顶点坐标
     */
    private fun getVertex(): Set<Location> {
        return setOf(
            minLocation,
            minLocation.apply { x = maxLocation.x },
            minLocation.apply { x = maxLocation.x }.apply { y = maxLocation.y },
            minLocation.apply { y = maxLocation.y },
            minLocation.apply { z = maxLocation.z },
            minLocation.apply { z = maxLocation.z }.apply { x = maxLocation.x },
            minLocation.apply { z = maxLocation.z }.apply { y = maxLocation.y },
            maxLocation
            )
    }
    fun forEach(action: (world: World,x: Int,y: Int,z: Int)->Unit) {
        val world = Bukkit.getWorld(worldName) ?: throw IllegalStateException("不可能发生的错误，未找到世界：$worldName")
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    action.invoke(world,x,y,z)
                }
            }
        }
    }

    /**
     * 会阻塞当前线程直至粒子效果渲染完毕
     */
    suspend fun display(durationInSeconds: Int, spawnParticle: (world:World, x:Double, y:Double, z:Double)->Unit) {
        repeat(durationInSeconds) {
            forEach { world, x, y, z -> spawnParticle(world,x.toDouble(),y.toDouble(),z.toDouble()) }
            delay(1000)
        }
    }

    /**
     * 该区域是否无实体方块遮挡，也没有玩家在区域中(忽略草丛鲜花等)
     */
    suspend fun isEmpty(): Boolean {
        val deferredResults = mutableListOf<Deferred<Boolean>>()
        forEach { world, x, y, z ->
            val deferred = EStructureScope.async {
                val block = world.getBlockAt(x, y, z)
                block.type.isSolid
            }
            deferredResults.add(deferred)
        }
        val results = deferredResults.awaitAll()
        return results.any { true }
    }

    /**
     * 该区域是否拥有地板
     */
    suspend fun hasFloor(): Boolean {
        val world = Bukkit.getWorld(worldName) ?: throw IllegalStateException("不可能发生的错误，未找到世界：$worldName")
        val deferredResults = mutableListOf<Deferred<Boolean>>()
        xRange.forEach { x ->
            zRange.forEach { z ->
                val deferred = EStructureScope.async {
                    val block = world.getBlockAt(x, minLocation.blockY - 1, z)
                    block.type.isSolid
                }
                deferredResults.add(deferred)
            }
        }
        val results = deferredResults.awaitAll()
        return results.all { true }
    }
    operator fun contains(point: Location) =
        point.world.name == worldName
                && point.blockX in xRange
                && point.blockY in yRange
                && point.blockZ in zRange

    operator fun contains(player: Player) =
        contains(player.location)

    /**
     * 是否与某个区域相交
     */
    fun overlap(another:Zone): Boolean {
        return getVertex().any { it in another } || another.getVertex().any{ it in this }
    }
}