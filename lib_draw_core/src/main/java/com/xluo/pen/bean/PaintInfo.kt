package com.xluo.pen.bean

data class PaintInfo(
    var id: Int,
    var name: String,
    var size: Int,
    var color: Int,
    var mode: Int,
    var alpha: Int,
    var imagePath: String,
    var picRes: List<Int> = arrayListOf()
)
