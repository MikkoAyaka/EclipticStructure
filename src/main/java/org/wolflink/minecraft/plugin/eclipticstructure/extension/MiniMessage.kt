package org.wolflink.minecraft.plugin.eclipticstructure.extension

import net.kyori.adventure.text.minimessage.MiniMessage

val MINI_MESSAGE = MiniMessage.miniMessage()
fun String.toComponent() = MINI_MESSAGE.deserialize(this)
