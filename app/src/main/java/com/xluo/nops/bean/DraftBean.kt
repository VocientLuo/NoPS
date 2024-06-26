package com.xluo.nops.bean

/**
 * 草稿箱数据
 */
data class DraftBean(val bgType: Int, val width: Int, val height: Int, var bgImage: String?, var bgColor: Int?, var isShow: Boolean = true, var layerList: ArrayList<Layer>)

const val DRAW_DRAFT_TYPE: String = "1001"

data class Layer(
    var layerName: String,
    var fileName: String,
    var width: Int,
    var height: Int,
    var isShow: Boolean,
    var alpha: Int,
)