package com.xluo.core.utils

import android.graphics.Bitmap
import android.util.LruCache

object CacheUtils {

    const val NO_BG_BITMAP = "no_bg_bitmap"

    val imageCache = LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 4).toInt())


    fun getNoBgCache(): Bitmap? {
        return imageCache[NO_BG_BITMAP]
    }

    fun saveNoBgCache(bitmap: Bitmap) {
        imageCache.put(NO_BG_BITMAP,bitmap)
    }

}