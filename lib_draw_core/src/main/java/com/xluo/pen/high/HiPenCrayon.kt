package com.xluo.pen.high

import com.xluo.draw_core.R

/**
 * 高阶磨砂
 */
class HiPenCrayon(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.crayon
        name = "磨砂"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }
}