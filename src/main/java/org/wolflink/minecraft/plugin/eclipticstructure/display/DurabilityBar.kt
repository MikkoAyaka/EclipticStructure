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
        val color =
            if(progress > 0.5) Color.fromRGB((510 * (1 - progress)).toInt(), 255, 0)
            else Color.fromRGB(255, (progress * 510).toInt(),0)
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