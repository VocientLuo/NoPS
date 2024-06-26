package com.xluo.nops.operation

import com.xluo.core.core.PvsTimeLine
import com.xluo.core.entity.PvsImageLayer
import com.xluo.core.entity.PvsShapeLayer
import com.xluo.core.entity.PvsTextLayer


object TimeLineData {

    /**
     * 当前编辑模板的时间线数据，在初始化时需要赋值
     */
    var pvsTimeLine: PvsTimeLine? = null


    fun findOldImageLayer(objectId: Long): PvsImageLayer? {
        if (pvsTimeLine != null) {
            pvsTimeLine!!.layerList.forEach {
                if (it.objectId == objectId) {
                    return it as PvsImageLayer
                }
            }
        }
        return null
    }

    fun findShapeLayer(objectId: Long): PvsShapeLayer? {
        if (pvsTimeLine != null) {
            pvsTimeLine!!.layerList.forEach {
                if (it.objectId == objectId) {
                    return it as PvsShapeLayer
                }
            }
        }
        return null
    }

    fun findTextLayer(objectId: Long): PvsTextLayer? {
        if (pvsTimeLine != null) {
            pvsTimeLine!!.layerList.forEach {
                if (it.objectId == objectId) {
                    return it as PvsTextLayer
                }
            }
        }
        return null
    }

    fun findOldLayerIndex(objectId: Long): Int {
        if (pvsTimeLine != null) {
            pvsTimeLine!!.layerList.forEachIndexed { index, pvsLayer ->
                if (pvsLayer.objectId == objectId) {
                    return index
                }
            }
        }
        return -1
    }

    fun removeLayerById(objectId: Long) {
        if (pvsTimeLine != null) {
            pvsTimeLine!!.layerList.removeAll {
                it.objectId == objectId
            }
        }
    }

}