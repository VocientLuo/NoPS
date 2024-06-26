package com.xluo.pen.core

import android.graphics.RectF
import android.view.MotionEvent
import android.view.VelocityTracker
import com.xluo.pen.bean.ControllerPoint
import java.util.ArrayList
import java.util.Stack
import kotlin.math.hypot
import kotlin.math.max

/**
 * Created by xluo on 2023/4/15.
 * 纯贝塞尔点采集
 */
abstract class PenPureBezier(id: Int) : BasePen(id) {
    // 绘制点
    var mHWPointList = Stack<ControllerPoint>()
    var mBezier = EvenBezier()
    val rect = RectF()

    val tracker = VelocityTracker.obtain()

    private fun getStepSpace(): Double {
        // 实际间距需要space与size共同作用
        var interval = space*size/40
        if (interval < minSpace) {
            interval = minSpace
        }
        if (interval > maxSpace) {
            maxSpace
        }
        return interval.toDouble()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                tracker.clear()
                freshPen()
                touchDown(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchUp(event)
                return true
            }
        }
        return false
    }

    private fun touchDown(e: MotionEvent) {
        super.touchDown(e.x, e.y)
        tracker.addMovement(e)
        mBezier.stepInterval = getStepSpace()
        mHWPointList.clear()
        val list = mBezier.begin(ControllerPoint(e.x, e.y, getPressValue(e)))
        mHWPointList.addAll(list)
    }

    private fun touchMove(e: MotionEvent) {
        super.touchMove(e.x, e.y)
        tracker.addMovement(e)
        val list = mBezier.extStroke(ControllerPoint(e.x, e.y, getPressValue(e)))
        mHWPointList.addAll(list)
    }

    private fun touchUp(e: MotionEvent) {
        super.touchUp(e.x, e.y)
        tracker.addMovement(e)
        val list = mBezier.endStroke(ControllerPoint(e.x, e.y, getPressValue(e)))
        mHWPointList.addAll(list)
    }

    private fun getPressValue(e: MotionEvent): Float {
        var p = e.pressure
        val minPress = minDiam/size
        return max(p, max(minPress, 0.2f))
    }

}