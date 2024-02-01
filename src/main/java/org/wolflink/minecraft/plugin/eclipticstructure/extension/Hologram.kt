package org.wolflink.minecraft.plugin.eclipticstructure.extension

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import java.util.UUID

object HologramAPI {
    fun createHologram(name: String,location:Location,lines:List<String>,durationInSeconds: Int) {
        val hologram = DHAPI.createHologram(name,location,lines)
        // 延迟后删除
        EStructureScope.launch {
            delay(durationInSeconds * 1000L)
            hologram.delete()
        }
    }
    fun createHologram(location:Location,lines:List<String>,durationInSeconds: Int) {
        createHologram("ES-"+UUID.randomUUID().toString(),location,lines,durationInSeconds)
    }
    fun createHologram(location:Location,lines:List<String>): Hologram =
        DHAPI.createHologram("ES-"+UUID.randomUUID().toString(),location,lines)

}
