package com.xluo.pen.shape

import android.graphics.Canvas
import com.xluo.pen.shape.PenShape
import kotlin.math.max
import kotlin.math.min

/**
 * 三角形画笔
 */
class PenTriangle: PenShape {

    constructor(paintId: Int) : super(paintId)

    override fun touchDown(startX: Float, startY: Float) {
        super.touchDown(startX, startY)
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
        if (mStartX == 0f || mStartY == 0f || mEndX.toInt() == 0 || mEndY.toInt() == 0) {
            return
        }
        mPath.reset()
        val pX1 = mStartX
        val pY1 = mEndY
        val pX2 = (mEndX-mStartX)/2+mStartX
        val pY2 = mStartY
        val pX3 = mEndX
        val pY3 = mEndY
        mPath.moveTo(pX1, pY1)
        mPath.quadTo(pX1, pY1, pX2, pY2)
        mPath.quadTo(pX2, pY2, pX3, pY3)
        mPath.quadTo(pX3, pY3, pX1, pY1)
        canvas.drawPath(mPath, mPaint)
    }

    override fun freshRectF(x: Float, y: Float) {
        rectF.left = min(mStartX, mEndX)
        rectF.right = max(mStartX, mEndX)
        rectF.top = min(mStartY, mEndY)
        rectF.bottom = max(mStartY, mEndY)
    }

}