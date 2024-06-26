package com.xluo.pen

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.xluo.pen.bean.ControllerPoint
import com.xluo.pen.shape.PenBezier
import kotlin.math.hypot

/**
 * 马克笔
 */
open class PenMark: PenBezier {

    private var lastRectSize: Float = 0f
    private var maxAlphaPoint: Int = 10
    private var currentPoint: Int = 0
    private var halfSize: Float = size/2f

    constructor(paintId: Int): super(paintId) {
        mPaint = Paint(Paint.DITHER_FLAG) // 新创建笔头，不用默认画笔
        mPaint.isAntiAlias = true //锯齿不显示
        mPaint.isDither = true
        mPaint.strokeMiter = 1.0f
    }

    override fun freshPen() {
        super.freshPen()
        mPaint.style = Paint.Style.FILL
        alpha = Color.alpha(color)/5
        mPaint.alpha = alpha
        lastRectSize = 0f
        halfSize = size/2f
        currentPoint = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        while (mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            doNeetToDo(canvas, point, mPaint)
            mCurPoint = point
        }
    }

    private fun doNeetToDo(canvas: Canvas, point: ControllerPoint, paint: Paint) {
        currentPoint++
        if (currentPoint < maxAlphaPoint) {
            paint.alpha = point.alpha
        } else {
            paint.alpha = alpha
        }
        drawLine(
            canvas,
            mCurPoint.x.toDouble(), mCurPoint.y.toDouble(),
            point.x.toDouble(), point.y.toDouble(),
            paint
        )
    }

    /**
     * 沿着path画椭圆
     */
    private fun drawLine(
        canvas: Canvas,
        x0: Double,
        y0: Double,
        x1: Double,
        y1: Double,
        paint: Paint
    ) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个园
        val curDis = hypot(x0 - x1, y0 - y1)
        val sizeA = if (size < 20) {
            size
        } else {
            20
        }
        var steps = 1 + (curDis / (size/sizeA)).toInt()
        val deltaX = (x1 - x0) / steps
        val deltaY = (y1 - y0) / steps
        var x = x0
        var y = y0
        for (i in 0 until steps) {
            lastRectSize = currentPoint.toFloat()
            if (lastRectSize > size) {
                lastRectSize = size.toFloat()
            }
            val oval = RectF()
            oval.left = (x - lastRectSize / 2.0f).toFloat()
            oval.top = (y - lastRectSize / 2.0f).toFloat()
            oval.right = (x + lastRectSize / 2.0f).toFloat()
            oval.bottom = (y + lastRectSize / 2.0f).toFloat()
            //最基本的实现，通过点控制线，绘制椭圆
            canvas.drawOval(oval, paint)
            x += deltaX
            y += deltaY
        }
    }
}