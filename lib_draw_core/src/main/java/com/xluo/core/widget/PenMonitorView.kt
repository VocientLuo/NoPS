package com.xluo.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.xluo.pen.high.HiPenPixel
import com.blankj.utilcode.util.LogUtils
import com.xluo.core.constants.Constants
import com.xluo.core.utils.dp
import com.xluo.pen.core.BasePen


/**
 * Create by AjjAndroid
 * Time:2022/2/16
 * 笔模拟view
 */
open class PenMonitorView : ImageView {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView()
    }

    private var bgLightColor = Color.parseColor("#eeeeee")
    private var bgDarkColor = Color.parseColor("#cccccc")
    private var penColor = Color.parseColor("#999999")
    private var left = 0f
    private var right = 0f
    private var top = 0f
    private var bottom = 0f
    private val padding = 10.dp

    private var centerX = 0f
    private var centerY = 0f
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var leftBottomX = 0f
    private var rightTopX = 0f

    private var rendBg = false

    private var pen: BasePen? = null
    private val offsetX = 40
    private val offsetY = 5
    private val minPress = 0.1f
    private val maxPress = 1.0f
    private var bgSquareLength = 10.dp
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val eventList = arrayListOf<MotionEvent>()
    // 是否需要改变笔的属性
    private var isAgent = false

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attributeSet, defStyleAttr, defStyleRes) {
        initView()
    }

    open fun initView() {

    }

    fun updatePen(pen: BasePen) {
        rendBg = true
        this.pen = pen
        if (pen is HiPenPixel) {
            pen.canvasWidth = right.toInt()
            pen.canvasHeight = bottom.toInt()
        }
        if (eventList.isEmpty()) {
            initEvent()
        }
        render()
    }

    fun bindPen(pen: BasePen) {
        this.pen = pen
        isAgent = true
        if (pen is HiPenPixel) {
            pen.canvasWidth = right.toInt()
            pen.canvasHeight = bottom.toInt()
        }
        if (eventList.isEmpty()) {
            initEvent()
        }
        render()
    }

    private fun render() {
        if (eventList.size < 3) {
            LogUtils.d("event size too less.")
            return
        }
        pen?.let {
            val bakColor = it.color
            val bakSize = it.size
            if (isAgent) {
                it.color = penColor
                it.size = Constants.DEFAULT_PEN_SIZE
            }
            if (it is HiPenPixel) {
                it.canvasWidth = right.toInt()
                it.canvasHeight = bottom.toInt()
                LogUtils.d("isAgent, canvasWidth: ${it.canvasWidth}, ${it.canvasHeight}")
            }
            LogUtils.d("isAgent, ${isAgent}， name: ${it.name}， size: ${it.size}, eventList: ${eventList.size}")
            eventList.forEach {e ->
                it.onTouchEvent(e)
            }
            invalidate()
            if (isAgent) {
                it.color = bakColor
                it.size = bakSize
            }
        }
    }

    private fun initEvent() {
        eventList.clear()
        var downTime = System.nanoTime()
        //刷新轨迹
        val startEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, startX, startY,
            minPress, minPress, 0, 0f, 0f, 0, 0)
        eventList.add(startEvent)
        var nextX = startX + offsetX
        var nextY = startY + offsetY
        val times = 5
        var pressure = minPress
        val pOffset = (maxPress-pressure)/times
        while (nextX < endX) {
            val event = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, nextX, nextY,
                pressure, pressure, 0, 0f, 0f, 0, 0)
            eventList.add(event)
            nextX += offsetX
            if (nextX < leftBottomX) {
                nextY += offsetY
                if (pressure < maxPress) {
                    pressure += pOffset
                }
                continue
            }
            if (nextX > leftBottomX && nextX < centerX) {
                nextY -= offsetY
                continue
            }
            if (nextX > centerX && nextX < rightTopX) {
                nextY  -= offsetY
                continue
            }
            if (nextX > rightTopX && nextX < endX) {
                if (nextY < endY) {
                    nextY += offsetY
                }
                if (nextY > endY) {
                    nextY = endY
                }
                if (pressure > minPress) {
                    pressure -= pOffset
                } else {
                    break
                }
            }
        }
        val endEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, endX, endY,
            minPress, minPress, 0, 0f, 0f, 0, 0)
        eventList.add(endEvent)
    }

    private fun initData() {
        centerX = ((right - left)/2 + left)
        centerY = ((bottom - top)/2 + top)
        startX = left + padding
        startY = centerY
        endX = right - padding
        endY = bottom - padding
        leftBottomX = (centerX-startX)*2/3 + startX
        rightTopX = (endX - centerX)/3 + centerX
        initEvent()
        render()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        left = 0f
        right = measuredWidth.toFloat()
        top = 0f
        bottom = measuredHeight.toFloat()
        initData()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rendBg) {
            renderBg(canvas)
        }
        //画笔渲染要在离屏缓存绘制，否则会有笔触抖动的问题
        val count = canvas.saveLayer(left, top, right, bottom, null)
        pen?.let {
            it.onDraw(canvas)
        }
        canvas.restoreToCount(count)
    }

    private fun renderBg(canvas: Canvas) {
        canvas.drawColor(bgLightColor)
        var countWidth = (right/bgSquareLength).toInt()
        var countHeight = (bottom/bgSquareLength).toInt()
        val modeWidth = right%bgSquareLength
        if (modeWidth > 0) {
            countWidth += 1
        }
        val modeHeight = right%bgSquareLength
        if (modeHeight > 0) {
            countHeight += 1
        }
        for (i in 0 until countWidth) {
            if (i%2==0) {
                bgPaint.color = bgDarkColor
            } else {
                bgPaint.color = bgLightColor
            }
            for (j in 0 until  countHeight) {
                val l = i*bgSquareLength
                var r = (i+1)*bgSquareLength
                val t = j*bgSquareLength
                var b = (j+1)*bgSquareLength
                if (r > right) {
                    r = right
                }
                if (b > bottom) {
                    b = bottom
                }
                val rectF = RectF(l, t, r, b)
                canvas.drawRect(rectF, bgPaint)
                if (bgPaint.color == bgLightColor) {
                    bgPaint.color = bgDarkColor
                } else {
                    bgPaint.color = bgLightColor
                }
            }
        }
    }
}