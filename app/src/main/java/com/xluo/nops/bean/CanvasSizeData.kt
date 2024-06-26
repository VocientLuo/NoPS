package com.xluo.nops.bean

import android.content.Context
import android.util.TypedValue
import com.xluo.nops.R
import com.xluo.nops.utils.ScreenUtils

val sizePics = arrayListOf(
    R.mipmap.draw_aa_icon_9_16,R.mipmap.draw_aa_icon_3_4,
    R.mipmap.draw_aa_icon_1_1,R.mipmap.draw_aa_icon_16_9,
    R.mipmap.draw_aa_icon_4_3,R.mipmap.draw_aa_icon_9_16,R.mipmap.draw_aa_icon_a4,R.mipmap.draw_aa_icon_a4,
    R.mipmap.draw_aa_icon_a4
)

data class Size(val resId: Int, val name: String, val width: Int, val height: Int, val realWith: Int, val realHeight: Int, var maxLayer: Int = ScreenUtils.BASE_LAYER_COUNT) {
    init {
        maxLayer = ScreenUtils.getMaxLayerCount(realWith, realHeight)
    }
}

private fun getRealSize(context: Context, unit: Int, size: Int): Int {
    val dm = context.resources.displayMetrics
    return TypedValue.applyDimension(unit, size.toFloat(), dm).toInt()
}


fun getNewCanvasSizeList(context: Context): ArrayList<Size> {
    val list = arrayListOf<Size>()
    val screenInfo = ScreenUtils.getScreenSize(context)
    val width = screenInfo[0]
    val height = screenInfo[1]

    // 9:16
    var displayHeight = width*16/9
    var realHeight = getRealSize(context, TypedValue.COMPLEX_UNIT_PX, displayHeight)
    list.add(Size(sizePics[0],"9:16", width, displayHeight, width, realHeight))

    displayHeight = width*4/3
    realHeight = getRealSize(context, TypedValue.COMPLEX_UNIT_PX, displayHeight)
    list.add(Size(sizePics[1],"3:4", width, displayHeight, width, realHeight))
    // 1:1
    list.add(Size(sizePics[2],"1:1", width, width, width, width))

    displayHeight = width*9/16
    realHeight = getRealSize(context, TypedValue.COMPLEX_UNIT_PX, displayHeight)
    list.add(Size(sizePics[3],"16:9", width, displayHeight, width, realHeight))

    displayHeight = width*3/4
    realHeight = getRealSize(context, TypedValue.COMPLEX_UNIT_PX, displayHeight)
    list.add(Size(sizePics[4],"4:3", width, width*3/4, width, realHeight))
    // 屏幕大小
    list.add(Size(sizePics[5],"屏幕", width, height, width, height))

    // A4
    list.add(
        Size(
            sizePics[6],"A4", 210, 297, getRealSize(context, TypedValue.COMPLEX_UNIT_MM, 210),
        getRealSize(context, TypedValue.COMPLEX_UNIT_MM, 297)
        )
    )
    // A5
    list.add(
        Size(
            sizePics[7],"A5", 148, 210, getRealSize(context, TypedValue.COMPLEX_UNIT_MM, 148),
        getRealSize(context, TypedValue.COMPLEX_UNIT_MM, 210)
        )
    )
    // 明信片
    list.add(
        Size(
            sizePics[8],"明信片", 100, 148, getRealSize(context, TypedValue.COMPLEX_UNIT_MM, 100),
        getRealSize(context, TypedValue.COMPLEX_UNIT_MM, 148)
        )
    )
    return list
}