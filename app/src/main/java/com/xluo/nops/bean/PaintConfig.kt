package com.xluo.nops.bean

import android.graphics.PorterDuff
import com.xluo.nops.R
import com.xluo.pen.core.PenMixMode
import com.xluo.core.constants.Constants
import com.xluo.pen.bean.PaintBean
import com.xluo.pen.bean.PenType

val penMixModeList = arrayListOf(
    PenMixMode(0, "正片叠底", R.drawable.icon_zpdd, PorterDuff.Mode.ADD, true),
    PenMixMode(1, "变暗", R.drawable.icon_ba, PorterDuff.Mode.DARKEN),
    PenMixMode(2, "颜色加深", R.drawable.icon_ysjs, PorterDuff.Mode.OVERLAY),
    PenMixMode(3, "线性加深", R.drawable.icon_xxjs, PorterDuff.Mode.SRC_OVER),
    PenMixMode(4, "变亮", R.drawable.icon_bl, PorterDuff.Mode.LIGHTEN),
    PenMixMode(5, "颜色减淡", R.drawable.icon_ysjd, PorterDuff.Mode.SCREEN),
    PenMixMode(6, "叠加", R.drawable.icon_dj, PorterDuff.Mode.SRC),
    PenMixMode(7, "实色混合", R.drawable.icon_sshh, PorterDuff.Mode.XOR),
    PenMixMode(8, "差值", R.drawable.icon_cz, PorterDuff.Mode.SRC_OUT),
    PenMixMode(9, "减去", R.drawable.icon_jq, PorterDuff.Mode.DST_OUT),
    PenMixMode(10, "划分", R.drawable.icon_hf, PorterDuff.Mode.SRC_OVER),
    PenMixMode(11, "线性高度", R.drawable.icon_xxgd, PorterDuff.Mode.SRC_ATOP),
    PenMixMode(12, "高度", R.drawable.icon_xjhb, PorterDuff.Mode.DST_ATOP),
)

val shapeSelectedPicMap = mapOf(
    Constants.LINE to R.mipmap.icon_hbxz_pre_2,
    Constants.ELLIPSE to R.mipmap.icon_hbxz_pre_3,
    Constants.RECT to R.mipmap.icon_hbxz_pre_4,
    Constants.TRIANGLE to R.mipmap.icon_hbxz_pre_5,
)

val shapePenList = arrayListOf(
    PaintBean(Constants.LINE, PenType.SHAPE, "直线", "", R.mipmap.icon_hbxz_2, 0),
    PaintBean(Constants.TRIANGLE, PenType.SHAPE, "三角形", "", R.mipmap.icon_hbxz_5, 0),
    PaintBean(Constants.ELLIPSE, PenType.SHAPE, "椭圆", "", R.mipmap.icon_hbxz_3, 0),
    PaintBean(Constants.RECT, PenType.SHAPE, "矩形", "", R.mipmap.icon_hbxz_4, 0),
)