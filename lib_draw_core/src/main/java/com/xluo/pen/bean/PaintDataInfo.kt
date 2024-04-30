package com.xluo.pen.bean

import com.xluo.core.constants.Constants

val penTypeList = arrayListOf(
    PaintType("历史画笔", PenType.HISTORY),
    PaintType("高阶画笔", PenType.HI_PEN),
    PaintType("传统画笔", PenType.TRADITION),
    PaintType("图形画笔", PenType.SHAPE),
    PaintType("图案画笔", PenType.PICTURE),
)

fun getLeafPen(): PaintBean {
    return PaintBean(Constants.LEAF, PenType.NONE, "柳叶笔", "", 0, 0)
}

val penHiList = arrayListOf(
    PaintBean(Constants.PEN_ID_HI_PENCIL, PenType.HI_PEN, "高阶铅笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CARBON, PenType.HI_PEN, "高阶碳素笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CLOTH, PenType.HI_PEN, "高阶织物笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CRAYON, PenType.HI_PEN, "高阶磨砂笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CRAYON_DARK, PenType.HI_PEN, "高阶木炭笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_OIL, PenType.HI_PEN, "高阶丝纱笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_PIXEL, PenType.HI_PEN, "高阶像素笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_SOFT, PenType.HI_PEN, "高阶柔边笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_HAND, PenType.HI_PEN, "高阶手写笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_MARK, PenType.HI_PEN, "高阶马克笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CIRCLE, PenType.HI_PEN, "高阶硬圆笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CIRCLE_SOFT, PenType.HI_PEN, "高阶软圆笔", "", 0),
    PaintBean(Constants.PEN_ID_HI_CARBON_PENCILE, PenType.HI_PEN, "高阶碳铅笔", "", 0),
)

val penPencil = PaintBean(Constants.PEN_ID_HI_PENCIL, PenType.HI_PEN, "高阶铅笔", "", 1)

val penPictureList = arrayListOf(
    // 这不是个图案画笔，但设计归类以及名称被定义为图案16
    PaintBean(
        Constants.PEN_ID_MULTI_PIC_16,
        PenType.PICTURE,
        "图案16",
        "",
        0,
    ),
)