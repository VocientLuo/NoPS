package com.xluo.core.entity

import android.graphics.*
import android.view.MotionEvent
import com.xluo.pen.high.HiPenPixel
import com.xluo.core.constants.Constants
import com.xluo.pen.core.BasePen
import com.blankj.utilcode.util.LogUtils
import com.xluo.pen.shape.PenShape
import kotlin.math.min

/**
 * Created by xluo on 2023/4/9.
 * 真实图层，可包含子图层：文字、图片等
 */
class PvsRichLayer : PvsLayer {
    companion object {
        var onShapeGenerated: ((Bitmap, RectF) -> Unit)? = null
    }

    var subLayer = arrayListOf<PvsLayer>()

    var canvas: Canvas

    var mWidth: Int
    var mHeight: Int

    var mCurrentPen: BasePen? = null

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private val matrixArray = FloatArray(9)
    private var imageMatrix = Matrix()

    constructor(width: Int, height: Int) : super() {
        mWidth = width
        mHeight = height
        val rect = RectF()
        rect.left = 0f
        rect.top = 0f
        rect.right = mWidth.toFloat()
        rect.bottom = mHeight.toFloat()
        originWidth = mWidth
        originWidth = mHeight
        pos = rect
        bitmap = Bitmap.createBitmap(mWidth, mHeight,Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
    }

    constructor(width: Int, height: Int, bmp: Bitmap) : super() {
        mWidth = width
        mHeight = height
        val rect = RectF()
        rect.left = 0f
        rect.top = 0f
        rect.right = mWidth.toFloat()
        rect.bottom = mHeight.toFloat()
        originWidth = mWidth
        originWidth = mHeight
        pos = rect
        bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(bitmap!!)
    }

    override fun type(): Int {
        return Constants.LAYER_TYPE_RICH
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (mCurrentPen == null) {
            LogUtils.e("can't draw, pen is not exists")
            return false
        }
        if (mCurrentPen is HiPenPixel) {
            (mCurrentPen as HiPenPixel).canvasWidth = mWidth
            (mCurrentPen as HiPenPixel).canvasHeight = mHeight
        }
        mCurrentPen?.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (mCurrentPen !is PenShape) {
                    mCurrentPen?.onDraw(canvas)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mCurrentPen !is PenShape) {
                    mCurrentPen?.onDraw(canvas)
                }
            }
            MotionEvent.ACTION_UP -> {
                // 形状画笔直接返回，进入编辑形状流程
                if (mCurrentPen is PenShape) {
                    val pen = mCurrentPen as PenShape
                    val rect = pen.rectF
                    if (rect.width() > pen.size && rect.height() > pen.size) {
                        onShapeGenerated?.invoke(drawShape(pen), rect)
                        mCurrentPen?.resetPath()
                        return false
                    }
                }
                // 只有手指松开时才绘制到bitmap的canvas上，移动时绘制在PvsEditView的canvas上
                mCurrentPen?.onDraw(canvas)
                mCurrentPen?.resetPath()
            }
        }
        return true
    }

    private fun drawShape(pen: PenShape): Bitmap {
        val bmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        pen.onDraw(canvas)
        // 裁减一下矩形，避免bitmap溢出
        pen.rectF.right = min(mWidth.toFloat(), pen.rectF.right)
        pen.rectF.bottom = min(mHeight.toFloat(), pen.rectF.bottom)
        return Bitmap.createBitmap(bmp, pen.rectF.left.toInt(), pen.rectF.top.toInt(),
            pen.rectF.width().toInt(), pen.rectF.height().toInt())
    }

    fun mergeLayer(richLayer: PvsRichLayer) {
        richLayer.bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)
        }
    }

    fun drawLayer() {
        //对子图层进行绘制
        subLayer.forEach { layer ->
            if (layer.isShow) {
                when (layer.type()) {
                    // PvsTimeLine只绘制大图层，文本/图片/画笔绘制在大图层的bitmap上
                    Constants.LAYER_TYPE_IMAGE -> {
                        layer.bitmap?.let { bitmap ->
                            if (!bitmap.isRecycled) {
                                canvas.drawBitmap(bitmap, layer.matrix, paint)
                            }
                        }
                    }
                    Constants.LAYER_TYPE_TEXT -> {
                        canvas.save()
                        val textLayer = layer as PvsTextLayer
                        textLayer.matrix.getValues(matrixArray)
                        imageMatrix.reset()
                        imageMatrix.setValues(matrixArray)
                        if (textLayer.align == Paint.Align.CENTER) {
                            imageMatrix.preTranslate(
                                (textLayer.lineMaxWidth / 2).toFloat(),
                                0f
                            )
                        } else if (textLayer.align == Paint.Align.RIGHT) {
                            imageMatrix.preTranslate(
                                (textLayer.lineMaxWidth).toFloat(),
                                0f
                            )
                        }
                        canvas.setMatrix(imageMatrix)
                        textLayer.staticLayout?.apply {
                            paint.color = textLayer.color
                            paint.alpha = textLayer.alpha
                            paint.typeface = textLayer.typeFace
                            paint.textAlign = textLayer.align
                            draw(canvas)
                        }
                        canvas.setMatrix(null)
                        canvas.restore()
                    }
                }
            }
        }
        subLayer.clear()
    }

    fun getOriginBmpAndClear(): Bitmap {
        val bitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        return bitmap
    }

    fun drawBitmap(bmp: Bitmap, left: Float, top: Float) {
        canvas.drawBitmap(bmp, left, top, Paint())
    }

    fun reset() {
        subLayer.clear()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    /**
     * 剪切选区
     */
    fun cutPath(path: Path, isNormal: Boolean) {
        bitmap?.let {
            bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)
            val paint = Paint()
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)
            paint.xfermode = if (isNormal) {
                // 取原bitmap的非交集部分
                PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
            } else {
                // 取原bitmap的交集部分
                PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            }
            canvas.drawBitmap(it, 0f, 0f, paint)
        }
    }

    override fun copy(): PvsLayer {
        val pvsRichLayer = PvsRichLayer(mWidth, mHeight)
        pvsRichLayer.pos = RectF(pos)
        pvsRichLayer.objectId = objectId
        pvsRichLayer.layerLevel = layerLevel
        pvsRichLayer.isShow = isShow
        pvsRichLayer.name = name
        pvsRichLayer.isSelected = isSelected
        pvsRichLayer.rotation = rotation
        pvsRichLayer.scaleX = scaleX
        pvsRichLayer.scaleY = scaleY
        pvsRichLayer.transX = transX
        pvsRichLayer.transY = transY
        pvsRichLayer.bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        pvsRichLayer.canvas = Canvas(pvsRichLayer.bitmap!!)
        pvsRichLayer.isLocked = isLocked
        pvsRichLayer.matrix = Matrix(matrix)
        pvsRichLayer.originHeight = originHeight
        pvsRichLayer.originWidth = originWidth
        pvsRichLayer.uri = uri
        pvsRichLayer.mirrorPointF.set(mirrorPointF)
        pvsRichLayer.subLayer.addAll(subLayer)
        return pvsRichLayer
    }
}