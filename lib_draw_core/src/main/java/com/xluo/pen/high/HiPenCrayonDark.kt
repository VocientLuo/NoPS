package com.xluo.pen.high

import com.xluo.draw_core.R

/**
 * 高阶木炭
 */
class HiPenCrayonDark(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.crayondark
        name = "木炭"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }
}