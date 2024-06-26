package com.xluo.pen

import android.graphics.Paint

/**
 * 毛笔
 */
class PenMaobi: PenSteel {

    constructor(paintId: Int): super(paintId) {
        // 绘制边框和内容
        style = Paint.Style.FILL
        midPaddingAlphaEnabled = false
        rotationEnabled = false
        spaceEnabled = false
        flowValueEnabled = false
        styleEnabled = false
        penShapeEnabled = false
        penShapePicEnabled = false
    }
}