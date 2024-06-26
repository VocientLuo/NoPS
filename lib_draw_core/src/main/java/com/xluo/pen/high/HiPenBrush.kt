package com.xluo.pen.high

import com.xluo.draw_core.R

/**
 * 高阶毛笔笔
 */
class HiPenBrush(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.six
        name = "毛笔"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }
}