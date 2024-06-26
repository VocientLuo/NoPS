package com.xluo.core.entity

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import com.xluo.core.constants.Constants

/**
 * Create by AjjAndroid
 * Time:2022/2/16
 */
class PvsImageLayer : PvsLayer() {

    override fun type(): Int {
        return Constants.LAYER_TYPE_IMAGE
    }

    override fun copy(): PvsLayer {
        val pvsImageLayer = PvsImageLayer()
        pvsImageLayer.pos = RectF(pos)
        pvsImageLayer.objectId = objectId
        pvsImageLayer.layerLevel = layerLevel
        pvsImageLayer.isShow = isShow
        pvsImageLayer.rotation = rotation
        pvsImageLayer.scaleX = scaleX
        pvsImageLayer.scaleY = scaleY
        pvsImageLayer.transX = transX
        pvsImageLayer.transY = transY
        pvsImageLayer.bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        pvsImageLayer.isLocked = isLocked
        pvsImageLayer.matrix = Matrix(matrix)
        pvsImageLayer.originHeight = originHeight
        pvsImageLayer.originWidth = originWidth
        pvsImageLayer.uri = uri
        pvsImageLayer.mirrorPointF.set(mirrorPointF)

        return pvsImageLayer
    }


}