package com.xluo.pen.high

import android.graphics.Canvas
import com.xluo.draw_core.R

/**
 * 高阶硬圆
 */
class HiPenCircle(id: Int) : HiPenBase(id) {

    init {
        resId = R.mipmap.pen_circle
        name = "硬圆"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }

    override fun freshPen() {
        super.freshPen()
        mPaint.shader = null
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