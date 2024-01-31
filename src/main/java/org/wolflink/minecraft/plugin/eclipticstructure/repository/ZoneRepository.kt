package org.wolflink.minecraft.plugin.eclipticstructure.repository

import org.bukkit.Location
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Zone
import org.wolflink.minecraft.wolfird.framework.database.repository.ListRepository

/**
 * Zone 区域仓库，世界名 -> 区域对象
 */
object ZoneRepository: ListRepository<String,Zone>() {
    override fun getPrimaryKey(p0: Zone) = p0.worldName

    /**
     *  查找坐标存在的区域
     */
    fun findByLocation(location: Location) = findAll().filter { location in it }
}