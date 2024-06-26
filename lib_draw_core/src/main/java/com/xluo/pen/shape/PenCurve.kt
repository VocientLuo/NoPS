package com.xluo.pen.shape

import android.graphics.Canvas
import kotlin.math.max
import kotlin.math.min

/**
 * 曲线画笔
 */
open class PenCurve : PenShape {

    constructor(paintId: Int) : super(paintId)

    override fun touchDown(startX: Float, startY: Float) {
        super.touchDown(startX, startY)
        mPath.reset()
        //设置曲线开始点
        mPath.moveTo(startX, startY)
        mStartX = startX
        mStartY = startY
    }

    override fun touchMove(currentX: Float, currentY: Float) {
        mPath.quadTo(mStartX, mStartY, (currentX + mStartX) / 2, (currentY + mStartY) / 2)
        mStartX = currentX
        mStartY = currentY
        freshRectF(currentX, currentY)
    }

    override fun touchUp(endX: Float, endY: Float) {
        super.touchUp(endX, endY)
        mPath.quadTo(mStartX, mStartY, endX, endY)
        freshRectF(endX, endY)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
    }

    override fun freshRectF(x: Float, y: Float) {
        if (!rectF.contains(x, y)) {
            rectF.left = min(rectF.left, x)
            rectF.top = min(rectF.top, y)
            rectF.right = max(rectF.right, x)
            rectF.bottom = max(rectF.left, y)
        }
    }
}