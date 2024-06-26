package com.xluo.pen.shape

import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.xluo.pen.core.BasePen

abstract class PenShape(paintId: Int): BasePen(paintId) {
    var drawFinished = false
    // 形状矩形，用于绘制后编辑
    var rectF = RectF(0f, 0f, 0f, 0f)

    abstract fun freshRectF(x: Float, y: Float)

    override fun freshPen() {
        style = Paint.Style.STROKE
        super.freshPen()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touch =  super.onTouchEvent(event)
        if (this !is PenMultiLine) {
            // 多边形笔逻辑不一样，自己去实现
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initRectF(event.x, event.y)
                }
                MotionEvent.ACTION_UP -> {
                    generateRectF()
                }
            }
        }

        return touch
    }

    fun initRectF(x: Float, y: Float) {
        rectF = RectF(x, y, x, y)
        drawFinished = false
    }

    fun generateRectF() {
        val border = size/2+5
        rectF.left -= border
        rectF.top -= border
        rectF.right += border
        rectF.bottom += border
        drawFinished = true
    }
}