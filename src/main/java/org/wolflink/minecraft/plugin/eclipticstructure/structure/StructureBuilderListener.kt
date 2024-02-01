package org.wolflink.minecraft.plugin.eclipticstructure.structure

import eu.decentsoftware.holograms.api.DHAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.wolflink.minecraft.plugin.eclipticstructure.config.MESSAGE_PREFIX
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_START_BUILDING
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureBuilderCompleteEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.StructureBuilderStartEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.GREEN_DUST_PARTICLE_OPTIONS

object StructureBuilderListener: Listener {
    private suspend fun createHologram(builder: StructureBuilder) {
        val hologram = DHAPI.createHologram(builder.uniqueName,builder.buildLocation.clone().add(0.0,3.0,0.0),
            listOf(
                "§7[ §r%esbuilder_${builder.id}_status% §7] §r%esbuilder_${builder.id}_structurename% §8| §f剩余 %esbuilder_${builder.id}_timeleft%",
                "§r",
                "§f%esbuilder_${builder.id}_progress%",
                "§f",
            ))
        while (hologram.isEnabled) {
            if(hologram.location.clone().add(0.0,-1.0,0.0).block.isSolid || hologram.location.clone().add(0.0,-2.0,0.0).block.isSolid) {
                DHAPI.moveHologram(hologram,hologram.location.clone().add(0.0,1.0,0.0))
            }
            delay(1000L)
        }
    }
    private fun deleteHologram(uniqueName: String) {
        DHAPI.getHologram(uniqueName)?.delete()
    }
    @EventHandler
    fun onStart(e: StructureBuilderStartEvent) {
        e.player.sendMessage(MESSAGE_PREFIX + STRUCTURE_BUILDER_START_BUILDING)
        EStructureScope.launch {
            createHologram(e.structureBuilder)
        }
    }
    @EventHandler
    fun onComplete(e: StructureBuilderCompleteEvent) {
        val builder = e.structureBuilder
        val location = builder.buildLocation
        val world = location.world
        EStructureScope.launch {
            builder.zone.display(5) { w, x, y, z ->
                w.spawnParticle(Particle.DUST_COLOR_TRANSITION, x+0.5,y+0.5,z+0.5, 3, GREEN_DUST_PARTICLE_OPTIONS); // 30 是粒子的数量
            }
            deleteHologram(builder.uniqueName)
        }
        world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE,2f,1f)
    }
}