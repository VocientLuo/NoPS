package com.xluo.core.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.MotionEvent
import com.xluo.core.entity.LassoData
import com.xluo.core.utils.FilterRegionUtils
import com.xluo.pen.bean.ControllerPoint
import com.xluo.pen.core.Bezier
import kotlin.math.max
import kotlin.math.min


/**
 * Create by xluo
 * Time:2023/7/24
 * 柳叶笔
 */
class PvsLeafView : PvsTouchView {
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

    var rectF = RectF()

    // 画笔大小，选区工具为画笔时生效
    var penSize = 50f

    // 非选中区半透明黑色
    private val coverColor = Color.parseColor("#5f000000")

    private val dashPaint = Paint()

    private val lassoInfoList = arrayListOf<LassoData>()

    private var needInitRectF = true

    private var lastX = 0f
    private var lastY = 0f

    var isCutting = false

    var mBezier = Bezier()
    var mLastPoint = ControllerPoint(0f, 0f)

    var targetPath = Path()

    var dashSize = 3f

    var isPointUp = false

    private var pvsEditView:PvsEditView? = null

    private var filterRegionUtils = FilterRegionUtils()

    private fun init() {
        dashPaint.pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 20f)
        dashPaint.style = Paint.Style.STROKE
        dashPaint.strokeCap = Paint.Cap.ROUND
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
        rectF.setEmpty()
        lassoInfoList.clear()
        visibility = INVISIBLE
        isCutting = false
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        collectLasso(event)

        freshRectF(event.x, event.y)
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            isPointUp = true
            finalRectF()
        }
        invalidate()
        return true
    }

    private fun addPath(path: Path) {
        val op = if (fillType == FillType.ADD) {
            Path.Op.UNION
        } else {
            Path.Op.DIFFERENCE
        }
        targetPath.op(path, op)
    }

    /**
     * 套索
     */
    private fun collectLasso(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lassoInfoList.last().path.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                lassoInfoList.last().path.lineTo(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                val lassoData = lassoInfoList.last()
                lassoData.path.lineTo(event.x, event.y)
                lassoData.path.close()
            }
        }
        addPath(lassoInfoList.last().path)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        // 绘制选区
        // drawSelect(canvas)
        canvas.drawPath(targetPath, dashPaint)
    }

    private fun freshRectF(x: Float, y: Float) {
        if (!rectF.contains(x, y)) {
            rectF.left = min(rectF.left, x)
            rectF.top = min(rectF.top, y)
            rectF.right = max(rectF.right, x)
            rectF.bottom = max(rectF.bottom, y)
        }
    }

    private fun finalRectF() {
        rectF.left -= penSize/2
        rectF.right += penSize/2
        rectF.top -= penSize/2
        rectF.bottom += penSize/2
    }

    fun genAreaBitmap(src: Bitmap, action: ((Bitmap, RectF) -> Unit)){
        // 根据裁剪区域生成bitmap
        val srcCopy = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(srcCopy)
        // 避免溢出
        rectF.right = min(src.width.toFloat(), rectF.right)
        rectF.bottom = min(src.height.toFloat(), rectF.bottom)
        val paint = Paint()
        var r = rectF
        paint.style = Paint.Style.FILL
        canvas.clipPath(targetPath, Region.Op.INTERSECT)
        canvas.drawBitmap(src, 0f, 0f, paint)
        val fBitmap = Bitmap.createBitmap(srcCopy, r.left.toInt(), r.top.toInt(),
            r.width().toInt(), r.height().toInt())
        action.invoke(fBitmap, RectF(r))
        finish()
    }
}

