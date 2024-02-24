package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.util.BoundingBox
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.META_BLOCK_BREAKABLE
import org.wolflink.minecraft.plugin.eclipticstructure.event.builder.BuilderCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureZoneRelationRepository
import org.wolflink.minecraft.plugin.eclipticstructure.repository.ZoneRepository
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder
import java.util.Random

object StructureDurabilityListener: Listener {

    // 检查建筑周围存在怪物的半径
    private const val MONSTER_CHECK_RADIUS = 4.0
    // 每只怪物对建筑造成的伤害
    private const val PER_MONSTER_DAMAGE = 0
    private val random = Random()
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerBreak(e: BlockBreakEvent) {
        if(!e.block.hasMetadata(META_BLOCK_BREAKABLE)) return
        val breakable = e.block.getMetadata(META_BLOCK_BREAKABLE).firstOrNull()?.asBoolean()!!
        if(!breakable) {
            val structure = ZoneRepository.findByLocation(e.block.location)
                .firstNotNullOfOrNull(StructureZoneRelationRepository::find1) ?: return
            e.isDropItems = false
            e.expToDrop = 0
            e.isCancelled = true
            structure.doRepair(1.0 / structure.builder.blockAmount * structure.blueprint.maxDurability * 5)
        }
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
            .mapNotNull(StructureZoneRelationRepository::find1)
            // 建造完成的建筑结构
            .filter { it.builder.status == Builder.Status.COMPLETED }
            // 造成 20% + 2000 最大耐久值的损害
            .forEach{
                it.doDamage(
                    0.2 * it.blueprint.maxDurability + 2000,
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
            val monsters = world.getNearbyEntities(box) { it is Monster }.filter { (it as LivingEntity).hasAI() }.toList()
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