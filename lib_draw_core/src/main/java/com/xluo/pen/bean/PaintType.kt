package com.xluo.pen.bean


enum class PenType {
    NONE,
    TRADITION,
    PICTURE,
    CUSTOM,
    SHAPE,
    HI_PEN,
    HISTORY,
}

data class PaintType(
    val name: String,
    val penType: PenType? = null,
    val penTypeId: Int = 0
)