package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 判断玩家背包中是否包含物品
  */
fun Player.hasItems(items: Set<ItemStack>) = hasItems(items.associate { it.type to it.amount })
fun Player.hasItems(items: Map<Material,Int>):Boolean {
    val inventoryItems = this.inventory
        .filterNotNull()
        .groupingBy { it.type }
        .fold(0) { acc, item -> acc + item.amount }
    for (item in items) {
        if(inventoryItems.getOrDefault(item.key,0) < item.value) return false
    }
    return true
}
/**
 * 检查后取走玩家背包中指定数量的物品
 * @param items 不重复的 ItemStack
  */
fun Player.takeItems(vararg items: ItemStack):Boolean {
    if(!this.hasItems(items.toSet())) return false
    this.inventory.removeItem(*items)
    return true
}