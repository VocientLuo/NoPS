package com.xluo.pen.high

import android.graphics.Canvas
import com.xluo.draw_core.R

/**
 * 高阶柔边笔
 */
class HiPenSoft(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.icon_airbrush
        name = "柔边笔"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }

    override fun onDraw(canvas: Canvas) {
        while (mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            bitmap?.let {
                canvas.drawBitmap(getScaledBitmap(it, point.p), point.x-size/2, point.y-size/2, mPaint)
            }
        }
    }
}