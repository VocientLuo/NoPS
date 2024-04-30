package com.xluo.pen.bean

import com.xluo.core.constants.Constants

val penTypeList = arrayListOf(
    PaintType("历史画笔", PenType.HISTORY),
    PaintType("常规画笔", PenType.NORMAL),
    PaintType("圆头画笔", PenType.CIRCLE),
    PaintType("勾线画笔", PenType.LINE),
    PaintType("材质元素", PenType.MATERIAL),
)

fun getLeafPen(): PaintBean {
    return PaintBean(Constants.LEAF, PenType.NONE, "柳叶笔", "")
}

val penPencil = PaintBean(Constants.PEN_ID_HI_PENCIL, PenType.NORMAL, "传统铅笔")

val penNormalList = arrayListOf(
    PaintBean(Constants.PEN_ID_HI_PENCIL, PenType.NORMAL, "传统铅笔"),
    PaintBean(Constants.PEN_ID_HI_CARBON, PenType.NORMAL, "碳素笔"),
    PaintBean(Constants.PEN_ID_MULTI_PIC_16, PenType.NORMAL, "边框笔", ),
    PaintBean(Constants.PEN_ID_HI_PIXEL, PenType.NORMAL, "高阶像素笔", ),
    PaintBean(Constants.PEN_ID_HI_SOFT, PenType.NORMAL, "高阶柔边笔", ),
    PaintBean(Constants.PEN_ID_HI_CARBON_PENCILE, PenType.NORMAL, "高阶碳铅笔", ),
)
val penCircleList = arrayListOf(
    PaintBean(Constants.PEN_ID_HI_HAND, PenType.LINE, "手写笔", ),
    PaintBean(Constants.PEN_ID_HI_MARK, PenType.LINE, "马克笔", ),
    PaintBean(Constants.PEN_ID_HI_CRAYON_DARK, PenType.LINE, "木炭笔", ),
)

val penLineList = arrayListOf(
    PaintBean(Constants.PEN_ID_HI_CIRCLE, PenType.CIRCLE, "硬圆笔",),
    PaintBean(Constants.PEN_ID_HI_CIRCLE_SOFT, PenType.CIRCLE, "软圆笔", ),
)
val penMaterialList = arrayListOf(
    PaintBean(Constants.PEN_ID_HI_CLOTH, PenType.MATERIAL, "织物笔", ),
    PaintBean(Constants.PEN_ID_HI_CRAYON, PenType.MATERIAL, "磨砂笔", ),
    PaintBean(Constants.PEN_ID_HI_OIL, PenType.MATERIAL, "丝纱笔", ),
)




