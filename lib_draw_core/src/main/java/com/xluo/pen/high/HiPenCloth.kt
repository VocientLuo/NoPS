package com.xluo.pen.high

import com.xluo.draw_core.R

/**
 * 高阶织物笔
 */
class HiPenCloth(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.cloth
        name = "织物"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }
}