package com.xluo.pen

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.xluo.pen.core.BasePen

/**
 * 柳叶笔
 */
class PenLeaf(paintId: Int) : BasePen(paintId) {

    enum class LeafType {
        FILL,
        CLEAN
    }

    var leafType = LeafType.FILL

    var penSize = 1f

    override fun freshPen() {
        super.freshPen()
        mPaint.strokeWidth = penSize
        mPaint.style = Paint.Style.FILL
        if (leafType == LeafType.FILL) {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        } else {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    override fun touchDown(startX: Float, startY: Float) {
        super.touchDown(startX, startY)
        mPath.reset()
        mPath.moveTo(startX, startY)
    }

    override fun touchMove(currentX: Float, currentY: Float) {
        super.touchMove(currentX, currentY)
        mPath.lineTo(currentX, currentY)
    }

    override fun touchUp(endX: Float, endY: Float) {
        super.touchUp(endX, endY)
        mPath.lineTo(endX, endY)
        mPath.close()
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawPath(mPath, mPaint)
    }
}