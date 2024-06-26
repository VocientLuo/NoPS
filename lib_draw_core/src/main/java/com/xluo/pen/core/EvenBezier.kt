package com.xluo.pen.core

import com.blankj.utilcode.util.LogUtils
import com.xluo.pen.bean.ControllerPoint
import kotlin.math.sqrt

/**
 * 匀速贝塞尔
 */
class EvenBezier {
    private var lastControlPoint: ControllerPoint? = null
    private var filterWeight = 0.5
    private var filterWeightInverse = 1 - filterWeight
    // 偏移量
    var stepOffset = 0.0
    // 间距
    var stepInterval = 5.0

    private val cValue = 0.33
    private val cValue2 = 0.1666
    private val cValue3 = 0.66


    private val curRawStroke = arrayListOf<ControllerPoint>()
    private val curRawSampledStroke = arrayListOf<ControllerPoint>()
    private val curFilteredStroke = arrayListOf<ControllerPoint>()

    fun begin(point: ControllerPoint): List<ControllerPoint> {
        curRawStroke.clear()
        curRawSampledStroke.clear()
        curFilteredStroke.clear()
        lastControlPoint = point
        stepOffset = stepInterval
        return extStroke(point)
    }

    fun extStroke(point: ControllerPoint): List<ControllerPoint> {
        val stepPoints = arrayListOf<ControllerPoint>()
        curRawStroke.add(point)
        curRawSampledStroke.add(point)
        val size = curRawStroke.size
        if (size >= 3) {
            val fPoint = calFilteredPoint(
                curRawSampledStroke[size-3],
                curRawSampledStroke[size-2],
                curRawSampledStroke[size-1],
            )
            curFilteredStroke.add(fPoint)
        }
        val filteredSize = curFilteredStroke.size
        if (filteredSize >= 3) {
            val list = createBezier(
                curFilteredStroke[filteredSize - 3],
                curFilteredStroke[filteredSize - 2],
                curFilteredStroke[filteredSize - 1],
            )
            stepPoints.addAll(list)
        }
        return stepPoints
    }

    private fun calFilteredPoint(p1: ControllerPoint, p2: ControllerPoint, p3: ControllerPoint): ControllerPoint {
        val m = p1.getMidPoint(p3)
        return ControllerPoint(
            (filterWeight * p2.x + filterWeightInverse * m.x).toFloat(),
            (filterWeight * p2.y + filterWeightInverse * m.y).toFloat(),
            (filterWeight * p2.p + filterWeightInverse * m.p).toFloat(),
        )
    }

    fun endStroke(point: ControllerPoint): List<ControllerPoint> {
        val stepPoints = arrayListOf<ControllerPoint>()
        curRawStroke.add(point)
        curRawSampledStroke.add(point)
        val size = curRawSampledStroke.size
        if (size >= 3) {
            val fPoint = calFilteredPoint(
                curRawSampledStroke[size-3],
                curRawSampledStroke[size-2],
                curRawSampledStroke[size-1],
            )
            curFilteredStroke.add(fPoint)
        }

        val filteredSize = curFilteredStroke.size
        if (filteredSize >=3) {
            val list = createBezier(
                curFilteredStroke[filteredSize - 3],
                curFilteredStroke[filteredSize - 2],
                curFilteredStroke[filteredSize - 1],
            )
            stepPoints.addAll(list)
        }

        curRawStroke.add(point)
        curRawSampledStroke.add(point)
        val size1 = curRawSampledStroke.size
        if (size1 >= 3) {
            val fPoint = calFilteredPoint(
                curRawSampledStroke[size1-3],
                curRawSampledStroke[size1-2],
                curRawSampledStroke[size1-1],
            )
            curFilteredStroke.add(fPoint)
        } else {
            LogUtils.d("sample size1: $size1")
        }

        val filteredSize1 = curFilteredStroke.size
        if (filteredSize1 >=3) {
            val list = createBezier(
                curFilteredStroke[filteredSize1 - 3],
                curFilteredStroke[filteredSize1 - 2],
                curFilteredStroke[filteredSize1 - 1],
            )
            stepPoints.addAll(list)
        } else {
            LogUtils.d("sample filteredSize1: $filteredSize1")
        }
        return stepPoints
    }

    private fun createBezier(pt0: ControllerPoint, pt1: ControllerPoint,
                     pt2: ControllerPoint? = null): List<ControllerPoint> {
        val p0 = pt0
        val p3 = pt1
        val p0_x = p0.x
        val p0_y = p0.y
        val p0_p = p0.p
        val p3_x = p3.x
        val p3_y = p3.y
        val p3_p = p3.p
        val p1: ControllerPoint
        if (lastControlPoint == null) {
            p1 = ControllerPoint(
                (p0_x + (p3_x - p0_x)*cValue).toFloat(),
                (p0_y + (p3_y - p0_y)*cValue).toFloat(),
                (p0_p + (p3_p - p0_p)*cValue).toFloat(),
            )
        } else {
            p1 = lastControlPoint!!.getMirroredPoint(p0)
        }
        var p2: ControllerPoint
        if (pt2 != null) {
            p2 = ControllerPoint(
                (p3_x - (((p3_x - p0_x) + (pt2.x - p3_x)) * cValue2)).toFloat(),
                (p3_y - (((p3_y - p0_y) + (pt2.y - p3_y)) * cValue2)).toFloat(),
                (p3_p - (((p3_p - p0_p) + (pt2.p - p3_p)) * cValue2)).toFloat()
            )
        } else {
            p2 = ControllerPoint(
                (p0_x + (p3_x - p0_x) * cValue3).toFloat(),
                (p0_y + (p3_y - p0_y) * cValue3).toFloat(),
                (p0_p + (p3_p - p0_p) * cValue3).toFloat()
            )
        }
        lastControlPoint = p2
        return calStepPoints(p0, p1, p2, p3)
    }

    private fun calStepPoints(p0: ControllerPoint, p1: ControllerPoint, p2: ControllerPoint,
                      p3: ControllerPoint): List<ControllerPoint> {
        val stepPoints = arrayListOf<ControllerPoint>()
        var i = stepInterval

        // Value access
        var p0_x = p0.x
        var p0_y = p0.y
        var p0_p = p0.p

        // Algebraic conveniences, not geometric
        var A_x = p3.x - 3 * p2.x + 3 * p1.x - p0_x
        var A_y = p3.y - 3 * p2.y + 3 * p1.y - p0_y
        var A_p = p3.p - 3 * p2.p + 3 * p1.p - p0_p
        var B_x = 3 * p2.x - 6 * p1.x + 3 * p0_x
        var B_y = 3 * p2.y - 6 * p1.y + 3 * p0_y
        var B_p = 3 * p2.p - 6 * p1.p + 3 * p0_p
        var C_x = 3 * p1.x - 3 * p0_x
        var C_y = 3 * p1.y - 3 * p0_y
        var C_p = 3 * p1.p - 3 * p0_p

        var t = (i - stepOffset) / sqrt((C_x * C_x + C_y * C_y).toDouble())
        while (t <= 1.0) {
            // Point
            var step_x = t * (t * (t * A_x + B_x) + C_x) + p0_x
            var step_y = t * (t * (t * A_y + B_y) + C_y) + p0_y
            var step_p = t * (t * (t * A_p + B_p) + C_p) + p0_p
            stepPoints.add(ControllerPoint(
                step_x.toFloat(),
                step_y.toFloat(),
                step_p.toFloat()
            ));
            // Step distance until next one
            var s_x = t * (t * 3 * A_x + 2 * B_x) + C_x // dx/dt
            var s_y = t * (t * 3 * A_y + 2 * B_y) + C_y // dy/dt
            var s = sqrt(s_x * s_x + s_y * s_y) // s = derivative in 2D space
            var dt = i / s // i = interval / derivative in 2D
            t += dt
        }
        if (stepPoints.size == 0) // We didn't step at all along this Bezier
            stepOffset += p0.getDistance(p3)
        else
            stepOffset = stepPoints.last().getDistance(p3)
        return stepPoints
    }
}