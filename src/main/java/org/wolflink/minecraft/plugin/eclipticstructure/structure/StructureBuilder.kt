package org.wolflink.minecraft.plugin.eclipticstructure.structure

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BaseBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.config.MESSAGE_PREFIX
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_INSUFFICIENT_ITEMS
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_STATUS_ERROR
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_ZONE_OVERLAP
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureBuilderCompleteEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureBuilderStartEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.RED_DUST_PARTICLE_OPTIONS
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.extension.getRelative
import org.wolflink.minecraft.plugin.eclipticstructure.extension.takeItems
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureBuilderRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository
import java.util.concurrent.atomic.AtomicInteger

/**
 * 建筑结构 建造者
 * TODO 利用 Bukkit 事件将业务代码分散到事件监听器中，实现面向切面开发
 */
class StructureBuilder(
    val structureTypeName: String,
    val buildLocation: Location,
    val pasteAir: Boolean,
) {
    companion object {
        val AUTOMATIC_ID = AtomicInteger(0)
    }
    val id = AUTOMATIC_ID.getAndIncrement()
    val uniqueName = "StructureBuilder-$id"
    init {
        StructureBuilderRepository.insert(this)
    }
    val structureMeta = StructureRegistry.get(structureTypeName) ?: throw NullPointerException("$structureTypeName 未被注册")
    val blueprint = structureMeta.blueprint
    private val clipboard = blueprint.loadClipboard()
    private val blockMap = mutableMapOf<BlockVector3,BaseBlock>()
    init {
        clipboard.forEach {
            val vector = BlockVector3.at(it.blockX,it.blockY,it.blockZ)
            blockMap[vector] = it.getFullBlock(clipboard)
        }
    }
    // 建筑占用区域(不可重复)
    val zone = Zone.create(buildLocation.world,clipboard.getRelative(buildLocation),clipboard)

    /**
     * 建造状态
     */
    enum class Status(val msg: String) {
        NOT_STARTED("§e未开始"),
        IN_PROGRESS("§f建造中"),
        ZONE_NOT_EMPTY("§c需要空间"),
        ZONE_HAS_PLAYER("§c存在玩家"),
        ZONE_NO_FLOOR("§c需要地板"),
        COMPLETED("§a已完成")
    }

    var status: Status = Status.NOT_STARTED

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
            StructureBuilderStartEvent(this@StructureBuilder,player).call()
            startBuilding()
        }
    }
    // 指针当前方块未建造
    private var nowIndex = 0
    // 放置每个方块的平均延时毫秒
    private val averageDelayMills: Long = (blueprint.buildSeconds / blockMap.size.toDouble() * 1000).toLong()
    fun getBuildProgress() = nowIndex.toDouble() / blockMap.size
    fun getBuildTimeLeft() = blueprint.buildSeconds - (blueprint.buildSeconds * getBuildProgress()).toInt()
    private suspend fun startBuilding() {
        status = Status.IN_PROGRESS
        // 剩余待建造的方块总数
        val leftBlockCount = blockMap.size - nowIndex
        // 最小坐标
        val minLocation = zone.minLocation
        val bukkitWorld = buildLocation.world
        // WorldEdit 世界对象
        val world = BukkitAdapter.adapt(bukkitWorld)
        // 判断区域是否有足够的空间 并且空间内没有玩家 并且空间有地板支撑
        while (true) {
            if(!zone.isEmpty()) status = Status.ZONE_NOT_EMPTY
            else if(zone.players.isNotEmpty()) status = Status.ZONE_HAS_PLAYER
            else if(!zone.hasFloor()) status = Status.ZONE_NO_FLOOR
            else {
                status = Status.IN_PROGRESS
                break
            }
            zone.display(5) { w, x, y, z ->
                w.spawnParticle(Particle.DUST_COLOR_TRANSITION, x+0.5,y+0.5,z+0.5, 3, RED_DUST_PARTICLE_OPTIONS); // 30 是粒子的数量
            }
        }
        val list = blockMap.toList()
        // 放置方块
        while (nowIndex++ < leftBlockCount - 1) {
            val pair = list[nowIndex]
            val blockVector = pair.first
            val fullBlock = pair.second
            delay(averageDelayMills)
            if(fullBlock.blockType.material.isAir && !pasteAir) continue
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
        StructureBuilderCompleteEvent(this).call()
    }
}
