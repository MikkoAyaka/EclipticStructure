package org.wolflink.minecraft.plugin.eclipticstructure.structure

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.wolflink.minecraft.plugin.eclipticstructure.extension.takeItems

/**
 * 建筑结构 建造者
 * @param structure     建筑结构
 * @param buildSeconds  建造用时
 * @param requiredItems 建筑材料(不可重复的 ItemStack)
 */
class StructureBuilder(val structure: Structure,val buildSeconds: Int,vararg val requiredItems: ItemStack) {
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
    private val leftSeconds = buildSeconds

    /**
     * 准备进行建造
     */
    fun onStart(player: Player) {
        // 状态判定
        if (status != Status.NOT_STARTED) return
        // 玩家是否拥有足够材料
        if (!player.takeItems(*requiredItems)) return
        // 开始建造
        startBuilding()
    }
    fun startBuilding() {
        status = Status.IN_PROGRESS
    }
}
