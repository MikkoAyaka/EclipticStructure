package org.wolflink.minecraft.plugin.eclipticstructure.extension

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BaseBlock
import org.bukkit.Location


/**
 * 获取偏移坐标，由 实际坐标 - 剪贴板原点坐标 获得
 */
fun Clipboard.getRelative(location: Location) = location.clone()
    .add(-origin.blockX.toDouble(),-origin.blockY.toDouble(),-origin.blockZ.toDouble())