package com.xluo.pen.pic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.text.TextUtils
import com.xluo.core.constants.Constants
import com.xluo.pen.PenManager
import com.xluo.pen.PenManager.paintPicList
import com.xluo.pen.core.PenPureBezier

import java.io.File
import java.io.FileInputStream

/**
 * 图案画笔
 */
open class PicPen : PenPureBezier {

    var paintImagePath: String? = null

    private var drawIndex = 0

    val bitmapList = arrayListOf<Bitmap>()

    constructor(paintId: Int) : super(paintId) {
        minSpace = 50
        penSquare = true
        isRotateByPenOriental = true
        penShapeEnabled = false
        penShapePicEnabled = false
    }

    override fun reset() {
        super.reset()
        minSpace = 50
        penSquare = true
        isRotateByPenOriental = true
    }

    fun setResIds(resIds: List<Int>) {
        resIds.forEach {
            val bitmap = BitmapFactory.decodeResource(PenManager.context.resources, it)
            bitmapList.add(bitmap)
        }
    }

    override fun freshPen() {
        super.freshPen()
        // 自定义导入图片画笔
        if (id == Constants.PEN_ID_CUSTOM && bitmapList.isEmpty()) {
            if (!TextUtils.isEmpty(paintImagePath) && File(paintImagePath).exists()) {
                val fis = FileInputStream(paintImagePath)
                val bitmap = BitmapFactory.decodeStream(fis)
                bitmapList.add(bitmap)
            }
        }
        // 可着色图片画笔
        if (id in Constants.PEN_ID_SEA_STAR..Constants.PEN_ID_GRASS) {
            bitmapList.clear()
            val resId = paintPicList[id - Constants.PEN_ID_SEA_STAR]
            // 给资源图片上色
            val bitmap = BitmapFactory.decodeResource(PenManager.context.resources, resId)
            bitmapList.add(getPaintBitmap(bitmap))
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (bitmapList.isEmpty()) {
            return
        }
        while(mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            var bitmap = bitmapList[drawIndex%bitmapList.size]
            var rotate = if (isRotateByPenOriental && lastPoint != null) {
                calRotate(lastPoint!!.x.toDouble(), lastPoint!!.y.toDouble(), point.x.toDouble(), point.y.toDouble())
            } else {
                0f
            }
            if (isRandomDegree) {
                rotate = randomDegree()
            }
            bitmap = getRotateBitmap(bitmap, rotate.toFloat())
            canvas.drawBitmap(bitmap, point.x-size/2, point.y-size/2, mPaint)
            lastPoint = point
            drawIndex++
        }
    }
}