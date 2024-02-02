package org.wolflink.minecraft.plugin.eclipticstructure.extension

import org.bukkit.Color

val RED_COLOR: Color = Color.fromRGB(255, 128, 128) // 红色
val DEEP_RED_COLOR: Color = Color.fromRGB(255, 0, 0) // 深红色

val GREEN_COLOR: Color = Color.fromRGB(128,255,128) // 绿色
val DEEP_GREEN_COLOR: Color = Color.fromRGB(0,255,0) // 深绿色

fun Color.toHexString(): String {
    val chars = arrayOf('0','0','0','0','0','0')
    chars[0] = '0' + red % 16
    chars[1] = '0' + red / 16
    chars[2] = '0' + green % 16
    chars[3] = '0' + green / 16
    chars[4] = '0' + blue % 16
    chars[5] = '0' + blue / 16
    return chars.joinToString()
}