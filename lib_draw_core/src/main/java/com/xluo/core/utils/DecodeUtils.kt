package com.xluo.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import com.xluo.core.entity.PvsImageDecodeInfo
import com.blankj.utilcode.util.ImageUtils
import kotlin.math.min


object DecodeUtils {


    /**
     * 获取assets里的图片的bitmap
     * @param fileName String
     * @param maxWidth Int
     * @param maxHeight Int
     * @return PvsImageDecodeInfo
     */
    fun decodeBitmapByAssets(
        context: Context,
        fileName: String,
        maxWidth: Int,
        maxHeight: Int
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        var stream = context.assets.open(fileName)
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        val simpleSize = ImageUtils.calculateInSampleSize(opts, maxWidth, maxHeight)
        opts.inJustDecodeBounds = false
        opts.inSampleSize = simpleSize
        stream.close()
        stream = context.assets.open(fileName)
        var bitmap = BitmapFactory.decodeStream(stream, null, opts)
        pvsImageDecodeInfo.bitmap = bitmap
        pvsImageDecodeInfo.inSimpleSize = simpleSize
        bitmap?.let {
            pvsImageDecodeInfo.scaleWidth = it.width
            pvsImageDecodeInfo.scaleHeight = it.height
        }
        return pvsImageDecodeInfo
    }


    fun decodeBitmapByAssetsToSize(
        context: Context,
        fileName: String,
        width: Int,
        height: Int,
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        var stream = context.assets.open(fileName)
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        opts.inJustDecodeBounds = false
        opts.inSampleSize = 1
        stream.close()
        stream = context.assets.open(fileName)
        var bitmap = BitmapFactory.decodeStream(stream, null, opts)
        bitmap?.let {
            pvsImageDecodeInfo.bitmap = ImageUtils.scale(it, width, height)
        }
        pvsImageDecodeInfo.inSimpleSize = 1
        pvsImageDecodeInfo.scaleWidth = width
        pvsImageDecodeInfo.scaleHeight = width
        return pvsImageDecodeInfo
    }

    fun decodeBitmapByFileToSize(
        fileName: String,
        width: Int,
        height: Int,
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileName, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        opts.inJustDecodeBounds = false
        opts.inSampleSize = 1
        var bitmap = BitmapFactory.decodeFile(fileName, opts)
        bitmap?.let {

            pvsImageDecodeInfo.bitmap = ImageUtils.scale(it, width, height)
        }
        pvsImageDecodeInfo.inSimpleSize = 1
        pvsImageDecodeInfo.scaleWidth = width
        pvsImageDecodeInfo.scaleHeight = width
        return pvsImageDecodeInfo
    }

    fun decodeBitmapByFileToScale(
        fileName: String,
        canvasWidth: Int,
        canvasHeight: Int
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileName, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        opts.inJustDecodeBounds = false
        opts.inSampleSize = 1
        var bitmap = BitmapFactory.decodeFile(fileName, opts)
        bitmap?.let {
            val bmp = it.copy(Bitmap.Config.ARGB_8888, true)
            val scaleX = canvasWidth/it.width.toFloat()
            val scaleY = canvasHeight/it.height.toFloat()
            val minScale = min(scaleX, scaleY)
            val newWidth = it.width * minScale
            val newHeight = it.height * minScale
            pvsImageDecodeInfo.scaleWidth = newWidth.toInt()
            pvsImageDecodeInfo.scaleHeight = newHeight.toInt()
            val matrix = Matrix()
            matrix.postScale(minScale, minScale)
            pvsImageDecodeInfo.bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, false)
        }
        pvsImageDecodeInfo.inSimpleSize = 1
        return pvsImageDecodeInfo
    }

    //图片合成
    fun decodeBitmapByFileToSizeTPHE(
        fileName: String,
        width: Int,
        height: Int,
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileName, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        opts.inJustDecodeBounds = false
        opts.inSampleSize = 1
        var bitmap = BitmapFactory.decodeFile(fileName, opts)
        bitmap?.let {
            pvsImageDecodeInfo.bitmap = ImageUtils.scale(it, width, height)
        }
        pvsImageDecodeInfo.inSimpleSize = 1
        pvsImageDecodeInfo.scaleWidth = width
        pvsImageDecodeInfo.scaleHeight = height
        return pvsImageDecodeInfo
    }


    fun decodeBitmapByUri(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        var stream = context.contentResolver.openInputStream(uri)
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        val simpleSize = ImageUtils.calculateInSampleSize(opts, maxWidth, maxHeight)
        opts.inJustDecodeBounds = false
        opts.inSampleSize = simpleSize
        stream?.close()
        stream = context.contentResolver.openInputStream(uri)
        var bitmap = BitmapFactory.decodeStream(stream, null, opts)
        pvsImageDecodeInfo.bitmap = bitmap
        pvsImageDecodeInfo.inSimpleSize = simpleSize
        bitmap?.let {
            pvsImageDecodeInfo.scaleWidth = it.width
            pvsImageDecodeInfo.scaleHeight = it.height
        }
        stream?.close()
        return pvsImageDecodeInfo
    }


    fun decodeBitmapByPath(
        filePath: String,
        maxWidth: Int,
        maxHeight: Int
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        val simpleSize = ImageUtils.calculateInSampleSize(opts, maxWidth, maxHeight)
        opts.inJustDecodeBounds = false
        opts.inSampleSize = simpleSize
        var bitmap = BitmapFactory.decodeFile(filePath, opts)
        pvsImageDecodeInfo.bitmap = bitmap
        pvsImageDecodeInfo.inSimpleSize = simpleSize
        bitmap?.let {
            pvsImageDecodeInfo.scaleWidth = it.width
            pvsImageDecodeInfo.scaleHeight = it.height
        }
        return pvsImageDecodeInfo
    }


    fun decodeBitmapToCropSize(
        context: Context,
        uri: Uri,
        width: Int,
        height: Int,
    ): PvsImageDecodeInfo {
        val opts = BitmapFactory.Options()
        var stream = context.contentResolver.openInputStream(uri)
        val pvsImageDecodeInfo = PvsImageDecodeInfo()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, opts)
        pvsImageDecodeInfo.originWidth = opts.outWidth
        pvsImageDecodeInfo.originHeight = opts.outHeight
        val simpleSize = ImageUtils.calculateInSampleSize(opts, 1500, 1500)
        opts.inJustDecodeBounds = false
        opts.inSampleSize = simpleSize
        stream?.close()
        stream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(stream, null, opts)
        bitmap?.let {
            //需要裁剪bitmap
            val rect = RectUtils.getShowRect(it.width, it.height, width, height)
            pvsImageDecodeInfo.bitmap = ImageUtils.scale(
                ImageUtils.clip(
                    it,
                    rect.left,
                    rect.top,
                    rect.width(),
                    rect.height()
                ), width, height
            )

        }
//        bitmap?.recycle()
        pvsImageDecodeInfo.inSimpleSize = simpleSize
        pvsImageDecodeInfo.scaleWidth = width
        pvsImageDecodeInfo.scaleHeight = width
        return pvsImageDecodeInfo
    }

}