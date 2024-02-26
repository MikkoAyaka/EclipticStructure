package org.wolflink.minecraft.plugin.eclipticstructure.extension

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun String.parsePapi(player: OfflinePlayer) = PlaceholderAPI.setPlaceholders(player,this)
