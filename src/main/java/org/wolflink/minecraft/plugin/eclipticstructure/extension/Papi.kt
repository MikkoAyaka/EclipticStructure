package org.wolflink.minecraft.plugin.eclipticstructure.extension

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

fun String.parsePapi(player: Player) = PlaceholderAPI.setPlaceholders(player,this)
