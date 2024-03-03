package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BaseBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.META_BLOCK_BREAKABLE
import org.wolflink.minecraft.plugin.eclipticstructure.META_STRUCTURE_ID
import org.wolflink.minecraft.plugin.eclipticstructure.config.MESSAGE_PREFIX
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_STATUS_ERROR
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_ZONE_OVERLAP
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.esLogger
import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.*
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureInitializedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.extension.getRelative
import org.wolflink.minecraft.plugin.eclipticstructure.extension.toComponent
import org.wolflink.minecraft.plugin.eclipticstructure.repository.BuilderRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository
import org.wolflink.minecraft.plugin.eclipticstructure.structure.Zone
import org.wolflink.minecraft.plugin.eclipticstructure.structure.registry.StructureRegistryItem
import java.util.concurrent.atomic.AtomicInteger

/**
 * 建筑结构 建造者
 * TODO 利用 Bukkit 事件将业务代码分散到事件监听器中，实现面向切面开发
 */
class Builder(
    private val structureLevel: Int,
    structureMeta: StructureRegistryItem,
    buildLocation: Location,
) {
    val buildLocation = buildLocation.toCenterLocation()
    val decorator = BuilderDecorator(this)
    companion object {
        val AUTOMATIC_ID = AtomicInteger(0)
    }
    val id = AUTOMATIC_ID.getAndIncrement()
    val uniqueName = "esbuilder_$id"
    init {
        BuilderRepository.insert(this)
    }
    val blueprint = structureMeta.blueprints[structureLevel-1]
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
    val structure by lazy { structureMeta.structureSupplier(structureLevel,this) }
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
            player.sendMessage("$MESSAGE_PREFIX $STRUCTURE_BUILDER_STATUS_ERROR".toComponent())
            return false
        }
        // 空间存在重叠
        if(ZoneRepository.findByOverlap(zone).isNotEmpty()) {
            player.sendMessage("$MESSAGE_PREFIX $STRUCTURE_BUILDER_ZONE_OVERLAP".toComponent())
            return false
        }
        return true
    }

    /**
     * @param player        发起建造的玩家
     * @param broadcast     是否发起公告通知所有玩家
     * @param floorCheck    是否进行地板检测
     * @param playerCheck   是否进行玩家检测
     * @param zoneCheck     是否进行空间检测(需要剩余空间)
     */
    fun build(player: Player,broadcast: Boolean,floorCheck: Boolean,playerCheck: Boolean,zoneCheck: Boolean) {
        EclipticStructure.runTask {
            // 建筑前检查
            if(!canBuild(player)) return@runTask
            val event = BuilderPreBuildEvent(this,player,broadcast,floorCheck,playerCheck,zoneCheck)
            // 外部处理
            Bukkit.getPluginManager().callEvent(event)
            if(event.isCancelled) return@runTask
            if(broadcast) {
                val broadcastText = listOf(
                    "坐标 ${buildLocation.toVector()}",
                )
                Bukkit.broadcast("$MESSAGE_PREFIX <green>${player.name} <white>开始建造 <yellow>${blueprint.structureName} <white>了。<hover:show_text:'<newline>${broadcastText.joinToString(separator = "<newline>")}<newline>'><yellow>[详情]".toComponent())
            }
            EStructureScope.launch {
                StructureInitializedEvent(structure).call()
                BuilderStartedEvent(this@Builder,player).call()
                // 实时更新状态
                asyncStatusUpdate(floorCheck, playerCheck, zoneCheck)
                // 尝试开始建造
                startBuilding()
            }
        }
    }
    // 指针当前方块未建造
    private var nowIndex = 0
    // 放置每个方块的平均延时毫秒
    private val averageDelayMills: Long = (blueprint.buildSeconds / blockMap.size.toDouble() * 1000).toLong()
    val buildProgress get() = nowIndex.toDouble() / blockMap.size
    val buildTimeLeft get() = blueprint.buildSeconds - (blueprint.buildSeconds * buildProgress).toInt()
    // 首次检测，判定是否有足够空间
    private var firstCheck = false
    private suspend fun startBuilding() {
        // 放置方块
        placeBlocks()
        status = Status.COMPLETED
        // 抛出事件
        BuilderCompletedEvent(this,structure).call()
        StructureCompletedEvent(structure).call()
        structure.available = true
    }
    private fun asyncStatusUpdate(floorCheck: Boolean,playerCheck: Boolean,zoneCheck: Boolean) {
        if(!floorCheck && !playerCheck && !zoneCheck) {
            firstCheck = true
            status = Status.IN_PROGRESS
            return
        }
        EStructureScope.launch {
            while (status != Status.COMPLETED) {
                if(zoneCheck && !firstCheck && zone.residualSpacePercent() < 0.75) status = Status.ZONE_NOT_EMPTY
                else if(playerCheck && zone.players.any { it.gameMode != GameMode.SPECTATOR }) status = Status.ZONE_HAS_PLAYER
                else if(floorCheck && !zone.hasFloor()) status = Status.ZONE_NO_FLOOR
                else {
                    firstCheck = true
                    status = Status.IN_PROGRESS
                }
                delay(5 * 1000)
            }
        }
    }
    private suspend fun placeBlocks() {
        val minLocation = zone.minLocation
        val list = blockMap.toList()
        val bukkitWorld = minLocation.world
        val weWorld = BukkitAdapter.adapt(bukkitWorld)
        while (nowIndex < blockMap.size) {
            // 建造状态异常
            if(!firstCheck || status != Status.IN_PROGRESS) {
                delay(1000)
                continue
            }
            val pair = list[nowIndex]
            val blockVector = pair.first
            val fullBlock = pair.second
            delay(averageDelayMills)
            nowIndex++
            if(fullBlock.blockType.material.isAir && !blueprint.pasteAir) continue
            // 坐标计算
            val x = minLocation.blockX + blockVector.blockX
            val y = minLocation.blockY + blockVector.blockY
            val z = minLocation.blockZ + blockVector.blockZ
            val material = BukkitAdapter.adapt(fullBlock)
            EclipticStructure.runTask {
                // 放置方块
                weWorld.setBlock(x, y, z, fullBlock.toBlockState())
                // 方块标签
                bukkitWorld.getBlockAt(x,y,z).also {
                    if(!it.type.isAir) {
                        // 添加不可破坏标签
                        it.setMetadata(META_BLOCK_BREAKABLE,FixedMetadataValue(EclipticStructure.instance,false))
                        // 添加建筑ID标识
                        it.setMetadata(META_STRUCTURE_ID,FixedMetadataValue(EclipticStructure.instance,id))
                    }
                }
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
        val items = mutableSetOf<Pair<Location,ItemStack>>()
        // 清理方块
        zone.forEach { world, x, y, z ->
            val location = Location(world,x.toDouble(),y.toDouble(),z.toDouble())
            val block = world.getBlockAt(location)
            if(block.state is Container) {
                val container = block.state as Container
                container.inventory.filter { it != null && it.type.isItem }.forEach {
                    // 延迟掉落
                    items.add(location to it)
                }
            }
            block.type = Material.AIR
            block.also {
                // 删除不可破坏标签
                it.removeMetadata(META_BLOCK_BREAKABLE,EclipticStructure.instance)
                // 删除建筑ID标识
                it.removeMetadata(META_STRUCTURE_ID,EclipticStructure.instance)
            }
        }
        // 删除建筑本身的掉落物
        zone.world.getNearbyEntities(zone.toBoundingBox()) {
            it.type == EntityType.DROPPED_ITEM
        }.forEach { it.remove() }
        // 掉落应该掉落的物品
        items.forEach { zone.world.dropItemNaturally(it.first,it.second) }
        BuilderDestroyedEvent(this).call()
    }
}
