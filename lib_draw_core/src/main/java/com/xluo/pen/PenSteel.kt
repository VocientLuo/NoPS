package com.xluo.pen

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.xluo.pen.bean.ControllerPoint
import com.xluo.pen.shape.PenBezier
import kotlin.math.hypot

/**
 * 钢笔
 */
open class PenSteel: PenBezier {

    constructor(paintId: Int): super(paintId) {
        mPaint = Paint(Paint.DITHER_FLAG) // 新创建笔头，不用默认画笔
        mPaint.isAntiAlias = true //锯齿不显示
        mPaint.isDither = true
        mPaint.strokeMiter = 1.0f
        style = Paint.Style.STROKE
        alphaEnabled = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        while (mHWPointList.isNotEmpty()) {
            val point: ControllerPoint = mHWPointList.pop()
            doNeetToDo(canvas, point, mPaint)
            mCurPoint = point
        }
    }

    private fun doNeetToDo(canvas: Canvas?, point: ControllerPoint, paint: Paint?) {
        drawLine(
            canvas!!, mCurPoint.x.toDouble(), mCurPoint.y.toDouble(),
            mCurPoint.width.toDouble(), point.x.toDouble(),
            point.y.toDouble(), point.width.toDouble(), paint!!
        )
    }

    /**
     * 沿着path画椭圆
     */
    private fun drawLine(
        canvas: Canvas,
        x0: Double,
        y0: Double,
        w0: Double,
        x1: Double,
        y1: Double,
        w1: Double,
        paint: Paint
    ) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个园
        val curDis = hypot(x0 - x1, y0 - y1)
        val steps = if (paint.strokeWidth < 6) {
            1 + (curDis / 2).toInt()
        } else if (paint.strokeWidth > 60) {
            1 + (curDis / 4).toInt()
        } else {
            1 + (curDis / 3).toInt()
        }
        val deltaX = (x1 - x0) / steps
        val deltaY = (y1 - y0) / steps
        val deltaW = (w1 - w0) / steps
        var x = x0
        var y = y0
        var w = w0
        for (i in 0 until steps) {
            val oval = RectF()
            oval.left = (x - w / 4.0f).toFloat()
            oval.top = (y - w / 2.0f).toFloat()
            oval.right = (x + w / 4.0f).toFloat()
            oval.bottom = (y + w / 2.0f).toFloat()
            //最基本的实现，通过点控制线，绘制椭圆
            canvas.drawOval(oval, paint)
            x += deltaX
            y += deltaY
            w += deltaW
        }
    }
}