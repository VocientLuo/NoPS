package com.xluo.core.entity

import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.xluo.core.constants.Constants
import com.blankj.utilcode.util.ConvertUtils

/**
 * Create by xl
 * 文字图层
 */
class PvsTextLayer : PvsLayer() {

    var text: String = ""

    var fontName: String = "None"
    var textSize: Float = ConvertUtils.sp2px(Constants.TEXT_DEFAULT_SIZE).toFloat()
    var color: Int = Color.WHITE
    var align: Paint.Align = Paint.Align.LEFT

    /**
     * 水平方向文字的间距
     */
    var horizontalExtra = Constants.TEXT_LETTER_SPACE

    /**
     * 行间距
     */
    var verticalExtra = Constants.TEXT_LINE_SPACE

    /**
     * 文字的弧度
     */
    var arcRadius = 0f

    var staticLayout: StaticLayout? = null

    /**
     * 当前行的最大宽度
     */
    var lineMaxWidth = 0

    var maxHeight = 0

    var typeFace: Typeface? = null


    override fun type(): Int {
        return Constants.LAYER_TYPE_TEXT
    }

    override fun copy(): PvsLayer {
        val pvsTextLayer = PvsTextLayer()
        pvsTextLayer.objectId = objectId
        pvsTextLayer.pos = RectF(pos)
        pvsTextLayer.layerLevel = layerLevel
        pvsTextLayer.isShow = isShow
        pvsTextLayer.rotation = rotation
        pvsTextLayer.scaleX = scaleX
        pvsTextLayer.scaleY = scaleY
        pvsTextLayer.transX = transX
        pvsTextLayer.transY = transY
        pvsTextLayer.bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        pvsTextLayer.isLocked = isLocked
        pvsTextLayer.matrix = Matrix(matrix)
        pvsTextLayer.text = text
        pvsTextLayer.fontName = fontName
        pvsTextLayer.textSize = textSize
        pvsTextLayer.align = align
        pvsTextLayer.horizontalExtra = horizontalExtra
        pvsTextLayer.verticalExtra = verticalExtra
        pvsTextLayer.arcRadius = arcRadius
        pvsTextLayer.color = color
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = pvsTextLayer.color
            textSize = pvsTextLayer.textSize
            textAlign = pvsTextLayer.align
            alpha = pvsTextLayer.alpha
            letterSpacing = pvsTextLayer.horizontalExtra
            typeface = pvsTextLayer.typeFace
            fontMetrics.top = 0f
            fontMetrics.bottom = 0f
        }
        staticLayout?.let {
            val staticLayout =
                StaticLayout(
                    text,
                    paint,
                    it.width,
                    Layout.Alignment.ALIGN_NORMAL,
                    verticalExtra,
                    0f,
                    false
                )
            pvsTextLayer.staticLayout = staticLayout
        }
        pvsTextLayer.lineMaxWidth = lineMaxWidth
        pvsTextLayer.maxHeight = maxHeight
        pvsTextLayer.alpha = alpha
        pvsTextLayer.typeFace = typeFace
        pvsTextLayer.originHeight = originHeight
        pvsTextLayer.originWidth = originWidth
        return pvsTextLayer
    }

}