package com.xluo.nops.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DebounceUtils {
    private var debounceJob: Job? = null
    private const val delay = 1000L

    fun debounce(
        action: suspend CoroutineScope.() -> Unit
    ) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(delay)
            action()
        }
    }
}