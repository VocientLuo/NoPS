package com.xluo.pen.pic

/**
 * 多色彩多图片画笔，图片颜色不能修改
 */
open class PenMultiPic(paintId: Int) : PicPen(paintId) {
    init {
        // 颜色不可变
        colorEnabled = false
        flowValueEnabled = false
        flowValueEnabled = false
        styleEnabled = false
        penShapeEnabled = false
        penShapePicEnabled = false
    }
}