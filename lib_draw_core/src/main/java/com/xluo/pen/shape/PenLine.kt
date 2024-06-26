package com.xluo.pen.shape

import android.graphics.Canvas
import kotlin.math.max
import kotlin.math.min

/**
 * 直线画笔
 */
class PenLine: PenShape {

    constructor(paintId: Int) : super(paintId)

    override fun touchMove(currentX: Float, currentY: Float) {
        mEndX = currentX
        mEndY = currentY
    }

    override fun onDraw(canvas: Canvas) {
        if (mEndX.toInt() == 0 && mEndY.toInt() == 0) {
            return
        }
        canvas.drawLine(mStartX, mStartY, mEndX, mEndY, mPaint)
    }

    override fun touchUp(endX: Float, endY: Float) {
        super.touchUp(endX, endY)
        mEndY = endY
        mEndX = endX
        freshRectF(endX, endY)
    }

    override fun freshRectF(x: Float, y: Float) {
        rectF.left = min(mStartX, mEndX)
        rectF.right = max(mStartX, mEndX)
        rectF.top = min(mStartY, mEndY)
        rectF.bottom = max(mStartY, mEndY)
    }
}