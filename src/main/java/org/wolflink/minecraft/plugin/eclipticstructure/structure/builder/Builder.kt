package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BaseBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.entity.Player
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.config.MESSAGE_PREFIX
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_INSUFFICIENT_ITEMS
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_STATUS_ERROR
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_ZONE_OVERLAP
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.event.*
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.extension.getRelative
import org.wolflink.minecraft.plugin.eclipticstructure.extension.takeItems
import org.wolflink.minecraft.plugin.eclipticstructure.repository.BuilderRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureZoneRelationRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository
import org.wolflink.minecraft.plugin.eclipticstructure.structure.registry.StructureRegistry
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Zone
import java.util.concurrent.atomic.AtomicInteger

/**
 * 建筑结构 建造者
 * TODO 利用 Bukkit 事件将业务代码分散到事件监听器中，实现面向切面开发
 */
class Builder(
    val structureTypeName: String,
    val buildLocation: Location,
    val pasteAir: Boolean,
) {
    val decorator = BuilderDecorator(this)
    companion object {
        val AUTOMATIC_ID = AtomicInteger(0)
    }
    val id = AUTOMATIC_ID.getAndIncrement()
    val uniqueName = "esbuilder-$id"
    init {
        BuilderRepository.insert(this)
    }
    private val structureMeta = StructureRegistry.get(structureTypeName) ?: throw NullPointerException("$structureTypeName 未被注册")
    val blueprint = structureMeta.blueprint
    private val clipboard = blueprint.loadClipboard()
    private val blockMap = mutableMapOf<BlockVector3,BaseBlock>()
    init {
        clipboard.forEach {
            val vector = BlockVector3.at(it.blockX,it.blockY,it.blockZ)
            blockMap[vector] = it.getFullBlock(clipboard)
        }
    }
    // 方块数量(空气除外)
    val blockAmount = blockMap.filterNot { it.value.material.isAir }.size
    // 建筑占用区域(不可重复)
    val zone = Zone.create(buildLocation.world, clipboard.getRelative(buildLocation), clipboard)
    private val structure = structureMeta.structureSupplier(this)
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
        set(value) {
            if(value == field) return
            BuilderStatusEvent(this,field,value).call()
            field = value
        }

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
        StructureInitializedEvent(structure).call()
        // 开始建造
        EStructureScope.launch {
            BuilderStartedEvent(this@Builder,player).call()
            startBuilding()
        }
    }
    // 指针当前方块未建造
    private var nowIndex = 0
    // 放置每个方块的平均延时毫秒
    private val averageDelayMills: Long = (blueprint.buildSeconds / blockMap.size.toDouble() * 1000).toLong()
    val buildProgress get() = nowIndex.toDouble() / blockMap.size
    val buildTimeLeft get() = blueprint.buildSeconds - (blueprint.buildSeconds * buildProgress).toInt()
    private suspend fun startBuilding() {
        status = Status.IN_PROGRESS
        // 建筑前检查
        buildCheck()
        // 放置方块
        placeBlocks()
        status = Status.COMPLETED
        // 抛出事件
        BuilderCompletedEvent(this,structure).call()
        StructureCompletedEvent(StructureZoneRelationRepository.find1(zone))
    }
    private suspend fun buildCheck() {
        while (true) {
            if(!zone.isEmpty()) status = Status.ZONE_NOT_EMPTY
            else if(zone.players.isNotEmpty()) status = Status.ZONE_HAS_PLAYER
            else if(!zone.hasFloor()) status = Status.ZONE_NO_FLOOR
            else {
                status = Status.IN_PROGRESS
                break
            }
            delay(5 * 1000)
        }
    }
    private suspend fun placeBlocks() {
        val minLocation = zone.minLocation
        val list = blockMap.toList()
        val bukkitWorld = minLocation.world
        val weWorld = BukkitAdapter.adapt(bukkitWorld)
        while (nowIndex++ < blockMap.size - 1) {
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
                weWorld.setBlock(x, y, z, fullBlock.toBlockState())
                // 播放方块放置音效
                bukkitWorld.playSound(Location(bukkitWorld,x.toDouble(),y.toDouble(),z.toDouble()),material.soundGroup.placeSound,1f,1f)
            }
        }
    }

    /**
     * 摧毁所有方块，不会掉落物品
     * 存在容器则只掉落容器内物品
     *
     * 删除建筑结构和建造者等引用，建筑被摧毁后无法恢复
     */
    fun destroy() {
        zone.forEach { world, x, y, z ->
            val location = Location(world,x.toDouble(),y.toDouble(),z.toDouble())
            val block = world.getBlockAt(location)
            if(block.state is Container) {
                val container = block.state as Container
                container.inventory.forEach { world.dropItemNaturally(location,it) }
            }
            block.type = Material.AIR
        }
        BuilderDestroyedEvent(this).call()
    }
}
