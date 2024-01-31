package org.wolflink.minecraft.plugin.eclipticstructure.coroutine

import kotlinx.coroutines.*

object EStructureScope: CoroutineScope by CoroutineScope(Dispatchers.Default)