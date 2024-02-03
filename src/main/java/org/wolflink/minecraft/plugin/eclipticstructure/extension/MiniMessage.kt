package org.wolflink.minecraft.plugin.eclipticstructure.extension

import net.kyori.adventure.text.minimessage.MiniMessage

val MINI_MESSAGE = MiniMessage.miniMessage()
fun String.toComponent() = MINI_MESSAGE.deserialize(this)

val SPLITER_COLOR = "<#9C9C9C>"
val SECONDARY_TEXT_COLOR = "<#E8E8E8>"
val PRIMARY_TEXT_COLOR = "<#FFFAFA>"
