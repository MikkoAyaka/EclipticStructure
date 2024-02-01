package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.call
import org.wolflink.minecraft.plugin.eclipticstructure.repository.StructureRepository

abstract class Structure(
    val blueprint: StructureBlueprint,
    val builder: StructureBuilder,
    var maxDurability: Int,// 最大耐久值
) {
    var durability = maxDurability
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
        StructureRepository.deleteByKey(builder.zone)
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