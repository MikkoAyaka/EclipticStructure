package org.wolflink.minecraft.plugin.eclipticstructure.display

/**
 * 文本进度条
 * @param progress  当前进度百分比(0.0~1.0)
 * @param length    进度条字符长度(默认20)
 */
class TextProgressBar(var progress: Double, private val length: Int = 20) {
    fun getBar():String {
        var bar = ""
        val finished = (progress * length).toInt()
        val notFinished = length - finished
        bar += "§f"
        repeat(finished) { bar += '░' }
        bar += "§7"
        repeat(notFinished) { bar += '░' }
        return bar
    }
}