package org.wolflink.minecraft.plugin.eclipticstructure.extension

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import java.io.File

fun File.loadAsClipboard():Clipboard? {
    val clipboardFormat = ClipboardFormats.findByFile(this)
    return clipboardFormat?.getReader(this.inputStream())?.read()
}