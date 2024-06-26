package com.xluo.pen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import com.xluo.pen.core.BasePen
import com.blankj.utilcode.util.LogUtils
import kotlin.math.max
import kotlin.math.min

/**
 * 选区复制笔
 */
class PenCut(paintId: Int) : BasePen(paintId) {

    var rectF = RectF()

    var firstPointX = 0f
    var firstPointY = 0f

    private val penSize = 5f

    // 是否有裁剪
    var hasCutting = false

    var cutPath = Path()

    override fun freshPen() {
        super.freshPen()
        // 虚线笔，并固定大小
        mPaint.pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 20f)
        mPaint.strokeWidth = penSize
        mPaint.color = Color.parseColor("#999999")
    }

    override fun touchDown(startX: Float, startY: Float) {
        super.touchDown(startX, startY)
        mPath.reset()
        mPath.moveTo(startX, startY)
        firstPointX = startX
        firstPointY = startY
        rectF = RectF(startX, startY, startX, startY)
        hasCutting = false
    }

    override fun touchMove(currentX: Float, currentY: Float) {
        super.touchMove(currentX, currentY)
        mPath.lineTo(currentX, currentY)
        mStartX = currentX
        mStartY = currentY
        freshRectF(currentX, currentY)
    }

    override fun touchUp(endX: Float, endY: Float) {
        mPath.lineTo(endX, endY)
        // 完成闭环
        mPath.lineTo(firstPointX, firstPointY)
        freshRectF(endX, endY)
        finalRectF()
        // 区域太小不要裁剪
        if (rectF.width()*rectF.height() > 50) {
            hasCutting = true
        } else {
            LogUtils.e("area too small...")
        }

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
    }

    private fun freshRectF(x: Float, y: Float) {
        if (!rectF.contains(x, y)) {
            rectF.left = min(rectF.left, x)
            rectF.top = min(rectF.top, y)
            rectF.right = max(rectF.right, x)
            rectF.bottom = max(rectF.bottom, y)
        }
    }

    private fun finalRectF() {
        rectF.left -= penSize/2
        rectF.right += penSize/2
        rectF.top -= penSize/2
        rectF.bottom += penSize/2
    }

    fun genAreaBitmap(src: Bitmap): Bitmap {
        // 每次裁剪只用用一次
        hasCutting = false
        // 根据裁剪区域生成bitmap
        val srcCopy = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(srcCopy)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        paint.style = Paint.Style.FILL
        canvas.drawPath(mPath, Paint())
        canvas.drawBitmap(src, 0f, 0f, paint)
        return Bitmap.createBitmap(srcCopy, rectF.left.toInt(), rectF.top.toInt(),
            rectF.width().toInt(), rectF.height().toInt())
    }
}