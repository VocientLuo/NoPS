package com.xluo.core.utils

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewConfiguration
import java.lang.Math.abs

val Int.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 检测是不是点击事件
 */
fun View.checkTouchIsClick(distanceX: Float, distanceY: Float): Boolean {
    return abs(distanceX) <= ViewConfiguration.get(context).scaledTouchSlop && abs(distanceY) <= ViewConfiguration.get(context).scaledTouchSlop
}



