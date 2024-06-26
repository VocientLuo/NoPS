package com.xluo.pen.high

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import com.xluo.pen.PenManager
import com.xluo.pen.core.PenPureBezier

/**
 * 高阶笔，对标画世界
 */
abstract class HiPenBase : PenPureBezier {

    var originBitmap: Bitmap? = null

    private var oldBitmap: Bitmap? = null

    var resId: Int = -1

    constructor(paintId: Int) : super(paintId)

    init {
        style = Paint.Style.FILL
        penPressEnabled = false
    }

    fun updateBitmap(bmp: Bitmap) {
        originBitmap = bmp
        freshPen()
    }

    fun updateBitmap(resId: Int) {
        originBitmap = BitmapFactory.decodeResource(PenManager.context.resources, resId)
        freshPen()
    }

    fun resetBitmap() {
        originBitmap = oldBitmap
    }

    override fun freshPen() {
        super.freshPen()
        if (originBitmap == null && resId != -1) {
            originBitmap = BitmapFactory.decodeResource(PenManager.context.resources, resId)
            oldBitmap = originBitmap
        }
        originBitmap?.let {
            bitmap = getRotateBitmap(getPaintBitmap(scaleTargetSize(it)))
        }
        bitmap?.let {
            val shader = BitmapShader(it, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            mPaint.shader = shader
        }
    }

    private fun scaleTargetSize(bitmap: Bitmap): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val matrix = Matrix()
        val paint = Paint()
        if (bitmap.width > bitmap.height) {
            val scale = size.toFloat() / bitmap.height
            matrix.postScale(scale, scale)
        } else {
            val scale = size.toFloat() / bitmap.width
            matrix.postScale(scale, scale)
        }
        canvas.drawBitmap(bitmap, matrix, paint)
        return bmp
    }

    override fun onDraw(canvas: Canvas) {
        while (mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            val realSize = getRealSize(point.p)
            // 方形笔头
            if (penSquare) {
                canvas.drawRect(point.x-realSize/2, point.y-realSize/2, point.x+realSize/2, point.y+realSize/2, mPaint)
            } else {
                canvas.drawCircle(point.x, point.y, realSize/2, mPaint)
            }
            lastPoint = point
        }
    }

    private fun getRotateBitmap(bitmap: Bitmap): Bitmap {
        val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        // 中空度
        if (midPadding > 0) {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            paint.alpha = midPaddingAlpha.toInt()
            paint.color = Color.TRANSPARENT
            paint.style = Paint.Style.FILL
            val space = midPadding/110f*size
            canvas.drawCircle(bitmap.width/2f, bitmap.height/2f, space, paint)
        }
        // 旋转
        val matrix = Matrix()
        if (penVertical) {
            matrix.postRotate(rotation)
        } else {
            matrix.postRotate(rotation+90)
        }
        return Bitmap.createBitmap(bmp, 0, 0, bitmap.width,
            bitmap.height, matrix, false)
    }

    /**
     * 根据当前点的压力值来缩放笔头
     */
    fun getRealSize(pressure: Float): Float {
        val scale = if (pressure > 1) {
            1f
        } else {
            pressure
        }
        return size * scale
    }

    fun getScaledBitmap(bitmap: Bitmap, pressure: Float): Bitmap {
        val scale = if (pressure > 1) {
            1f
        } else {
            pressure
        }
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }
}