package com.xluo.pen.high

import com.xluo.draw_core.R

/**
 * 高阶丝纱
 */
class HiPenOil(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.oil
        name = "丝纱"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }
}