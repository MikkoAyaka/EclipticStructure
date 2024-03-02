package org.wolflink.minecraft.plugin.eclipticstructure.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SingletonJob(private val block: suspend CoroutineScope.() -> Unit) {
    private var jobHolder: Job? = null

    /**
     * 尝试启动，如果任务已经在进行中，不会重复启动
     */
    fun tryLaunch(scope: CoroutineScope) {
        if(jobHolder == null || jobHolder?.isCompleted == true) {
            jobHolder = scope.launch(block = block)
        }
    }
}