package com.xluo.nops.operation

import android.graphics.Bitmap

object PosterOperationUtils {

    var tempOperationListener: TempOperationListener? = null
    var draftOperationListener: DraftOperationListener? = null


    /**
     * 临时操作相关的回调
     */
    interface TempOperationListener {

        fun onCropImage(originUri: String, cropUri: String, cropBitmap: Bitmap)


        fun onUpdateText(text: String)

    }


    interface DraftOperationListener {

        fun refreshDrafts()

    }

    /**
     * 剪同款相关操作时 界面关闭才调用
     */
    fun destroy() {
        tempOperationListener = null
    }


}