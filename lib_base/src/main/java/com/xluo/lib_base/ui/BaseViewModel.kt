package com.xluo.lib_base.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel : ViewModel(), CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {

}