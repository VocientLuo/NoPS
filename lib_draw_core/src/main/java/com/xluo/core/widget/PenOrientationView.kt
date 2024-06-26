package com.xluo.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.xluo.core.utils.dp

/**
 * Create by AjjAndroid
 * 笔方向模拟view
 */
open class PenOrientationView : View {
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

    private val paint = Paint().apply {
        color = Color.parseColor("#cccccc")
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    private val moniPaint = Paint().apply {
        color = Color.parseColor("#cccccc")
        strokeWidth = 1f
        style = Paint.Style.FILL
    }

    private var penVertical = true
    private var bgRadius = 0f
    private var moniRadius = 8f

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

    fun updateOriental(vertical: Boolean) {
        this.penVertical = vertical
        invalidate()
    }

    private fun initData() {
        centerX = ((right - left)/2 + left)
        centerY = ((bottom - top)/2 + top)
        startX = left + padding
        startY = centerY
        endX = right - padding
        endY = bottom - padding
        bgRadius = centerX - padding
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
        canvas.drawCircle(centerX, centerY, bgRadius, paint)
        var cX1 = 0f
        var cX2 = 0f
        var cY1 = 0f
        var cY2 = 0f
        if (!penVertical) {
            cX1 = centerX
            cY1 = top+padding
            cX2 = centerX
            cY2 = bottom-padding
        } else {
            cX1 = left + padding
            cY1 = centerY
            cX2 = right - padding
            cY2 = centerY
        }
        canvas.drawCircle(cX1, cY1, moniRadius, moniPaint)
        canvas.drawCircle(cX2, cY2, moniRadius, moniPaint)
    }
}