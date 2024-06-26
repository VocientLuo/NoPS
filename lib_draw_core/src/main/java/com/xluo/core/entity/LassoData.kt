package com.xluo.core.entity

import android.graphics.Path
import android.graphics.RectF
import com.xluo.core.widget.PvsCutView
import com.xluo.pen.bean.ControllerPoint


data class BezierInfo(val bezierList: ArrayList<ControllerPoint> = arrayListOf(), val pointList: ArrayList<ControllerPoint> = arrayListOf())

/**
 * 套索数据类
 */
data class LassoData(val type: PvsCutView.LassoType, var path: Path, val rectF: RectF, val bezierInfo: BezierInfo, var penSize: Float)
