package org.wolflink.minecraft.plugin.eclipticstructure.file

import org.wolflink.minecraft.plugin.eclipticstructure.EclipticStructure
import java.io.File

object FileFolders {
    val dataFolder = EclipticStructure.instance.dataFolder
    val schemFolder = File(dataFolder,"schematics")

    fun init() {
        if(!dataFolder.exists()) dataFolder.mkdirs()
        if(!schemFolder.exists()) schemFolder.mkdirs()
    }
}