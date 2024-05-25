package com.xluo.nops.utils

import android.app.Application
import android.graphics.Bitmap
import com.blankj.utilcode.util.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object CacheUtil {


    private lateinit var mContext: Application

    /**
     *临时导出路径
     */
    var TEMP_EXPORT_IMAGE_FOLDER = ""

    fun init(application: Application) {
        mContext = application
        TEMP_EXPORT_IMAGE_FOLDER = "${mContext.cacheDir.path}${File.separator}export${File.separator}"
        FileUtils.createOrExistsDir(TEMP_EXPORT_IMAGE_FOLDER)
    }

    fun bitmapSaveToLocal(bitmap: Bitmap, path: String): Boolean {
        return try {
            val os: OutputStream = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}