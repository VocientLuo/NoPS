package com.xluo.core.entity

import android.graphics.*
import com.xluo.core.constants.Constants

/**
 * Create by xl
 * 形状图层
 */
class PvsShapeLayer : PvsLayer() {

    companion object {
        const val SHAPE_CIRCLE = 0  //圆形
        const val SHAPE_RECTANGLE = 1  //矩形
        const val SHAPE_PENTAGON = 2  //五边形
        const val SHAPE_HEXAGON = 3   //六边形
        const val SHAPE_TRIANGLE = 4  //三角形
        const val SHAPE_ROUND_RECTANGLE = 5  //圆角矩形
        const val SHAPE_STAR = 6  //五角星
        const val SHAPE_CIRCLE_RING = 7 //圆环
        const val SHAPE_RECTANGLE_RING = 8  //矩形环
    }

    var shapeType: Int = -1

    //形状的path
    var path = Path()

    var color = Color.WHITE


    override fun type(): Int {
        return Constants.LAYER_TYPE_SHAPE
    }

    override fun copy(): PvsLayer {
        val pvsShapeLayer = PvsShapeLayer()
       pvsShapeLayer.pos = RectF(pos)
       pvsShapeLayer.objectId = objectId
       pvsShapeLayer.layerLevel = layerLevel
       pvsShapeLayer.isShow = isShow
       pvsShapeLayer.rotation = rotation
       pvsShapeLayer.scaleX = scaleX
       pvsShapeLayer.scaleY = scaleY
       pvsShapeLayer.transX = transX
       pvsShapeLayer.transY = transY
       pvsShapeLayer.bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
       pvsShapeLayer.isLocked = isLocked
       pvsShapeLayer.matrix = Matrix(matrix)
       pvsShapeLayer.shapeType = shapeType
       pvsShapeLayer.path = Path()
       pvsShapeLayer.alpha = alpha
       pvsShapeLayer.color = color
        return pvsShapeLayer
    }
}