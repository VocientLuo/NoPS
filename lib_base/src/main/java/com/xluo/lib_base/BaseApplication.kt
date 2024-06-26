package com.xluo.lib_base

import android.app.Application
import com.tencent.mmkv.MMKV
import com.xluo.lib_base.file.IFileUtils

open class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        IFileUtils.instance.init(this)
    }
}