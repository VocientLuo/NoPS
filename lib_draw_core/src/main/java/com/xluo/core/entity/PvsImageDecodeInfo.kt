package com.xluo.core.entity

import android.graphics.Bitmap

/**
 * Create by xl
 * 图片解析器
 */
class PvsImageDecodeInfo : PvsObject() {

    var inSimpleSize = 1

    var originWidth = 0

    var originHeight = 0

    var bitmap: Bitmap? = null

    /**
     * 设置了缩放值之后bitmap 的宽
     */
    var scaleWidth = 0

    /**
     * 设置了缩放值之后bitmap 的高
     */
    var scaleHeight = 0

    var uri: String = ""


    fun copy(): PvsImageDecodeInfo {
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        pvsImageDecodeInfo.inSimpleSize = 1
        pvsImageDecodeInfo.originHeight = originHeight
        pvsImageDecodeInfo.originWidth = originWidth
        pvsImageDecodeInfo.bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        pvsImageDecodeInfo.scaleWidth = scaleWidth
        pvsImageDecodeInfo.scaleHeight = scaleHeight
        pvsImageDecodeInfo.uri = uri
        return pvsImageDecodeInfo
    }


}