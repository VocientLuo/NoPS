package com.xluo.pen.bean


enum class PenType {
    NONE,
    SHAPE,
    NORMAL,
    CIRCLE,
    MATERIAL,
    LINE,
    HISTORY,
    CUSTOM,
}

data class PaintType(
    val name: String,
    val penType: PenType? = null,
    val penTypeId: Int = 0
)