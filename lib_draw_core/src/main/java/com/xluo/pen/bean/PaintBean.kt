package com.xluo.pen.bean

import android.graphics.Color

data class PaintBean(
    var id: Int,
    var penType: PenType,
    var name: String,
    var path: String = "",
    var select: Int = 0,
    var color: Int = Color.BLACK,
) {
    fun copy(): PaintBean {
        return PaintBean(id, penType, name, path, select, color)
    }
}