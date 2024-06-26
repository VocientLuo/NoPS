package com.xluo.core.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.xluo.core.core.PvsTimeLine
import com.xluo.core.interfaces.PvsCanvasSizeChangeCallback
import com.xluo.core.interfaces.PvsEditViewListener

/**
 * Create by AjjAndroid
 * Time:2022/2/16
 * 图片编辑手势操作控件
 */
open class PvsBaseView : View {

    var pvsTimeLine = PvsTimeLine()

    var pvsEditViewListener: PvsEditViewListener? = null

    var imageMatrix = Matrix()


    /**
     * 是否是编辑状态
     */
    var isEdit = false


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


    fun addCanvasSizeChangeListener(pvsCanvasSizeChangeCallback: PvsCanvasSizeChangeCallback) {
        pvsTimeLine.addCanvasSizeChangeListener(pvsCanvasSizeChangeCallback)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    fun changeCanvasSize(canvasWidth: Int, canvasHeight: Int) {
        pvsTimeLine.changeCanvasSize(canvasWidth, canvasHeight)
    }

    fun changeToBlackMode() {
        setBackgroundColor(Color.BLACK)
    }

    fun changeToWhiteMode() {
        setBackgroundColor(Color.WHITE)
    }

    fun setCanvasBackgroundColor(color: Int) {
        setBackgroundColor(color)
    }
}