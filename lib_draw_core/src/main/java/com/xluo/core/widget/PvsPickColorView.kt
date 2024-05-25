package com.xluo.core.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import com.xluo.draw_core.R

/**
 * Create by xluo
 * Time:2023/7/24
 * 颜色吸取控件
 */
class PvsPickColorView : PvsTouchView {

    lateinit var targetBitmap: Bitmap

    // 吸色点
    var centerX = 0f
    var centerY = 0f

    val largeRadius = 250f
    val smallRadius = 150f

    var pickColor = Color.TRANSPARENT
    val pickPaint = Paint()
    var colorRadius = 0f

    val borderColor = Color.BLACK
    val borderPaint = Paint()
    val borderSize = 10f

    var onColorPicked: ((Int) -> Unit)? = null

    lateinit var centerIcon: Bitmap

    constructor(context: Context) : super(context) {
        initRes()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initRes()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initRes()
    }

    private fun initRes() {
        val size = largeRadius - smallRadius - borderSize
        pickPaint.strokeWidth = size
        colorRadius = size/2 + smallRadius + borderSize/2
        pickPaint.color = pickColor
        pickPaint.style = Paint.Style.STROKE

        borderPaint.strokeWidth = borderSize
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        centerIcon = BitmapFactory.decodeResource(resources, R.mipmap.icon_xipan)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw border
        canvas.drawCircle(centerX, centerY, smallRadius, borderPaint)
        canvas.drawCircle(centerX, centerY, largeRadius, borderPaint)
        // draw icon
        canvas.drawBitmap(centerIcon, (centerX - centerIcon.width/2), (centerY - centerIcon.height/2), Paint())
        // draw color
        pickPaint.color = pickColor
        canvas.drawCircle(centerX, centerY, colorRadius, pickPaint)
    }

    fun startPick(editView: PvsEditView) {
        targetBitmap = editView.saveToPhoto(true)!!
        centerX = editView.width/2f
        centerY = editView.height/2f
        visibility = VISIBLE
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        centerX = event.x
        centerY = event.y
        pickColor = targetBitmap.getPixel(centerX.toInt(), centerY.toInt())
        invalidate()
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            onColorPicked?.invoke(pickColor)
            visibility = GONE
        }
        return true
    }

}

