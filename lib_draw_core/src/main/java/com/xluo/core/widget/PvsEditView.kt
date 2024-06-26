package com.xluo.core.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsLayer
import com.xluo.core.entity.PvsRichLayer
import com.xluo.core.utils.PvsDistortUtils
import com.xluo.pen.PenErase
import com.xluo.pen.core.BasePen
import com.xluo.pen.shape.PenShape

/**
 * Create by AjjAndroid
 * Time:2022/2/16
 * 图片编辑控件，
 */
class PvsEditView : PvsTouchView {
    enum class EditMode {
        EDITOR,
        DISTORT,// 液化
    }

    var editMode = EditMode.EDITOR

    /**
     * 当前画笔
     */
    var currentPen: BasePen? = null

    /**
     * 编辑前的图层
     */
    var lastRichLayer: PvsLayer? = null

    private var actionLayerUpdate: ((old: PvsLayer, current: PvsLayer) -> Unit)? = null

    fun onLayerUpdate(action: ((old: PvsLayer, current: PvsLayer) -> Unit)) {
        actionLayerUpdate = action
    }

    var drawTimesChanged: (() -> Unit)? = null

    /**
     * 是否处于模拟绘制，只有放开触摸时才绘制到真实bitmap上，否则绘制到模拟canvas上
     */
    var isSimulation: Boolean = false

    var onCutBmpGenerated: ((Bitmap, RectF) -> Unit)? = null

    var pvsDistortUtils = PvsDistortUtils {
        invalidate()
    }


    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.setMatrix(imageMatrix)
        canvas.translate(offsetX, offsetY)
        pvsTimeLine.drawLayer(canvas)
        // 这里是模拟绘画，没有画到真实的图层上，抬起时才画到真实图层上
        // 橡皮擦不需要模拟，手势按下和抬起时也不需要模拟，zoom缩放结束不能模拟，把之前误模拟的清理
        if (isSimulation && currentPen !is PenErase) {
            currentPen?.onDraw(canvas)
        }
    }

    fun setDistortType(type: PvsDistortUtils.DistortType) {
        pvsDistortUtils.distortType = type
    }

    fun setMeshStep(index: Int) {
        if (editMode == EditMode.DISTORT) {
            pvsDistortUtils.showMeshIndex(index)
        }
    }

    fun startDistort() {
        getLastShowLayer()?.let {
            editMode = EditMode.DISTORT
            pvsDistortUtils.start(it)
        }
    }

    fun stopDistort() {
        pvsDistortUtils.stopDistort()
    }

    fun resetDistort() {
        lastRichLayer = getLastShowLayer()?.copy()
        pvsDistortUtils.resetDistort()
        getLastShowLayer()?.let {
            addOperation(getLastShowLayer()!!)
        }
    }

    fun cleanDistort() {
        lastRichLayer = getLastShowLayer()?.copy()
        pvsDistortUtils.resetDistort()
        pvsDistortUtils.release()
        getLastShowLayer()?.let {
            addOperation(getLastShowLayer()!!)
        }
    }

    fun getLastShowLayer(): PvsRichLayer? {
        if (pvsTimeLine.layerList.size <= 0) {
            return null
        }
        var index = pvsTimeLine.layerList.size - 1
        while (index >= 0) {
            if (pvsTimeLine.layerList[index].isShow && pvsTimeLine.layerList[index].isSelected) {
                return pvsTimeLine.layerList[index] as PvsRichLayer
            }
            index--
        }
        return null
    }

    fun onUpdate() {
        invalidate()
    }

    fun addSubLayer(pvsLayer: PvsLayer) {
        val layer = getLastShowLayer() ?: return
        val old = layer.copy()
        layer.subLayer.add(pvsLayer)
        onUpdate()
        // 加入操作栈
        actionLayerUpdate?.invoke(old, layer)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 这里只处理单指事件，双指事件在ZoomView中处理。
        // 如果当前没有选中显示图层，不做处理
        val pvsLayer = getLastShowLayer() ?: return super.onTouchEvent(event)
        // 液化特殊处理
        if (editMode == EditMode.DISTORT) {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                lastRichLayer = pvsLayer.copy()
            }
            pvsDistortUtils.onTouchEvent(event)
            if (event.actionMasked == MotionEvent.ACTION_UP) {
                addOperation(pvsLayer)
            }
            return true
        }
        pvsLayer.mCurrentPen = currentPen
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            lastRichLayer = pvsLayer.copy()
            // 开始模拟
            isSimulation = true
        }
        // 柳叶笔不需要模拟，直接绘制
        if (event.actionMasked == MotionEvent.ACTION_UP || currentPen !is PenShape) {
            // 停止模拟
            isSimulation = false
        }
        // 图层绘制
        val isTouch = pvsLayer.onTouchEvent(event)
        invalidate()
        if (event.actionMasked == MotionEvent.ACTION_UP && isTouch) {
            // 绘制次数+1
            drawTimesChanged?.invoke()
            addOperation(pvsLayer)
        }
        return if (isTouch) {
            isTouch
        } else {
            super.onTouchEvent(event)
        }
    }

    private fun addOperation(layer: PvsRichLayer) {
        post {
            // 加入撤回栈
            lastRichLayer?.let {
                actionLayerUpdate?.invoke(it, layer)
            }
        }
    }

    /**
     * 区域复制
     */
    fun genCopyLayerBmp(cutView: PvsCutView) {
        getLastShowLayer()?.let {
            it.bitmap?.let {bmp ->
                cutView.genAreaBitmap(bmp) {result, rect ->
                    onCutBmpGenerated?.invoke(result, rect)
                }
            }
        }
    }

    /**
     * 区域剪切
     */
    fun genCutLayerBmp(cutView: PvsCutView) {
        getLastShowLayer()?.let {
            it.bitmap?.let {bmp ->
                it.bitmap?.let {bmp ->
                    cutView.genAreaBitmap(bmp) {result, rect ->
                        it.cutPath(cutView.targetPath, cutView.isNormal)
                        onUpdate()
                        onCutBmpGenerated?.invoke(result, rect)
                    }
                }
            }
        }
    }

    /**
     * 初始化设置，必须的选择背景 要么图片 要么创建一个指定尺寸的画布
     * @param pvsBackgroundLayer PvsBackgroundLayer
     */
    fun init(pvsBackgroundLayer: PvsBackgroundLayer) {
        pvsTimeLine.bgLayer = pvsBackgroundLayer
        invalidate()
    }

    fun saveToPhoto(showBg: Boolean): Bitmap? {
        return pvsTimeLine.saveToPhoto(width, height, showBg)
    }
}

