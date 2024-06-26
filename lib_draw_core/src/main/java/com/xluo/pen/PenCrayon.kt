package com.xluo.pen

import android.graphics.*
import com.xluo.pen.core.TraditionPen
import kotlin.math.abs

/**
 * 蜡笔
 */
class PenCrayon: TraditionPen {

    /**
     * 锯齿大小
     */
    private val segment: Float = 1.0f
    private val deviation: Float = 1.0f

    constructor(paintId: Int): super(paintId) {

        mPaint = Paint(Paint.DITHER_FLAG) // 创建蜡笔，不用默认画笔
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true //锯齿不显示
        mPaint.isDither = true
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        val discretePathEffect = DiscretePathEffect(segment, deviation)
        mPaint.pathEffect = discretePathEffect
    }

    override fun touchDown(startX: Float, startY: Float) {
        mPath.reset()
        mPath.moveTo(startX, startY)
        mStartX = startX
        mStartY = startY
    }

    override fun touchMove(x: Float, y: Float) {
        val dx: Float = abs(x - mStartX)
        val dy: Float = abs(y - mStartY)
        if (dx > 0 || dy > 0) {
            mPath.quadTo(mStartX, mStartY, ((x + mStartX) / 2), ((y + mStartY) / 2))
            mStartX = x
            mStartY = y
        } else if (dx == 0f || dy == 0f) {
            mPath.quadTo(mStartX, mStartY, ((x + 1 + mStartX) / 2), ((y + 1 + mStartY) / 2))
            mStartX = x + 1
            mStartY = y + 1
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
    }
}