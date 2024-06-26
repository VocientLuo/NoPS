package com.xluo.pen

import android.graphics.*
import com.xluo.core.constants.Constants
import com.xluo.pen.shape.PenBezier

/**
 * 橡皮擦, 可调节透明度
 */
class PenErase: PenBezier {

    // 透明度范围，这个值比较合适。
    private val alphaRange = 40

    constructor(paintId: Int) : super(paintId) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG) //创建抗锯齿画笔
        mPaint.isAntiAlias = true //锯齿不显示
        mPaint.strokeJoin = Paint.Join.ROUND //接洽处为圆弧
        mPaint.strokeCap = Paint.Cap.ROUND //画笔样式圆形
        style = Paint.Style.FILL
        size = Constants.DEFAULT_ERASE_SIZE
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }

    override fun freshPen() {
        mPaint.color = Color.TRANSPARENT
        val targetAlpha = alpha*alphaRange/255
        mPaint.alpha = 255-targetAlpha
    }

    override fun onDraw(canvas: Canvas) {
        while (mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            canvas.drawCircle(point.x, point.y, size.toFloat(), mPaint)
        }
    }
}
