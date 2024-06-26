package com.xluo.pen.high

import android.graphics.Canvas
import com.xluo.draw_core.R

/**
 * 高阶手写笔
 */
class HiPenHand(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.pen_oval
        name = "手写笔"
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