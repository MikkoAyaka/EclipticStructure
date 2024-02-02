package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureRepository
import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder
import java.util.concurrent.atomic.AtomicInteger

abstract class Structure(
    val blueprint: Blueprint,
    val builder: Builder,
) {
    companion object {
        val AUTOMATIC_ID = AtomicInteger(0)
    }
    val decorator by lazy { StructureDecorator(this) }
    val handler by lazy { StructureHandler(this) }
    val zone = builder.zone
    val id = AUTOMATIC_ID.getAndIncrement()
    val uniqueName = "esstructure_$id"
    var durability = blueprint.maxDurability
    var available = false

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
    fun doDamage(damage: Double,source: DamageSource) {
        doDamage(damage.toInt(),source)
    }
    fun doDamage(damage: Int, source: DamageSource) {
        durability -= damage
        if(durability <= 0) {
            destroy()
        }
    }
}