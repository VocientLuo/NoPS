package com.xluo.core.entity

import android.graphics.*
import com.xluo.core.constants.Constants

/**
 * Create by AjjAndroid
 * Time:2022/2/16
 */
class PvsBackgroundLayer : PvsLayer() {


    var bgType: Int = -1

    /**
     * 如果
     */
    var bgColor = Color.WHITE

    /**
     * 画布缩放
     */
    var scale: Float = 1.0f

    /**
     * 如果类型是图片，则此值不为空
     */
    var decodeInfo: PvsImageDecodeInfo? = null

    var bgLayerWidth = 0

    var bgLayerHeight = 0

    override fun type(): Int {
        return Constants.LAYER_TYPE_BG
    }

    override fun copy(): PvsBackgroundLayer {
        val pvsBackgroundLayer = PvsBackgroundLayer()
        pvsBackgroundLayer.pos = RectF(pos)
        pvsBackgroundLayer.layerLevel = layerLevel
        pvsBackgroundLayer.isShow = isShow
        pvsBackgroundLayer.rotation = rotation
        pvsBackgroundLayer.scaleX = scaleX
        pvsBackgroundLayer.scaleY = scaleY
        pvsBackgroundLayer.transX = transX
        pvsBackgroundLayer.transY = transY
        pvsBackgroundLayer.scale = scale
        pvsBackgroundLayer.rotation = rotation
        pvsBackgroundLayer.bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        pvsBackgroundLayer.isLocked = isLocked
        pvsBackgroundLayer.matrix = Matrix(matrix)
        pvsBackgroundLayer.decodeInfo = decodeInfo?.copy()
        pvsBackgroundLayer.bgLayerWidth = bgLayerWidth
        pvsBackgroundLayer.bgLayerHeight = bgLayerHeight
        pvsBackgroundLayer.bgColor = bgColor
        pvsBackgroundLayer.bgType = bgType
        pvsBackgroundLayer.originHeight = originHeight
        pvsBackgroundLayer.originWidth = originWidth
        return pvsBackgroundLayer
    }
}