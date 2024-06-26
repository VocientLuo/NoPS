package com.xluo.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.MotionEvent
import com.xluo.core.entity.PvsRichLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Create by xl
 * Time:2023/10/24
 * 液化, 算法要升级
 */
class PvsDistortUtils{

    enum class DistortType {
        Push, // 推
        Reset, // 恢复
        LtoR, // 顺时针
        RtoL, // 逆时针
        Extend, // 扩展
        Shrink, // 收缩
    }

    var originBitmap: Bitmap? = null

    var radius: Float = 50f

    var strong: Float = 0.05f

    var distortType: DistortType = DistortType.Push

    var verts: FloatArray? = null
    var lastVerts: FloatArray? = null
    var orig: FloatArray? = null

    private val meshStepList: ArrayList<FloatArray> = ArrayList()

    // mesh网格数
    private val density = 5
    var cWidth = 200
    var cHeight = 200
    var count = (cWidth + 1) * (cHeight + 1)

    var bWidth: Int = 0
    var bHeight: Int = 0

    var pointX = 0f
    var pointY = 0f
    var lastPointX = 0f
    var lastPointY = 0f

    private var pvsRichLayer: PvsRichLayer? = null

    private var canvas: Canvas? = null

    private var onUpdate: (() -> Unit)

    private var meshing = false

    constructor(update: (() -> Unit)) {
        onUpdate = update
    }

    var circlePaint: Paint = Paint().apply {
        color = Color.parseColor("#666666")
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }


    fun start(richLayer: PvsRichLayer) {
        pvsRichLayer = richLayer
        meshStepList.clear()
        richLayer.let {
            it.bitmap?.let { bmp ->
                GlobalScope.launch(Dispatchers.IO) {
                    bWidth = bmp.width
                    bHeight = bmp.height
                    //updateMeshInfo()
                    canvas = it.canvas
                    originBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true) //genBitmap(bWidth, bHeight)
                    verts = FloatArray(count*2)
                    orig = FloatArray(count*2)
                    lastVerts = FloatArray(count*2)
                    var index = 0
                    for (i in 0 until cHeight+1) {
                        val fy = bHeight * i / cHeight
                        for (j in 0 until cWidth+1) {
                            val fx = bWidth * j / cWidth
                            verts!![index*2] = fx.toFloat()
                            orig!![index*2] = verts!![index*2]
                            lastVerts!![index*2] = verts!![index*2]
                            verts!![index*2+1] = fy.toFloat()
                            orig!![index*2+1] = verts!![index*2+1]
                            lastVerts!![index*2+1] = verts!![index*2+1]
                            index++
                        }
                    }
                    withContext(Dispatchers.Main) {
                        invalidate()
                    }
                }
            }
        }
    }

    fun resetDistort() {
        canvas?.let {
            it.drawColor(0, PorterDuff.Mode.CLEAR)
            it.drawBitmap(originBitmap!!, 0f, 0f, null)
            pvsRichLayer?.let {layer->
                start(layer)
            }
        }
    }

    fun stopDistort() {
        if (!meshing) {
            return
        }
        meshing = false
        pointX = 0f
        pointY = 0f
        lastPointX = 0f
        lastPointY = 0f
        invalidate()
    }

    private fun invalidate() {
        onDraw()
        onUpdate.invoke()
    }

    fun release() {
        verts = null
        orig = null
        lastVerts = null
        originBitmap = null
    }

    fun onDraw() {
        drawMesh()
        GlobalScope.launch(Dispatchers.IO) {
            meshStepList.add(verts!!.clone())
        }
    }

    private fun drawMesh() {
        canvas?.let {
            // 先擦掉原来的图像
            it.drawColor(0, PorterDuff.Mode.CLEAR)
            // 开始绘制
            originBitmap?.let { bmp ->
                it.drawBitmapMesh(bmp, cWidth, cHeight, verts!!, 0, null, 0, null)
            }
            if (pointX > 0 && pointY > 0) {
                it.drawCircle(pointX, pointY, radius, circlePaint)
            }
        }
    }

    fun showMeshIndex(progress: Int) {
        if (meshStepList.isEmpty()) {
            return
        }
        var step = meshStepList.size*progress/100 -1
        step = if (step < 0) {
            0
        } else {
            step
        }
        verts = meshStepList[step].clone()
        drawMesh()
    }

    fun onTouchEvent(event: MotionEvent) {
        pointX = event.x
        pointY = event.y
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (meshing) {
                    return
                }
                meshing = true
                startMesh()
            }
            MotionEvent.ACTION_UP -> {
                stopDistort()
            }
        }
    }

    private fun startMesh() {
        GlobalScope.launch(Dispatchers.IO) {
            while (meshing) {
                when (distortType) {
                    DistortType.Extend -> {
                        extendPoint(pointX, pointY)
                    }
                    DistortType.Shrink -> {
                        shrinkPoint(pointX, pointY)
                    }
                    DistortType.LtoR -> {
                        ltorPoint(pointX, pointY)
                    }
                    DistortType.RtoL -> {
                        rtolPoint(pointX, pointY)
                    }
                    DistortType.Push -> {
                        pushPoint(pointX, pointY)
                    }
                    DistortType.Reset -> {
                        resetPoint(pointX, pointY)
                    }
                }
                if (meshing) {
                    withContext(Dispatchers.Main) {
                        invalidate()
                    }
                }
                sleep(10)
            }
        }
    }

    private val offsetAngle = 0.5f

    /**
     * 顺时针旋转
     */
    private fun ltorPoint(cx: Float, cy: Float) {
        var i = 0
        while (i < lastVerts!!.size) {
            val dx = cx - lastVerts!![i]
            val dy = cy - lastVerts!![i + 1]
            val dd = dx * dx + dy * dy
            val d = sqrt(dd)
            if (d < radius) {
                val originAngle = atan2(dy, dx)
                val currentAngle = originAngle - offsetAngle
                val ax = d * cos(currentAngle) * strong
                val ay = d * sin(currentAngle) * strong
                verts!![i] = lastVerts!![i] + ax
                verts!![i + 1] = lastVerts!![i + 1] + ay
            }
            i += 2
        }
        lastVerts = verts
    }

    /**
     * 逆时针旋转
     */
    private fun rtolPoint(cx: Float, cy: Float) {
        var i = 0
        while (i < lastVerts!!.size) {
            val dx = cx - lastVerts!![i + 0]
            val dy = cy - lastVerts!![i + 1]
            val dd = dx * dx + dy * dy
            val d = sqrt(dd)
            if (d < radius) {
                val originAngle = atan2(dx, dy)
                val currentAngle = originAngle + offsetAngle
                val ax = d * cos(currentAngle) * strong
                val ay = d * sin(currentAngle) * strong
                verts!![i] = lastVerts!![i] - ax
                verts!![i + 1] = lastVerts!![i + 1] - ay
            }
            i += 2
        }
        lastVerts = verts
    }

    /**
     * 扩展
     */
    private fun extendPoint(cx: Float, cy: Float) {
        var i = 0
        while (i < lastVerts!!.size) {
            val dx = cx - lastVerts!![i + 0]
            val dy = cy - lastVerts!![i + 1]
            val dd = dx * dx + dy * dy
            //计算每个座标点与当前点（cx、cy）之间的距离
            val d = sqrt(dd)
            //对verts数组（保存bitmap上21 * 21个点经过扭曲后的座标）重新赋值
            if (d < radius) {
                verts!![i + 0] = lastVerts!![i + 0] - dx * strong
                verts!![i + 1] = lastVerts!![i + 1] - dy * strong
            }
            i += 2
        }
        lastVerts = verts
    }

    /**
     * 扩展
     */
    private fun shrinkPoint(cx: Float, cy: Float) {
        var i = 0
        while (i < lastVerts!!.size) {
            val dx = cx - lastVerts!![i + 0]
            val dy = cy - lastVerts!![i + 1]
            val dd = dx * dx + dy * dy
            //计算每个座标点与当前点（cx、cy）之间的距离
            val d = Math.sqrt(dd.toDouble()).toFloat()
            //对verts数组（保存bitmap上21 * 21个点经过扭曲后的座标）重新赋值
            if (d < radius - 1) {
                verts!![i + 0] = lastVerts!![i + 0] + dx * strong
                verts!![i + 1] = lastVerts!![i + 1] + dy * strong
            }
            i += 2
        }
        lastVerts = verts
    }

    /**
     * 恢复
     */
    private fun resetPoint(cx: Float, cy: Float) {
        var i = 0
        while (i < verts!!.size) {
            val dx = cx - verts!![i + 0]
            val dy = cy - verts!![i + 1]
            val dd = dx * dx + dy * dy
            val d = sqrt(dd.toDouble()).toFloat()
            if (d < radius) {
                verts!![i] = orig!![i]
                verts!![i + 1] = orig!![i+1]
            }
            i += 2
        }
    }

    /**
     * 推
     */
    private fun pushPoint(cx: Float, cy: Float) {
        var i = 0
        while (i < lastVerts!!.size) {
            if (lastPointX == 0f || lastPointY == 0f || pointX == 0f || pointY == 0f) {
                break
            }
            val dx = cx - lastVerts!![i + 0]
            val dy = cy - lastVerts!![i + 1]
            val dd = dx * dx + dy * dy
            val d = sqrt(dd)
            if (d < radius) {
                verts!![i] = verts!![i] + (cx-lastPointX)*strong
                verts!![i+1] = verts!![i+1] + (cy-lastPointY)*strong
            }
            i += 2
        }
        lastPointX = cx
        lastPointY = cy
        lastVerts = verts
    }
}