package org.wolflink.minecraft.plugin.eclipticstructure.structure

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BaseBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustTransition
import org.bukkit.entity.Player
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.config.*
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.extension.getRelative
import org.wolflink.minecraft.plugin.eclipticstructure.extension.takeItems
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository

/**
 * 建筑结构 建造者
 */
class StructureBuilder(
    val structureTypeName: String,
    val buildLocation: Location,
    val pasteAir: Boolean,
) {
    val structureMeta = StructureRegistry.get(structureTypeName) ?: throw NullPointerException("$structureTypeName 未被注册")
    val blueprint = structureMeta.blueprint
    private val clipboard = blueprint.loadClipboard()
    // 建筑占用区域(不可重复)
    val zone = Zone.create(buildLocation.world,clipboard.getRelative(buildLocation),clipboard)

    /**
     * 建造状态
     */
    internal enum class Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    private var status: Status = Status.NOT_STARTED

    // 当前剩余时间
    private val leftSeconds = blueprint.buildSeconds

    private fun canBuild(player: Player): Boolean {
        // 状态异常
        if (status != Status.NOT_STARTED) {
            player.sendMessage(MESSAGE_PREFIX + STRUCTURE_BUILDER_STATUS_ERROR)
            return false
        }
        // 玩家缺少足够的材料
        if (!player.takeItems(*blueprint.requiredItems)) {
            player.sendMessage(MESSAGE_PREFIX + STRUCTURE_BUILDER_INSUFFICIENT_ITEMS)
            return false
        }
        // 空间存在重叠
        if(ZoneRepository.findByOverlap(zone).isNotEmpty()) {
            player.sendMessage(MESSAGE_PREFIX + STRUCTURE_BUILDER_ZONE_OVERLAP)
            return false
        }
        return true
    }
    /**
     * 准备进行建造
     */
    fun build(player: Player) {
        // 建筑前检查
        if(!canBuild(player)) return
        // 保存建筑结构对象至仓库
        StructureRepository.insert(structureMeta.structureSupplier(this))
        // 保存建筑结构区域至仓库
        ZoneRepository.insert(zone)
        // 开始建造
        EStructureScope.launch {
            startBuilding()
        }
        player.sendMessage(MESSAGE_PREFIX + STRUCTURE_BUILDER_START_BUILDING)
    }
    // 指针当前方块未建造
    private var nowIndex = 0
    private suspend fun startBuilding() {
        status = Status.IN_PROGRESS
        val blockMap = mutableMapOf<BlockVector3,BaseBlock>()
        clipboard.forEach {
            val vector = BlockVector3.at(it.blockX,it.blockY,it.blockZ)
            blockMap[vector] = it.getFullBlock(clipboard)
        }
        // 剩余待建造的方块总数
        val leftBlockCount = blockMap.size - nowIndex
        // 放置每个方块的平均延时毫秒
        val averageDelayMills: Long = (blueprint.buildSeconds / leftBlockCount.toDouble() * 1000).toLong()
        // 最小坐标
        val minLocation = zone.minLocation
        val bukkitWorld = buildLocation.world
        // WorldEdit 世界对象
        val world = BukkitAdapter.adapt(bukkitWorld)
        // 粒子效果 TODO 移动到单独的文件中
        // 创建起始颜色和结束颜色
        val startColor: Color = Color.fromRGB(255, 128, 128) // 红色
        val endColor: Color = Color.fromRGB(255, 0, 0) // 红色变深
        // 创建粒子效果的选项
        val dustOptions = DustTransition(startColor, endColor, 2.0f) // 1.0f 是粒子的大小
        // 判断区域是否有足够的空间 并且空间内没有玩家 并且空间有地板支撑
        while (!zone.isEmpty() || zone.players.isNotEmpty() || !zone.hasFloor()) {
            zone.display(5) { w, x, y, z ->
                w.spawnParticle(Particle.DUST_COLOR_TRANSITION, x+0.5,y+0.5,z+0.5, 3, dustOptions); // 30 是粒子的数量
            }
        }
        // 放置脚手架
//        zone.forEach { _, x, y, z ->
//            EclipticStructure.runTask {
//                world.setBlock(x,y,z,BukkitAdapter.asBlockType(Material.SCAFFOLDING)!!.defaultState)
//            }
//        }
        val list = blockMap.toList()
        // 放置方块
        while (nowIndex++ < leftBlockCount - 1) {
            val pair = list[nowIndex]
            val blockVector = pair.first
            val fullBlock = pair.second
            if(fullBlock.blockType.material.isAir && !pasteAir) continue
            delay(averageDelayMills)
            // 坐标计算
            val x = minLocation.blockX + blockVector.blockX
            val y = minLocation.blockY + blockVector.blockY
            val z = minLocation.blockZ + blockVector.blockZ
            val material = BukkitAdapter.adapt(fullBlock)
            EclipticStructure.runTask {
                // 放置方块
                world.setBlock(x, y, z, fullBlock.toBlockState())
                // 播放方块放置音效
                bukkitWorld.playSound(Location(bukkitWorld,x.toDouble(),y.toDouble(),z.toDouble()),material.soundGroup.placeSound,1f,1f)
            }
        }
        status = Status.COMPLETED
    }
}
