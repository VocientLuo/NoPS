package com.xluo.nops.ui.activity.fragment

import android.view.View

object ViewUtils {
    fun viewEnabled(view: View, enabled: Boolean) {
        if (enabled) {
            view.alpha = 1.0f
        } else {
            view.alpha = 0.5f
        }
        view.isEnabled = enabled
    }
}