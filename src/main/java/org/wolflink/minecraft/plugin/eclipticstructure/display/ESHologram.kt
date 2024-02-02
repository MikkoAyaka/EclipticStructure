package org.wolflink.minecraft.plugin.eclipticstructure.display

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope

class ESHologram(val uniqueName: String,val location: Location,val lines: List<String>) {
    private var hologram: Hologram? = null
    fun create() {
        hologram = DHAPI.createHologram(uniqueName,location.clone().add(0.0,3.0,0.0),lines)
        EStructureScope.launch { check() }
    }
    fun delete() {
        DHAPI.getHologram(uniqueName)?.delete()
        hologram = null
    }
    private suspend fun check() {
        while (hologram?.isEnabled == true) {
            val tempHologram = hologram ?: break
            if(tempHologram.location.clone().add(0.0,-1.0,0.0).block.isSolid
                || tempHologram.location.clone().add(0.0,-2.0,0.0).block.isSolid) {
                DHAPI.moveHologram(tempHologram,tempHologram.location.clone().add(0.0,1.0,0.0))
            }
            delay(1000L)
        }
    }
}