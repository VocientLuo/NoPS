package com.xluo.pen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.xluo.pen.bean.ControllerPoint
import com.xluo.pen.shape.PenBezier
import kotlin.math.hypot

/**
 * 贝塞尔路径图片画笔
 */
open class PenPicBezier : PenBezier {

    private var mContext: Context

    private var drawIndex = 0

    private val bitmapList = arrayListOf<Bitmap>()

    constructor(context: Context, paintId: Int, resIds: List<Int>) : super(paintId) {
        mContext = context
        bitmapList.clear()
        resIds.forEach {
            val bitmap = BitmapFactory.decodeResource(mContext.resources, it)
            bitmapList.add(bitmap)
        }
    }

    override fun freshPen() {
        super.freshPen()
        mPaint.style = Paint.Style.FILL
        alpha = Color.alpha(color)
        mPaint.alpha = alpha
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        while (mHWPointList.isNotEmpty()) {
            val point: ControllerPoint = mHWPointList.pop()
            doNeetToDo(canvas, point, mPaint)
            mCurPoint = point
        }
    }

    private fun doNeetToDo(canvas: Canvas, point: ControllerPoint, paint: Paint) {
        drawLine(
            canvas!!,
            mCurPoint.x.toDouble(), mCurPoint.y.toDouble(),
            point.x.toDouble(), point.y.toDouble(),
        )
    }

    private fun drawLine(
        canvas: Canvas,
        x0: Double,
        y0: Double,
        x1: Double,
        y1: Double
    ) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个园
        val curDis = hypot(x0 - x1, y0 - y1)
        var steps = 1 + (curDis / (size/3)).toInt()
        val deltaX = (x1 - x0) / steps
        val deltaY = (y1 - y0) / steps
        var x = x0
        var y = y0
        for (i in 0 until steps) {
            drawBitmap(canvas, x.toFloat(), y.toFloat())
            x += deltaX
            y += deltaY
        }
    }

    private fun drawBitmap(canvas: Canvas, x: Float, y: Float) {
        var bitmap = bitmapList[drawIndex%bitmapList.size].copy(Bitmap.Config.ARGB_8888, true)
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
        canvas.drawBitmap(bitmap, x, y, mPaint)
        drawIndex++
    }
}