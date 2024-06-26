package com.xluo.core.core

import android.graphics.*
import androidx.core.graphics.toRectF
import com.xluo.core.constants.Constants
import com.xluo.core.entity.*
import com.xluo.core.interfaces.PvsCanvasSizeChangeCallback
import com.xluo.core.utils.RectUtils


/**
 * Create by xl
 * 当前编辑图层的时间线
 */
class PvsTimeLine {

    /**
     * 存放当前添加的所有图层
     */
    var layerList = arrayListOf<PvsLayer>()

    var backupLayerList = arrayListOf<PvsLayer>()

    var viewWidth = 0

    var viewHeight = 0

    /**
     * 背景图层数据
     */
    var bgLayer: PvsBackgroundLayer? = null

    var backupBgLayer: PvsBackgroundLayer? = null

    /**
     * 预加载图层相关，图片图层、文本图层
     */
    var preAddLayer: PvsLayer? = null

    var canvasWidth = 0

    var canvasHeight = 0

    var paint = Paint(Paint.DITHER_FLAG)

    var canvasSizeCallbackList = arrayListOf<PvsCanvasSizeChangeCallback>()

    var xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)


    var showRect: Rect = Rect()

    private var imageMatrix = Matrix()

    fun drawLayer(canvas: Canvas) {
        //对图层进行绘制
        bgLayer?.let {
            if (it.isShow) {
                showRect = RectUtils.getShowRect(viewWidth, viewHeight, it.bgLayerWidth, it.bgLayerHeight)
                when (it.bgType) {
                    Constants.BG_TYPE_COLOR -> {
                        paint.color = it.bgColor
                        canvas.drawRect(showRect, paint)
                    }
                    Constants.BG_TYPE_IMAGE -> {
                        it.decodeInfo?.bitmap?.let { bgBitmap ->
                            canvas.drawBitmap(bgBitmap, null, showRect, paint)
                        }
                    }
                }
            } else {
                showRect = RectUtils.getShowRect(viewWidth, viewHeight, it.bgLayerWidth, it.bgLayerHeight)
                it.bitmap?.let { bgBitmap ->
                    canvas.drawBitmap(bgBitmap, null, showRect, paint)
                }
            }
            layerList.forEach { layer ->
                if (layer.isShow) {
                    when (layer.type()) {
                        // PvsTimeLine只绘制大图层，文本和图片绘制在大图层里bitmap上
                        Constants.LAYER_TYPE_RICH -> {
                            (layer as PvsRichLayer).drawLayer()
                            paint.alpha = layer.alpha
                            val id = canvas.saveLayer(showRect.toRectF(), paint)
                            layer.bitmap?.let { bitmap ->
                                if (!bitmap.isRecycled) {
                                    canvas.drawBitmap(bitmap, layer.matrix, paint)
                                }
                            }
                            canvas.restoreToCount(id)
                        }
                    }
                }
            }
        }

        preAddLayer?.let {
            if (it.isShow) {
                when (it.type()) {
                    Constants.LAYER_TYPE_IMAGE -> {
                        val id = canvas.saveLayer(showRect.toRectF(), paint)
                        it.bitmap?.let { bitmap ->
                            if (!bitmap.isRecycled) {
                                canvas.drawBitmap(bitmap, it.matrix, paint)
                            }
                        }
                        paint.xfermode = xfermode
                        canvas.drawRect(showRect, paint)
                        paint.xfermode = null
                        canvas.restoreToCount(id)
                    }
                    Constants.LAYER_TYPE_TEXT -> {
                        canvas.save()
                        val textLayer = it as PvsTextLayer
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
    }

    val matrixArray = FloatArray(9)

    fun addLayer(layer: PvsLayer) {
        layerList.add(layer)
    }

    fun addLayer(layer: List<PvsLayer>) {
        layerList.clear()
        layerList.addAll(layer)
    }

    fun changeCanvasSize(canvasWidth: Int, canvasHeight: Int) {
        this.canvasHeight = canvasHeight
        this.canvasWidth = canvasWidth
        canvasSizeCallbackList.forEach {
            it.onCanvasSizeChange(canvasWidth, canvasHeight)
        }
    }

    fun addCanvasSizeChangeListener(pvsCanvasSizeChangeCallback: PvsCanvasSizeChangeCallback) {
        canvasSizeCallbackList.add(pvsCanvasSizeChangeCallback)
    }

    /**
     * 移除背景后替换为默认的透明图层占位图
     */
    fun removeBackground(pvsBackgroundLayer: PvsBackgroundLayer) {
        bgLayer = pvsBackgroundLayer
    }

    fun replaceBackground(pvsBackgroundLayer: PvsBackgroundLayer) {
        bgLayer = pvsBackgroundLayer
    }

    fun saveToPhoto(viewWidth: Int, viewHeight: Int, showBg: Boolean): Bitmap? {
        val bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val defaultExportScale = 1f
        bgLayer?.let {
            showRect =
                RectUtils.getShowRect(
                    (viewWidth * defaultExportScale).toInt(),
                    (viewHeight * defaultExportScale).toInt(),
                    it.bgLayerWidth,
                    it.bgLayerHeight
                )
            drawScaleLayer(canvas, 1f, showRect, showBg)
            return Bitmap.createBitmap(
                bitmap,
                showRect.left,
                showRect.top,
                showRect.width(),
                showRect.height()
            )
        }
        return null
    }


    /**
     * 绘制缩放后的画布，将图形缩放后进行绘制 导出
     * @param canvas Canvas
     * @param scale Float
     * @param showBg Boolean  强制显示背景
     * 返回裁减掉边缘后的bitmap
     */
    private fun drawScaleLayer(canvas: Canvas, scale: Float, showRect: Rect, showBg: Boolean) {
        //对图层进行绘制
        bgLayer?.let {
            if (showBg || it.isShow) {
                when (it.bgType) {
                    Constants.BG_TYPE_COLOR -> {
                        paint.color = it.bgColor
                        canvas.drawRect(showRect, paint)
                    }
                    Constants.BG_TYPE_IMAGE -> {
                        it.decodeInfo?.bitmap?.let { bgBitmap ->
                            canvas.drawBitmap(bgBitmap, null, showRect, paint)
                        }
                    }
                }
            }
            layerList.forEach { layer ->
                if (layer.isShow) {
                    when (layer.type()) {
                        Constants.LAYER_TYPE_RICH -> {
                            (layer as PvsRichLayer).drawLayer()
                            paint.alpha = layer.alpha
                            val id = canvas.saveLayer(showRect.toRectF(), paint)
                            val matrix = Matrix(layer.matrix)
                            matrix.postScale(scale, scale)
                            layer.bitmap?.let { bitmap ->
                                if (!bitmap.isRecycled) {
                                    canvas.drawBitmap(bitmap, matrix, paint)
                                }
                            }
                            canvas.restoreToCount(id)
                        }
                        Constants.LAYER_TYPE_IMAGE -> {
                            val id = canvas.saveLayer(showRect.toRectF(), paint)
                            val matrix = Matrix(layer.matrix)
                            matrix.postScale(scale, scale)
                            layer.bitmap?.let { bitmap ->
                                canvas.drawBitmap(bitmap, matrix, paint)
                            }
                            paint.xfermode = xfermode
                            canvas.drawRect(showRect, paint)
                            paint.xfermode = null
                            canvas.restoreToCount(id)
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
                            imageMatrix.postScale(scale, scale)
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
        }
    }
}