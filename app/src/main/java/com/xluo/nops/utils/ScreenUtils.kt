package com.xluo.nops.utils

import android.content.Context

class ScreenUtils {
    companion object {
        const val BASE_LAYER_COUNT = 30
        private const val BASE_LAYER_SIZE = 1080*1920

        fun getScreenSize(context: Context): ArrayList<Int> {
            val dm = context.resources.displayMetrics
            return arrayListOf(
                dm.widthPixels,
                dm.heightPixels
            )
        }

        fun getScreenInfo(context: Context, action: ((w: Int, h: Int) -> Unit)) {
            val dm = context.resources.displayMetrics
            action.invoke(dm.widthPixels, dm.heightPixels)
        }

        fun getMaxLayerCount(width: Int, height: Int): Int {
            return (BASE_LAYER_COUNT / ((width * height).toFloat() / BASE_LAYER_SIZE.toFloat())).toInt()
        }
    }
}