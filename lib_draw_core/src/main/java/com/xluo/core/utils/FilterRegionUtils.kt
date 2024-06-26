package com.xluo.core.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import java.util.Stack
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class FilterRegionUtils {

    data class Point(val x: Int, val y: Int)

    data class Segment(val point: Point, val rect: Rect)

    private val segmentStack = Stack<Segment>()

    private val tolerance = 70

    private var rectF = RectF()

    private val markedPointMap = HashMap<Int, Boolean>()

    private val visitedSeedMap = HashMap<Int, Boolean>()

    private var width: Int = 0
    private var height: Int = 0

    private var pointColor: Int = 0

    private lateinit var pixels: IntArray

    private val segmentList = arrayListOf<Segment>()

    fun findColorRegion(x: Int, y: Int, bitmap: Bitmap, action: ((Path, RectF) -> Unit)) {
        markedPointMap.clear()
        segmentStack.clear()
        visitedSeedMap.clear()
        width = bitmap.width
        height = bitmap.height
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return
        }

        val region = Region()

        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        rectF = RectF(x.toFloat(), y.toFloat(), x.toFloat(), y.toFloat())

        // 拿到该bitmap的颜色数组
        pixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        pointColor = bitmap.getPixel(x, y)
        val point = Point(x, y)

        searchLineAtPoint(point)
        var index = 1
        while (segmentStack.isNotEmpty()) {
            val segment = segmentStack.pop()
            processSegment(segment)
            region.union(segment.rect)
            rectF.left = min(rectF.left, segment.rect.left.toFloat())
            rectF.top = min(rectF.top, segment.point.y.toFloat())
            rectF.right = max(rectF.right, segment.rect.right.toFloat())
            rectF.bottom = max(rectF.bottom, segment.point.y.toFloat())
            index++
        }
        val tempPath = region.boundaryPath
        path.addPath(tempPath)

        action.invoke(path, rectF)
    }

    private fun processSegment(segment: Segment) {
        val left = segment.rect.left
        val right = segment.rect.right
        val y = segment.point.y
        for (x in left .. right) {
            val top = y-1
            searchLineAtPoint(Point(x, top))
            val bottom = y+1
            searchLineAtPoint(Point(x, bottom))
        }
    }

    private fun searchLineAtPoint(point: Point) {
        if (point.x < 0 || point.x >= width || point.y < 0 || point.y >= height) return
        if (visitedSeedMap[point.y * width + point.x] != null) {
            return
        }
        if (!markPointIfMatches(point)) return
        // search left
        var left = point.x;
        var x = point.x - 1;
        while (x >= 0) {
            val lPoint = Point(x, point.y)
            if (markPointIfMatches(lPoint)) {
                left = x
            } else {
                break
            }
            x--
        }
        // search right
        var right = point.x
        x = point.x + 1
        while (x < width) {
            val rPoint = Point(x, point.y)
            if (markPointIfMatches(rPoint)) {
                right = x
            } else {
                break
            }
            x++
        }
        val segment = Segment(point, Rect(left, point.y-1, right, point.y+1))
        segmentList.add(segment)
        segmentStack.push(segment)
    }

    private fun markPointIfMatches(point: Point): Boolean {
        val offset = point.y*width + point.x
        val visited = visitedSeedMap[offset]
        if (visited != null) return false
        var matches = false
        if (matchPoint(point)) {
            matches = true
            markedPointMap[offset] = true
        }
        visitedSeedMap[offset] = true
        return matches
    }

    private fun matchPoint(point: Point): Boolean {
        val index = point.y*width + point.x
        val c1 = pixels[index]
        val t = max(max(abs(Color.red(c1)-Color.red(pointColor)), abs(Color.green(c1)-Color.green(pointColor))),
            abs(Color.blue(c1)-Color.blue(pointColor)))
        val alpha = abs(Color.alpha(c1)-Color.alpha((pointColor)))
        // 容差值范围内的都视作同一颜色
        return t < tolerance && alpha < tolerance
    }
}