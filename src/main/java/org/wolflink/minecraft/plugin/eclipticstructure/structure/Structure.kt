package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureAvailableEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureDurabilityDamageEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.structure.StructureUnavailableEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder
import java.util.concurrent.atomic.AtomicInteger

abstract class Structure(
    val blueprint: Blueprint,
    val builder: Builder
) {
    companion object {
        val AUTOMATIC_ID = AtomicInteger(0)
    }
    abstract val customListener: IStructureListener?
    val decorator by lazy { StructureDecorator(this) }
    val handler by lazy { StructureHandler(this) }
    val zone = builder.zone
    val id = AUTOMATIC_ID.getAndIncrement()
    val uniqueName = "esstructure_$id"
    var durability = blueprint.maxDurability
    var available = false
        set(value) {
            if(value == field) return
            if(value) StructureAvailableEvent(this).call()
            else StructureUnavailableEvent(this).call()
            field = value
        }

    /**
     * 建筑受到损伤的伤害来源
     */
    enum class DamageSource {
        PLAYER_BREAK,// 玩家破坏
        EXPLORATION,// 爆炸
        MONSTER_OCCUPY,// 怪物占领
    }

    /**
     * 摧毁建筑
     */
    private fun destroy() {
        available = false
        builder.destroy()
        StructureDestroyedEvent(this).call()
    }
    /**
     * 对建筑造成伤害
     * TODO 允许使用修饰器调整某一类伤害
     */
    fun doDamage(damage: Double,sourceType: DamageSource,source: Any) {
        doDamage(damage.toInt(),sourceType,source)
    }
    fun doDamage(damage: Int, sourceType: DamageSource,source: Any) {
        val event = StructureDurabilityDamageEvent(this,sourceType,source,damage).apply { call() }
        if(event.isCancelled) return
        durability -= (damage * event.damageMultiple).toInt()
        if(durability <= 0) {
            destroy()
        }
    }
}