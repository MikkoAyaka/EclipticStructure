package org.wolflink.minecraft.plugin.eclipticstructure.structure

import com.sk89q.worldedit.extent.clipboard.Clipboard
import org.bukkit.inventory.ItemStack
import org.wolflink.minecraft.plugin.eclipticstructure.extension.loadAsClipboard
import org.wolflink.minecraft.plugin.eclipticstructure.file.FileFolders
import java.io.File
import java.io.FileNotFoundException

/**
 * 建筑结构 蓝图
 * 存放建筑结构的元数据(未被实例化的数据)
 * @param requiredItems 建筑材料(材质不重复，数量可超过64)
 */
class StructureBlueprint (
    val structureDisplayName: String,
    val schemFileName: String,
    val buildSeconds: Int,
    vararg val requiredItems: ItemStack
) {
    fun loadClipboard(): Clipboard {
        val schemFile = File(FileFolders.schemFolder, schemFileName)
        return schemFile.loadAsClipboard() ?: throw FileNotFoundException("未找到建筑结构文件：$schemFileName")
    }
}