package com.xluo.pen.shape

import android.graphics.Canvas
import com.xluo.pen.shape.PenShape
import kotlin.math.max
import kotlin.math.min

/**
 * 椭圆画笔
 */
class PenOval: PenShape {

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
        canvas.drawOval(mStartX, mStartY, mEndX, mEndY, mPaint)
    }

    override fun freshRectF(x: Float, y: Float) {
        rectF.left = min(mStartX, mEndX)
        rectF.right = max(mStartX, mEndX)
        rectF.top = min(mStartY, mEndY)
        rectF.bottom = max(mStartY, mEndY)
    }

}