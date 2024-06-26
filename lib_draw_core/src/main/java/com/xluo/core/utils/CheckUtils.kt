package com.xluo.core.utils

import android.graphics.Point
import com.xluo.core.entity.PvsLayer

object CheckUtils {


    val mPoint = Point()

    /**
     * 检测是否暗中了某个图层 返回检测到的第一个
     * @param downX Float
     * @param downY Float
     */
    fun checkIsTouchLayer(downX: Float, downY: Float, layerList: List<PvsLayer>): PvsLayer? {
        val size = layerList.size - 1
        for (index in size downTo 0) {
            val layer = layerList[index]
            mPoint.set(downX.toInt(), downY.toInt())
            //旋转点击点
            RectUtils.rotatePoint(
                mPoint,
                layer.pos.centerX(),
                layer.pos.centerY(),
                -layer.rotation
            )
            if (layer.pos.contains(mPoint.x.toFloat(), mPoint.y.toFloat())) {
                if (!layer.isLocked) {
                    return layer
                }
            }
        }
        return null
    }


    fun checkIsTouchTargetLayer(downX: Float, downY: Float, pvsLayer: PvsLayer?): Boolean {
        if (pvsLayer == null) {
            return false
        }
        mPoint.set(downX.toInt(), downY.toInt())
        //旋转点击点
        RectUtils.rotatePoint(
            mPoint,
            pvsLayer.pos.centerX(),
            pvsLayer.pos.centerY(),
            -pvsLayer.rotation
        )
        return pvsLayer.pos.contains(mPoint.x.toFloat(), mPoint.y.toFloat())

    }

}