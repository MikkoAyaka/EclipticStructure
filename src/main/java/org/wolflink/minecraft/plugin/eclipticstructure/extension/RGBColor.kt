package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Color

val RED_COLOR: Color = Color.fromRGB(255, 128, 128) // 红色
val DEEP_RED_COLOR: Color = Color.fromRGB(255, 0, 0) // 深红色

val GREEN_COLOR: Color = Color.fromRGB(128,255,128) // 绿色
val DEEP_GREEN_COLOR: Color = Color.fromRGB(0,255,0) // 深绿色

private val HEX_CODE = arrayOf('0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F')

/**
 * 将颜色对象转为例如 AFAF00 的十六进制颜色字符
 */
fun Color.toHex(): String {
    val chars = arrayOf('0','0','0','0','0','0')
    chars[0] = HEX_CODE[red / 16]
    chars[1] = HEX_CODE[red % 16]
    chars[2] = HEX_CODE[green / 16]
    chars[3] = HEX_CODE[green % 16]
    chars[4] = HEX_CODE[blue / 16]
    chars[5] = HEX_CODE[blue % 16]
    return chars.joinToString(separator = "")
}