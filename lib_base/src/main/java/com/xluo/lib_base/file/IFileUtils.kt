package com.xluo.lib_base.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.RuntimeException



class IFileUtils {
    companion object {
        const val TAG = "IFileUtils"
        const val COMMON_DIR = "common"
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            IFileUtils()
        }
    }

    private var mContext: Context? = null

    fun init(context: Context) {
        Log.d(TAG, "IFileUtils inited.")
        mContext = context
    }

    fun getFileExt(type: IFileType): String {
        return when (type) {
            IFileType.TXT -> "txt"
            IFileType.LOG -> "log"
            IFileType.JPG -> "jpg"
            IFileType.PNG -> "png"
            IFileType.NONE -> ""
        }
    }

    private fun checkUse(): Boolean {
        if (mContext == null) {
            Log.e(TAG, "getAppDir failed. please use init method in application")
            throw RuntimeException("getAppDir failed. please use init method in application")
        }
        return true
    }

    fun getAppDir(): String {
        var dir = ""
        if (checkUse()) {
            val file = mContext!!.filesDir.path
            dir = file.toString()
        }
        return dir
    }

    fun createFile(type: IFileType): File {
        val filePath = "${getAppDir()}/${System.currentTimeMillis()}.${getFileExt(type)}"
        return File(filePath)
    }

    fun createCustomFile(ext: String): File {
        val filePath = "${getAppDir()}/${System.currentTimeMillis()}.${ext}"
        return File(filePath)
    }

    /**
     * ================save image==================
     */

    fun saveBitmap2Png(bitmap: Bitmap): String {
        val file = createFile(IFileType.PNG)
        saveBitmap(file, bitmap, Bitmap.CompressFormat.PNG)
        return file.path
    }

    fun saveBitmap2Jpg(bitmap: Bitmap): String {
        val file = createFile(IFileType.JPG)
        saveBitmap(file, bitmap, Bitmap.CompressFormat.JPEG)
        return file.path
    }

    private fun saveBitmap(file: File, bitmap: Bitmap, format: Bitmap.CompressFormat) {
        val fos = FileOutputStream(file, false)
        bitmap.compress(format, 100, fos)
        fos.flush()
    }

    /**
     * ===================read bitmap====================
     */

    fun readBitmapByPath(path: String): Bitmap? {
        if (!File(path).exists()) {
            return null
        }
        return BitmapFactory.decodeFile(path)
    }

    /**
     * ===============save image to gallery===========
     */

    /**
     * @param
     * bitmap bitmap
     * imageType IImageType enum
     * return boolean 成功=true
     */
    fun saveBitmap2Gallery(bitmap: Bitmap, imageType: IImageType): Boolean {
        val ext = when (imageType) {
            IImageType.JPEG,IImageType.JPG -> {
                ".jpg"
            }
            IImageType.PNG -> {
                ".png"
            }
            IImageType.GIF -> {
                ".gif"
            }
            IImageType.WEBP -> {
                ".webp"
            }
        }
        val fileName = "${System.currentTimeMillis()}$ext"
        mContext?.let {
            bitmap.saveToAlbum(it, fileName)
            return true
        }
        return false
    }

    /**
     * @param
     * path 文件路径
     * return boolean 成功=true
     */
    fun saveImage2GalleryByPath(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) {
            return false
        }
        val fileName = file.name
        val bitmap = readBitmapByPath(path)
        mContext?.let {
            bitmap?.saveToAlbum(it, fileName)
            return true
        }
        return false
    }

    fun saveVideo2Gallery(path: String): Boolean {
        mContext?.let {
            return saveVideoToGallery(it, path)
        }
        return false
    }

    /**
     * ==============save file==================
     */

    fun saveTxtFile(content: String): String {
        return saveTxtFile(content.byteInputStream())
    }

    fun saveTxtFile(inputStream: InputStream): String {
        val fileName = "${System.currentTimeMillis()}.txt"
        return saveFile(fileName, inputStream)
    }

    fun saveFile(fileName: String, inputStream: InputStream): String {
        val filePath = "${getAppDir()}/${fileName}"
        val file = File(filePath)
        if (file.exists()) {
            Log.e(TAG, "file already exists!")
            return ""
        }
        val outputStream = BufferedOutputStream(FileOutputStream(file))
        val bytes = ByteArray(8192)
        var i: Int
        while (inputStream.read(bytes).also { i = it } != -1) {
            outputStream.write(bytes, 0, i)
        }
        outputStream.close()
        return filePath
    }

    fun saveFileForce(fileName: String, inputStream: InputStream): String {
        val filePath = "${getAppDir()}/${fileName}"
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        val outputStream = BufferedOutputStream(FileOutputStream(file))
        val bytes = ByteArray(8192)
        var i: Int
        while (inputStream.read(bytes).also { i = it } != -1) {
            outputStream.write(bytes, 0, i)
        }
        outputStream.close()
        return filePath
    }

    fun getFileByName(fileName: String): File? {
        val filePath = "${getAppDir()}/${fileName}"
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "file not exists")
            return null
        }
        return file
    }
}