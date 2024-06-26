package com.xluo.nops

import android.content.Context
import android.net.Uri
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import java.io.File

fun Context.copyFileToCacheDir(cacheFile: File, sourceUri: Uri) {
    runCatching {
        cacheFile.createNewFile()
        cacheFile.outputStream().use {
            it.write(contentResolver.openInputStream(sourceUri)?.readBytes())
            it.flush()
        }
    }.onFailure {
        ToastUtils.showShort("获取图片失败")
    }

}

fun View.hide() {
    visibility = View.GONE
}
fun View.show() {
    visibility = View.VISIBLE
}

