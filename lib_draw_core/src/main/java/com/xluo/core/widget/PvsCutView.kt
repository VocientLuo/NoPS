package com.xluo.core.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.graphics.toRect
import com.xluo.core.entity.BezierInfo
import com.xluo.core.entity.LassoData
import com.xluo.core.utils.FilterRegionUtils
import com.xluo.pen.bean.ControllerPoint
import com.xluo.pen.core.Bezier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min


/**
 * Create by xluo
 * Time:2023/7/24
 * 选区裁剪
 */
class PvsCutView : PvsTouchView {
    enum class LassoType {
        LASSO,
        PAINT,
        RECT,
        OVAL,
        MAGIC,
    }

    enum class FillType {
        ADD,
        REDUCE
    }
    
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    var fillType = FillType.ADD

    var lassoType = LassoType.LASSO

    var region = Region()

    // 画笔大小，选区工具为画笔时生效
    var penSize = 50f

    // 非选中区半透明黑色
    private val coverColor = Color.parseColor("#5f000000")

    private val dashPaint = Paint().apply {
        color = Color.RED
    }

    private val lassoInfoList = arrayListOf<LassoData>()

    private var needInitRectF = true

    var isCutting = false

    var mBezier = Bezier()
    var mLastPoint = ControllerPoint(0f, 0f)

    var targetPath = Path()

    var dashSize = 8f

    private var lastPointX = 0f
    private var lastPointY = 0f

    /**
     * 正选or反选
     */
    var isNormal = true

    var isPointUp = false

    private var pvsEditView:PvsEditView? = null

    private var filterRegionUtils = FilterRegionUtils()

    var actionBitmap: ((Bitmap) -> Unit)? = null

    private var dashAnimate = ValueAnimator()
    private var dashMax = 40
    private var dashMin = 1

    var actionShowLoading: (() -> Unit)? = null
    var actionHideLoading: (() -> Unit)? = null

    fun onReverse() {
        isNormal = !isNormal
        invalidate()
    }

    private fun init() {
        dashPaint.pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 20f)
        dashPaint.style = Paint.Style.STROKE
        dashPaint.strokeCap = Paint.Cap.ROUND
        startDashAnimate()
    }

    fun start(editView: PvsEditView) {
        pvsEditView = editView
        needInitRectF = true
        targetPath = Path()
        visibility = VISIBLE
        isCutting = true
    }

    fun finish() {
        targetPath.reset()
        region.setEmpty()
        lassoInfoList.clear()
        visibility = INVISIBLE
        isCutting = false
        isNormal = true
        mBezier = Bezier()
    }

    private fun startDashAnimate() {
        dashAnimate.setIntValues(dashMin, dashMax)
        dashAnimate.duration = 4000
        dashAnimate.addUpdateListener {
            val dash = it.animatedValue as Int
            dashPaint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), dash.toFloat())
            invalidate()
        }
        dashAnimate.repeatCount = ValueAnimator.INFINITE
        dashAnimate.start()
    }

    private fun pauseAnim() {
        dashAnimate.pause()
    }

    private fun resumeAnim() {
        dashAnimate.resume()
    }

    private fun findRegionPath(event: MotionEvent) {
        actionShowLoading?.invoke()
        GlobalScope.launch(Dispatchers.IO) {
            pvsEditView?.let {
                it.saveToPhoto(true)?.let {bitmap ->
                    filterRegionUtils.findColorRegion(event.x.toInt(), event.y.toInt(), bitmap) {path, r ->
                        addPath(path, r)
                        GlobalScope.launch(Dispatchers.Main) {
                            invalidate()
                            actionHideLoading?.invoke()
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (lassoType == LassoType.MAGIC) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    pauseAnim()
                    lastPointX = event.x
                    lastPointY = event.y
                }
                MotionEvent.ACTION_MOVE -> {

                }
                MotionEvent.ACTION_UP -> {
                    resumeAnim()
                    if (abs(lastPointX.toInt()-event.x.toInt()) == 0 && abs(lastPointY.toInt()-event.y.toInt()) == 0) {
                        findRegionPath(event)
                    }
                }
            }
            return true
        }
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            isPointUp = false
            pauseAnim()
            dashPaint.strokeWidth = dashSize
            val path = Path()
            path.moveTo(event.x, event.y)
            val rectF = RectF(event.x, event.y, event.x, event.y)
            val bezierInfo = BezierInfo()
            if (lassoType == LassoType.PAINT) {
                val point = ControllerPoint(event.x, event.y)
                bezierInfo.pointList.add(point)
                mLastPoint = point
            }
            lassoInfoList.add(LassoData(lassoType, path, rectF, bezierInfo, penSize))
        }
        when (lassoType) {
            LassoType.LASSO -> collectLasso(event)
            LassoType.PAINT -> collectCircle(event)
            LassoType.RECT -> collectRect(event)
            LassoType.OVAL -> collectOval(event)
            else -> {}
        }
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            resumeAnim()
            isPointUp = true
        }
        invalidate()
        return true
    }

    private fun collectCircle(event: MotionEvent) {
        val lassoData = lassoInfoList.last()
        val currentX = event.x
        val currentY = event.y
        var deltaX: Double = (currentX - mLastPoint.x).toDouble()
        var deltaY: Double = (currentY - mLastPoint.y).toDouble()
        val curPointDis = hypot(deltaX, deltaY)
        val curPoint = ControllerPoint(currentX, currentY)
        if (lassoData.bezierInfo.pointList.size < 2) {
            mBezier.init(mLastPoint, curPoint)
        } else {
            mBezier.addNode(curPoint)
        }
        if (lassoData.bezierInfo.pointList.size > 3) {
            lassoData.bezierInfo.pointList.removeAt(0)
        }
        lassoData.bezierInfo.pointList.add(curPoint)
        val steps = 1 + curPointDis.toInt() / 10
        val step = 1.0 / steps
        var t = 0.0
        // circle 不要低于50f
        val size = max(penSize, 50f)
        while (t < 1.0) {
            val point: ControllerPoint = mBezier.getPoint(t)
            val path = Path()
            path.addCircle(point.x, point.y, size, Path.Direction.CW)
            val rectF = RectF(point.x - size, point.y - size, point.x + size, point.y + size)
            addPath(path, rectF)
            t += step
        }
        mLastPoint = curPoint
    }

    private fun collectRect(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                updateRect(lassoInfoList.last().rectF, event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                val lassoData = lassoInfoList.last()
                updateRect(lassoData.rectF, event.x, event.y)
                val path = Path()
                path.addRect(lassoData.rectF, Path.Direction.CW)
                addPath(path, lassoData.rectF)
            }
        }
    }

    private fun collectOval(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                updateRect(lassoInfoList.last().rectF, event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                val lassoData = lassoInfoList.last()
                updateRect(lassoData.rectF, event.x, event.y)
                val path = Path()
                path.addOval(lassoData.rectF, Path.Direction.CW)
                addPath(path, lassoData.rectF)
            }
        }
    }

    private fun addPath(path: Path, rect: RectF) {
        var regionOp: Region.Op
        val op = if (fillType == FillType.ADD) {
            regionOp = Region.Op.UNION
            Path.Op.UNION
        } else {
            regionOp = Region.Op.DIFFERENCE
            Path.Op.DIFFERENCE
        }
        region.op(rect.toRect(), regionOp)
        targetPath.op(path, op)
    }

    private fun updateRect(rectF: RectF, x: Float, y: Float) {
        rectF.right = x
        rectF.bottom = y
    }

    /**
     * 套索
     */
    private fun collectLasso(event: MotionEvent) {
        val lassoData = lassoInfoList.last()
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lassoData.path.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                lassoData.path.lineTo(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {

                lassoData.path.lineTo(event.x, event.y)
                lassoData.path.close()
            }
        }
        freshRectF(lassoData.rectF, event.x, event.y)
        addPath(lassoInfoList.last().path, lassoData.rectF)
    }

    private fun freshRectF(rectF: RectF, x: Float, y: Float) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return
        }
        if (!rectF.contains(x, y)) {
            rectF.left = min(rectF.left, x)
            rectF.top = min(rectF.top, y)
            rectF.right = max(rectF.right, x)
            rectF.bottom = max(rectF.bottom, y)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        // 绘制选区
        drawSelect(canvas)
        // 绘制覆盖色
        canvas.drawColor(coverColor)
        // 绘制虚线框
        drawDash(canvas)
    }

    private fun drawSelect(canvas: Canvas) {
        val op = if (isNormal) {
            Region.Op.DIFFERENCE
        } else {
            Region.Op.INTERSECT
        }
        canvas.clipPath(targetPath, op)
    }

    private fun drawDash(canvas: Canvas) {
        if (lassoInfoList.isNotEmpty()) {
            lassoInfoList.last().let {
                if (!isPointUp) {
                    if (it.type == LassoType.RECT) {
                        canvas.drawRect(it.rectF, dashPaint)
                    } else if (it.type == LassoType.OVAL) {
                        canvas.drawOval(it.rectF, dashPaint)
                    }
                }
            }
        }
        canvas.drawPath(targetPath, dashPaint)

        if (!isNormal) {
            // 反选时在外层画虚线框
            val r = RectF(dashSize, dashSize, (width - dashSize), (height - dashSize))
            canvas.drawRect(r, dashPaint)
        }
    }

    fun canOperate(): Boolean {
        return (!region.isEmpty) || !isNormal
    }

    fun genAreaBitmap(src: Bitmap, action: ((Bitmap, RectF) -> Unit)){
        if (!canOperate()) {
            return
        }
        // 根据裁剪区域生成bitmap
        val srcCopy = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(srcCopy)
        val rectF = region.bounds
        // 避免溢出
        rectF.right = min(src.width, rectF.right)
        rectF.bottom = min(src.height, rectF.bottom)
        val paint = Paint()
        var r = rectF
        paint.style = Paint.Style.FILL
        val op = if (isNormal) {
            Region.Op.INTERSECT
        } else {
            r = Rect(0, 0, width, height)
            Region.Op.DIFFERENCE
        }
        canvas.clipPath(targetPath, op)
        canvas.drawBitmap(src, 0f, 0f, paint)
        val fBitmap = Bitmap.createBitmap(srcCopy, r.left, r.top,
            r.width(), r.height())
        action.invoke(fBitmap, RectF(r))
        finish()
    }
}

