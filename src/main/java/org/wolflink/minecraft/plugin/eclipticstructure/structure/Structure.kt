package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.Bukkit
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureAvailableEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureDurabilityDamageEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureUnavailableEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.structure.blueprint.Blueprint
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder
import java.util.concurrent.atomic.AtomicInteger

abstract class Structure(
    val blueprint: Blueprint,
    val builder: Builder
) {
    companion object {
        val AUTOMATIC_ID = AtomicInteger(0)
    }
    abstract val customListeners: List<IStructureListener>
    val decorator by lazy { StructureDecorator(this) }
    val handler by lazy { StructureHandler(this) }
    val zone = builder.zone
    val id = AUTOMATIC_ID.getAndIncrement()
    val uniqueName = "esstructure_$id"
    var durability = blueprint.maxDurability
        private set
    var available = false
        set(value) {
            if(value == field) return
            if(value) StructureAvailableEvent(this).call()
            else StructureUnavailableEvent(this).call()
            field = value
        }
    var destroyed = false
        private set

    /**
     * 建筑受到损伤的伤害来源
     */
    enum class DamageSource {
        PLAYER_BREAK,// 玩家破坏
        EXPLORATION,// 爆炸
        MONSTER_OCCUPY,// 怪物占领
        LACK_OF_ENERGY// 能源不足
    }

    /**
     * 摧毁建筑
     */
    protected fun destroy() {
        available = false
        destroyed = true
        builder.destroy()
        StructureDestroyedEvent(this).call()
    }

    /**
     * 修理建筑
     * TODO 有特殊能力的玩家修理建筑效果翻倍
     */
    fun doRepair(value: Int) {
        if(durability + value > blueprint.maxDurability) durability = blueprint.maxDurability
        else durability += value
    }
    fun doRepair(value: Double) = doRepair(value.toInt())
    /**
     * 对建筑造成伤害
     * TODO 允许使用修饰器调整某一类伤害
     */
    fun doDamage(damage: Double,sourceType: DamageSource,source: Any) {
        doDamage(damage.toInt(),sourceType,source)
    }
    fun doDamage(damage: Int, sourceType: DamageSource,source: Any? = null) {
        EclipticStructure.runTask {
            val event = StructureDurabilityDamageEvent(this,sourceType,source,damage)
            Bukkit.getPluginManager().callEvent(event)
            if(event.isCancelled) return@runTask
            durability -= (damage * event.damageMultiple).toInt()
            if(durability <= 0) {
                durability = 0
                destroy()
            }
        }
    }
}