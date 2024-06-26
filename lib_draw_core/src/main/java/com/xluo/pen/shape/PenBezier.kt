package com.xluo.pen.shape

import android.graphics.Canvas
import android.view.MotionEvent
import com.xluo.pen.bean.ControllerPoint
import com.xluo.pen.core.BasePen
import com.xluo.pen.core.Bezier
import com.xluo.pen.core.MotionElement
import java.util.Stack
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.ln

/**
 * 贝塞尔笔，主要实现贝塞尔能力，不做渲染
 */
abstract class PenBezier(id: Int) : BasePen(id) {
    var DIS_VEL_CAL_FACTOR = 0.02f

    private var mBezier = Bezier()

    init {
        penShapeEnabled = false
        penShapePicEnabled = false
        flowValueEnabled = false
        penVerticalEnabled = false
        penSquareEnabled = false
        rotationEnabled = false
    }

    // 值越大密度越大
    var density = 5

    var mHWPointList = Stack<ControllerPoint>()

    var mPointList = ArrayList<ControllerPoint>()

    var mLastPoint = ControllerPoint(0f, 0f)
    var mCurPoint = ControllerPoint(0f, 0f)

    //笔的宽度信息
    var mBaseWidth = 0.0

    var mLastVel = 0.0

    var mLastWidth = 0.0

    override fun onDraw(canvas: Canvas) {}

    open fun createMotionElement(motionEvent: MotionEvent): MotionElement {
        return MotionElement(
            motionEvent.x, motionEvent.y,
            motionEvent.pressure, motionEvent.size, motionEvent.getToolType(0)
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mBaseWidth = mPaint.strokeWidth.toDouble()
        val event2 = MotionEvent.obtain(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPath.reset()
                freshPen()
                onDown(createMotionElement(event2))
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                onMove(createMotionElement(event2))
                return true
            }

            MotionEvent.ACTION_UP -> {
                onUp(createMotionElement(event2))
                return true
            }

            else -> {}
        }
        return false
    }

    open fun onDown(mElement: MotionElement) {
        mPointList.clear()
        mHWPointList.clear()
        mCurPoint = ControllerPoint(mElement.x, mElement.y)
        mLastWidth = calPointWidth(mCurPoint, mElement)
        setCustomPointWidth(mCurPoint, MotionEvent.ACTION_DOWN)
        mLastVel = 0.0
        mPointList.add(mCurPoint)
        //记录当前的点
        mLastPoint = mCurPoint
    }

    private  fun calPointWidth(point: ControllerPoint, element: MotionElement): Double {
        // 记录宽度
        val width = if (element.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
            element.pressure * mBaseWidth
        } else {
            0.8 * mBaseWidth
        }
        point.width = width.toFloat()
        return width
    }

    open fun setCustomPointWidth(point: ControllerPoint, actionType: Int) {}

    open fun onMove(mElement: MotionElement) {
        mCurPoint = ControllerPoint(mElement.x, mElement.y)
        val deltaX = (mCurPoint.x - mLastPoint.x).toDouble()
        val deltaY = (mCurPoint.y - mLastPoint.y).toDouble()
        val curDis = hypot(deltaX, deltaY)
        val curVel: Double = curDis * DIS_VEL_CAL_FACTOR
        val curWidth: Double
        if (mPointList.size < 2) {
            if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = mElement.pressure * mBaseWidth
            } else {
                curWidth = calcNewWidth(curVel, mLastVel, 1.5)
            }
            mCurPoint.width = curWidth.toFloat()
            mBezier.init(mLastPoint, mCurPoint)
        } else {
            mLastVel = curVel
            if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
                curWidth = mElement.pressure * mBaseWidth
            } else {
                curWidth = calcNewWidth(curVel, mLastVel,1.5)
            }
            mCurPoint.width = curWidth.toFloat()
            mBezier.addNode(mCurPoint)
        }
        setCustomPointWidth(mCurPoint, MotionEvent.ACTION_DOWN)
        mLastWidth = mCurPoint.width.toDouble()
        mPointList.add(mCurPoint)
        genPointToDraw(curDis)
        mLastPoint = mCurPoint
    }

    private fun genPointToDraw(curDis: Double) {
        val steps: Int = 1 + curDis.toInt() / density
        val step = 1.0 / steps
        var t = 0.0
        while (t < 1.0) {
            val point = mBezier.getPoint(t)
            mHWPointList.add(getWithPointAlphaPoint(point))
            t += step
        }
    }

    private fun calcNewWidth(
        curVel: Double, lastVel: Double, factor: Double
    ): Double {
        val calVel = curVel * 0.6 + lastVel * (1 - 0.6)
        val vfac = ln(factor * 2.0f) * -calVel
        return mBaseWidth * exp(vfac)
    }

    open fun onUp(mElement: MotionElement) {
        mCurPoint = ControllerPoint(mElement.x, mElement.y)
        val deltaX = (mCurPoint.x - mLastPoint.x).toDouble()
        val deltaY = (mCurPoint.y - mLastPoint.y).toDouble()
        val curDis = hypot(deltaX, deltaY)
        //如果用笔画的画我的屏幕，记录他宽度的和压力值的乘，但是哇，这个是不会变的
        if (mElement.tooltype == MotionEvent.TOOL_TYPE_STYLUS) {
            mCurPoint.width = (mElement.pressure * mBaseWidth).toFloat()
        } else {
            mCurPoint.width = 0f
        }
        setCustomPointWidth(mCurPoint, MotionEvent.ACTION_DOWN)
        mPointList.add(mCurPoint)
        mBezier.addNode(mCurPoint)
        genPointToDraw(curDis)
        mBezier.end()
        genPointToDraw(curDis)
    }

    private fun getWithPointAlphaPoint(point: ControllerPoint): ControllerPoint {
        val nPoint = ControllerPoint()
        nPoint.x = point.x
        nPoint.y = point.y
        nPoint.width = point.width
        var pointAlpha = (this.alpha * point.width / size).toInt()
        if (pointAlpha < 10) {
            pointAlpha = 10
        } else if (pointAlpha > this.alpha) {
            pointAlpha = this.alpha
        }
        nPoint.alpha = pointAlpha
        return nPoint
    }
}