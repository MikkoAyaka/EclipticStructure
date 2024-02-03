package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.util.BoundingBox
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.BuilderCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureZoneRelationRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository
import java.util.Random

object StructureDurabilityListener: Listener {

    // 检查建筑周围存在怪物的半径
    private const val MONSTER_CHECK_RADIUS = 4.0
    // 每只怪物对建筑造成的伤害
    private const val PER_MONSTER_DAMAGE = 30
    private val random = Random()
    @EventHandler
    fun onPlayerBreak(e: BlockBreakEvent) {
        val structure = ZoneRepository.findByLocation(e.block.location).map(StructureZoneRelationRepository::find1).firstOrNull() ?: return
        e.isDropItems = false
        e.expToDrop = 0
        e.isCancelled = true
        structure.doDamage(
            1.0 / structure.builder.blockAmount * structure.blueprint.maxDurability,
            Structure.DamageSource.PLAYER_BREAK,
            e.player
        )
    }
    private fun onExploration(source: Any,worldName:String,blockList: List<Block>) {
        var minX: Int = Int.MAX_VALUE;var minY: Int = Int.MAX_VALUE;var minZ: Int = Int.MAX_VALUE
        var maxX: Int = Int.MIN_VALUE;var maxY: Int = Int.MIN_VALUE;var maxZ: Int = Int.MIN_VALUE
        blockList.map { it.location }.forEach {
            if(it.blockX < minX) minX = it.blockX
            if(it.blockY < minY) minY = it.blockY
            if(it.blockZ < minZ) minZ = it.blockZ

            if(it.blockX > maxX) maxX = it.blockX
            if(it.blockY > maxY) maxY = it.blockY
            if(it.blockZ > maxZ) maxZ = it.blockZ
        }
        val zone = Zone(
            worldName,
            minX..maxX,
            minY..maxY,
            minZ..maxZ
        )
        // 与爆炸重叠的区域
        ZoneRepository.findByOverlap(zone)
            // 被爆炸影响的建筑结构
            .map(StructureZoneRelationRepository::find1)
            // 造成 20% ~ 50% 最大耐久值的损害
            .forEach{
                it.doDamage(
                    random.nextDouble(0.2,0.5) * it.blueprint.maxDurability,
                    Structure.DamageSource.EXPLORATION,
                    source
                )
            }
    }
    @EventHandler
    fun onExploration(e: BlockExplodeEvent) {
        onExploration(e.block,e.block.location.world.name,e.blockList().toList())
        e.blockList().clear()
    }
    @EventHandler
    fun onExploration(e: EntityExplodeEvent) {
        onExploration(e.entity,e.entity.location.world.name,e.blockList().toList())
        e.blockList().clear()
    }
    // Structure - TaskId
    private val taskMap = mutableMapOf<Structure,Int>()
    @EventHandler
    fun onStructureComplete(e: BuilderCompletedEvent) {
        val zone = e.builder.zone
        val world = zone.world
        val box = BoundingBox.of(
            zone.minLocation.clone().add(-MONSTER_CHECK_RADIUS,-MONSTER_CHECK_RADIUS,-MONSTER_CHECK_RADIUS),
            zone.maxLocation.clone().add(MONSTER_CHECK_RADIUS,MONSTER_CHECK_RADIUS,MONSTER_CHECK_RADIUS),
        )
        val structure = e.structure
        val taskId = Bukkit.getScheduler().runTaskTimer(EclipticStructure.instance, Runnable {
            val monsters = world.getNearbyEntities(box) { it is Monster }.toList()
            val monsterAmount = monsters.size
            structure.doDamage(monsterAmount * PER_MONSTER_DAMAGE,Structure.DamageSource.MONSTER_OCCUPY,monsters)
        },20L,20L).taskId
        taskMap[structure] = taskId
    }
    @EventHandler
    fun onStructureDestroy(e: StructureDestroyedEvent) {
        val taskId = taskMap[e.structure] ?: return
        Bukkit.getScheduler().cancelTask(taskId)
        taskMap.remove(e.structure)
    }
}