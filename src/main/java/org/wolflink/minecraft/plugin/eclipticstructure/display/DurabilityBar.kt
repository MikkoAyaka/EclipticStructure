package org.wolflink.minecraft.plugin.eclipticstructure.display

import org.bukkit.Color
import org.wolflink.minecraft.plugin.eclipticstructure.extension.toHexString

/**
 * 耐久进度条
 * @param progress  当前进度百分比(0.0~1.0)
 * @param length    进度条字符长度(默认20)
 */
class DurabilityBar(var progress: Double, private val length: Int = 20) {
    fun getBar():String {
        val color = Color.fromRGB(
            ((1 - progress) * 255).toInt(),
            (progress * 255).toInt(),
            0
        )
        var bar = ""
        val finished = (progress * length).toInt()
        val notFinished = length - finished
        bar += "#${color.toHexString()}"
        repeat(finished) { bar += '░' }
        bar += "§7"
        repeat(notFinished) { bar += '░' }
        return bar
    }
}