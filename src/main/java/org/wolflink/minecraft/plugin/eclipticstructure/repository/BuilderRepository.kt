package org.wolflink.minecraft.plugin.eclipticstructure.repository

import org.wolflink.minecraft.plugin.eclipticstructure.structure.builder.Builder
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository

object BuilderRepository: MapRepository<Int, Builder>() {
    override fun getPrimaryKey(p0: Builder) = p0.id
}