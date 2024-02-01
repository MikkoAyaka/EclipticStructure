package org.wolflink.minecraft.plugin.eclipticstructure.repository

import org.bukkit.Location
import org.bukkit.World
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Zone
import org.wolflink.minecraft.wolfird.framework.database.repository.ListRepository

/**
 * Zone 区域仓库，世界名 -> 区域对象
 * 区域是可以重叠的
 */
object ZoneRepository : ListRepository<String, Zone>() {
    override fun getPrimaryKey(p0: Zone) = p0.worldName

    /**
     *  查找坐标存在的区域
     */
    fun findByLocation(location: Location) = findAll().filter { location in it }
    fun findByWorld(world: World) = findAll().filter { it.worldName == world.name }

    /**
     * 查找与该区域存在重合的其它区域
     */
    fun findByOverlap(zone: Zone): Set<Zone> {
        return findAll()
            .filter {// 存在重合可能的区域
                it.maxLocation.x >= zone.minLocation.x
                        && it.maxLocation.y >= zone.minLocation.y
                        && it.maxLocation.z >= zone.minLocation.z
            }
            .filter { it.overlap(zone) } // 互相存在顶点重合
            .filter { it == zone } // 不为自己
            .toSet()
    }
}