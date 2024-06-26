package com.xluo.nops.utils

import android.Manifest
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ToastUtils
import com.xluo.lib_base.utils.PermissionUtil

object PermissUtils {

    fun getPicturePermission(activity: FragmentActivity, callback: (() -> Unit)) {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        PermissionUtil.requestPermisions(activity, list) { allGranted, grantedList, deniedList ->
            if (allGranted){
                callback.invoke()
            } else {
                ToastUtils.showShort("权限未开启")
            }
        }
    }
}