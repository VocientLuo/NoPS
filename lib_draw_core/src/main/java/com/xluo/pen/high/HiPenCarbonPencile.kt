package com.xluo.pen.high

import android.graphics.Canvas
import android.graphics.Path
import com.xluo.draw_core.R
import com.xluo.pen.bean.ControllerPoint

/**
 * 高阶碳铅笔
 */
class HiPenCarbonPencile(id: Int) : HiPenBase(id) {
    init {
        resId = R.mipmap.carbon
        name = "碳铅笔"
    }

    override fun reset() {
        super.reset()
        updateBitmap(resId)
    }

    override fun onDraw(canvas: Canvas) {
        while (mHWPointList.isNotEmpty()) {
            val point = mHWPointList.pop()
            val path = genPath(point)
            canvas.drawPath(path, mPaint)
        }
    }

    private fun genPath(point: ControllerPoint): Path {
        val path = Path()
        val realSize = getRealSize(point.p)
        val p1x = point.x-realSize/3
        val p1y = point.y-realSize/2
        val p2x = point.x-realSize/2
        val p2y = point.y
        val p3x = point.x+realSize/2
        val p3y = point.y+realSize/3
        path.moveTo(p1x, p1y)
        path.quadTo(p1x, p1y, p2x, p2y)
        path.quadTo(p2x, p2y, p3x, p3y)
        return path
    }
}