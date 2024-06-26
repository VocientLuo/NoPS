package com.xluo.pen.high

import android.graphics.Canvas
import android.graphics.Rect
import com.xluo.pen.bean.ControllerPoint

/**
 * 高阶像素笔
 */
class HiPenPixel(id: Int) : HiPenBase(id) {
    var canvasWidth = 0
    var canvasHeight = 0
    init {
        name = "像素笔"
        size = 25
    }

    override fun reset() {
        super.reset()
        size = 25
    }


    override fun onDraw(canvas: Canvas) {
        if (canvasWidth < 1 || canvasHeight < 1) {
            return
        }
        while (mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            canvas.drawRect(getDrawRect(point), mPaint)
        }
    }

    private fun getDrawRect(point: ControllerPoint): Rect {
        val leftCount = (point.x/size).toInt()
        val topCount = (point.y/size).toInt()
        val left = leftCount*size
        val top = topCount*size
        var right = left+size
        var bottom = top+size
        if (right > canvasWidth-1) {
            right = canvasWidth-1
        }
        if (bottom > canvasHeight-1) {
            bottom = canvasHeight-1
        }
        return Rect(left, top, right, bottom)
    }
}