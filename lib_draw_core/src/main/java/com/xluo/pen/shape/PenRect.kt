package com.xluo.pen.shape

import android.graphics.Canvas
import android.graphics.Rect
import com.xluo.pen.shape.PenShape
import kotlin.math.max
import kotlin.math.min

/**
 * 矩形画笔
 */
open class PenRect: PenShape {

    constructor(paintId: Int) : super(paintId)

    override fun touchDown(startX: Float, startY: Float) {
        mStartX = startX
        mStartY = startY
    }

    override fun touchMove(currentX: Float, currentY: Float) {
        mEndX = currentX
        mEndY = currentY
    }

    override fun touchUp(endX: Float, endY: Float) {
        super.touchUp(endX, endY)
        mEndY = endY
        mEndX = endX
        freshRectF(endX, endY)
    }

    override fun onDraw(canvas: Canvas) {
        if (mStartX.toInt() === 0 || mStartY.toInt() === 0 || mEndX.toInt() == 0 || mEndY.toInt() == 0) {
            return
        }
        val rect = Rect(mStartX.toInt(), mStartY.toInt(), mEndX.toInt(), mEndY.toInt())
        canvas.drawRect(rect, mPaint)
    }

    override fun freshRectF(x: Float, y: Float) {
        rectF.left = min(mStartX, mEndX)
        rectF.right = max(mStartX, mEndX)
        rectF.top = min(mStartY, mEndY)
        rectF.bottom = max(mStartY, mEndY)
    }
}