package com.xluo.core.widget

import android.content.Context
import android.util.AttributeSet

/**
 * Create by AjjAndroid
 * Time:2022/2/16
 * 图片编辑手势操作控件,缩放等操作还暂未实现
 */
open class PvsTouchView : PvsBaseView {

    var offsetX = 0f
    var offsetY = 0f


    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attributeSet, defStyleAttr, defStyleRes)

}