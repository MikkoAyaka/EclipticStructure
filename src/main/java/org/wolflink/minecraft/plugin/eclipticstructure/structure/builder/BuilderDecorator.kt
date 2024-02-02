package org.wolflink.minecraft.plugin.eclipticstructure.structure.builder

import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import org.wolflink.minecraft.plugin.eclipticstructure.config.MESSAGE_PREFIX
import org.wolflink.minecraft.plugin.eclipticstructure.config.STRUCTURE_BUILDER_START_BUILDING
import org.wolflink.minecraft.plugin.eclipticstructure.coroutine.EStructureScope
import org.wolflink.minecraft.plugin.eclipticstructure.display.ESHologram
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderCompletedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderDestroyedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderStartedEvent
import org.wolflink.minecraft.plugin.eclipticstructure.event.BuilderStatusEvent
import org.wolflink.minecraft.plugin.eclipticstructure.extension.GREEN_DUST_PARTICLE_OPTIONS
import org.wolflink.minecraft.plugin.eclipticstructure.extension.RED_DUST_PARTICLE_OPTIONS

/**
 * 建造者装饰器
 * 管理 StructureBuilder 的全息显示对象、区域粒子动画、玩家提示信息等
 */
class BuilderDecorator(private val builder: Builder) : IBuilderListener {
    private val hologram by lazy {
        ESHologram(
            builder.uniqueName, builder.buildLocation, listOf(
                "§7[ §r%${builder.uniqueName}_status% §7] §r%${builder.uniqueName}_structurename% §8| §f剩余 %${builder.uniqueName}_timeleft%",
                "§r",
                "§f%${builder.uniqueName}_progress%",
                "§f",
            )
        )
    }

    override fun completed(e: BuilderCompletedEvent) {
        val location = builder.buildLocation
        EStructureScope.launch {
            completeEffect(location)
            // 在特效显示完成后删除全息显示
            hologram.delete()
        }
    }

    /**
     * 在建造完成后播放音效并显示粒子效果
     */
    private suspend fun completeEffect(location: Location) {
        EStructureScope.launch {
            builder.zone.display(5) { w, x, y, z ->
                w.spawnParticle(
                    Particle.DUST_COLOR_TRANSITION,
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    3,
                    GREEN_DUST_PARTICLE_OPTIONS
                ); // 30 是粒子的数量
            }
        }
        EclipticStructure.runTask {
            location.world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 2f, 1f)
        }
    }

    override fun started(e: BuilderStartedEvent) {
        e.player.sendMessage(MESSAGE_PREFIX + STRUCTURE_BUILDER_START_BUILDING)
        hologram.create()
    }

    private suspend fun onCheckFailed() {
        while (builder.status != Builder.Status.IN_PROGRESS) {
            builder.zone.display(1) { w, x, y, z ->
                w.spawnParticle(
                    Particle.DUST_COLOR_TRANSITION,
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    3,
                    RED_DUST_PARTICLE_OPTIONS
                ); // 30 是粒子的数量
            }
        }
    }

    override fun toggleStatus(e: BuilderStatusEvent) {
        if (e.to == Builder.Status.ZONE_HAS_PLAYER
            || e.to == Builder.Status.ZONE_NO_FLOOR
            || e.to == Builder.Status.ZONE_NOT_EMPTY
        )
            EStructureScope.launch {
                onCheckFailed()
            }
    }

    override fun destroyed(e: BuilderDestroyedEvent) {
        playDestroyedSound(e.builder.buildLocation)
    }

    private fun playDestroyedSound(location: Location) {
        location.world.playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1f)
        location.world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2f, 0.6f)
    }
}