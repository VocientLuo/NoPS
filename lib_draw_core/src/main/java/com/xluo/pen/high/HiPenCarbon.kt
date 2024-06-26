package com.xluo.pen.high

import com.xluo.draw_core.R

/**
 * 高阶碳素笔
 */
class HiPenCarbon(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.carbon
        name = "碳笔"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }
}