package com.xluo.pen.core

import android.graphics.PorterDuff
import android.os.Build
import androidx.annotation.RequiresApi

data class PenMixMode(val id: Int, val name: String, val icon: Int, val value: PorterDuff.Mode, var selected: Boolean=false)