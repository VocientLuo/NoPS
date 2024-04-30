package com.xluo.pen

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.xluo.core.utils.dp
import com.xluo.pen.core.BasePen

/**
 * 边框画笔
 */
open class PenBorder : BasePen {

    var innerPaint: Paint = Paint()

    constructor(paintId: Int) : super(paintId)

    override fun touchDown(startX: Float, startY: Float) {
        super.touchDown(startX, startY)
        mPath.reset()
        //设置曲线开始点
        mPath.moveTo(startX, startY)
        mStartX = startX
        mStartY = startY
        mPaint.style = Paint.Style.STROKE
        innerPaint = Paint(mPaint)
        innerPaint.color = Color.parseColor("#ffffff")
        innerPaint.strokeWidth = size - 3.dp
    }

    override fun touchMove(currentX: Float, currentY: Float) {
        mPath.quadTo(mStartX, mStartY, (currentX + mStartX) / 2, (currentY + mStartY) / 2)
        mStartX = currentX
        mStartY = currentY
    }

    override fun touchUp(endX: Float, endY: Float) {
        super.touchUp(endX, endY)
        mPath.quadTo(mStartX, mStartY, endX, endY)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
        canvas.drawPath(mPath, innerPaint)
    }
}