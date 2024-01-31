package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.entity.Player
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository

/**
 * 获取玩家当前所处的空间集合
 */
fun Player.getZones() = ZoneRepository.findByLocation(this.location)