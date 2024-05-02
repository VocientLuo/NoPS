package com.xluo.nops

import com.ajj.module_canvas_lxz.utils.CacheUtil
import com.xluo.lib_base.BaseApplication

class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        CacheUtil.init(this)
    }
}