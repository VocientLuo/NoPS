package com.xluo.pen.shape

import android.graphics.Canvas
import com.xluo.pen.shape.PenShape
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * 多边形画笔
 */
class PenMultiLine: PenShape {

    constructor(paintId: Int) : super(paintId)

    private var mNextStartX = -1f
    private var mNextStartY = -1f

    private var firstPointX = 0f
    private var firstPointY = 0f
    // 线条数
    private var lineNumber = 0
    private val maxLineNumber = 3

    override fun touchDown(startX: Float, startY: Float) {
        if (mNextStartX == -1f && mNextStartY == -1f) {
            mStartX = startX
            mStartY = startY
            firstPointX = startX
            firstPointY = startY
            lineNumber = 0
            initRectF(startX, startY)
        } else {
            mStartX = mNextStartX
            mStartY = mNextStartY
            lineNumber++
        }
    }

    override fun touchMove(currentX: Float, currentY: Float) {
        mEndX = currentX
        mEndY = currentY
    }

    override fun touchUp(endX: Float, endY: Float) {
        mNextStartX = endX
        mNextStartY = endY
        freshRectF(endX, endY)
        val dis = hypot(mEndX-firstPointX, mEndY-firstPointY)
        // 边框闭合时认为是绘制完毕
        if (dis < size && lineNumber >= maxLineNumber) {
            generateRectF()
            reset()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mEndX.toInt() == 0 && mEndY.toInt() == 0) {
            return
        }
        if (mStartX != mEndX || mStartY != mEndY) {
            canvas.drawLine(
                mStartX,
                mStartY,
                mEndX,
                mEndY,
                mPaint
            )
        }
    }

    override fun freshRectF(x: Float, y: Float) {
        if (!rectF.contains(x, y)) {
            rectF.left = min(rectF.left, x)
            rectF.top = min(rectF.top, y)
            rectF.right = max(rectF.right, x)
            rectF.bottom = max(rectF.left, y)
        }
    }

    override fun reset() {
        super.reset()
        mNextStartX = -1f
        mNextStartY = -1f
        firstPointX = 0f
        firstPointY = 0f
    }
}