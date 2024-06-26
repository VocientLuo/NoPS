package com.xluo.core.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.xluo.core.entity.PvsLayer
import com.xluo.core.interfaces.PvsCanvasSizeChangeCallback
import com.xluo.core.utils.CheckUtils
import com.xluo.core.utils.RectUtils
import com.xluo.core.utils.checkTouchIsClick
import com.xluo.core.utils.dp
import com.xluo.draw_core.R

/**
 * Create by AjjAndroid
 * Time:2022/2/17
 */
class DragRectView : View, PvsCanvasSizeChangeCallback {

    private var closeBitmap: Bitmap? = null
    private var copyBitmap: Bitmap? = null
    private var rotateAndScaleBitmap: Bitmap? = null

    private var replaceBitmap: Bitmap? = null
    private var cropBitmap: Bitmap? = null
    private var editTextBitmap: Bitmap? = null

    private var closeRect = RectF()
    private var editRect = RectF()
    private var copyRect = RectF()
    private var rotateScaleRect = RectF()

    /**
     * 当模式为剪同款时  仅显示替换和裁剪按钮 拖动也不允许
     */
    private var replaceRect = RectF()
    private var cropRect = RectF()
    private var editTextRect = RectF()

    /**
     * 修正后显示的区域
     */
    private var reviseRect = RectF()

    private var bolderColor = Color.parseColor("#ffffff")
    private val bolderWidth = 2.dp
    private var iconWidth = 24.dp.toInt()


    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var bolderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = bolderWidth
        color = bolderColor
        style = Paint.Style.STROKE
    }

    private var type: Int = -1

    private var downX = 0f
    private var downY = 0f

    /**
     * 用来检测点击事件
     */
    private var originClickDownX = 0f

    private var originClickDownY = 0f

    /**
     * 是否点击到了矩形范围内  这个范围内属于拖动操作
     */
    private var isTouchDrag = false
    private var isTouchDelete = false
    private var isTouchCopy = false
    private var isTouchEdit = false
    private var isTouchScale = false

    private var isTouchCutSameReplace = false
    private var isTouchCutSameCrop = false
    private var isTouchCutSameEditText = false

    /**
     * 画布的宽度
     */
    private var canvasWidth = 0

    /**
     * 画布的高度
     */
    private var canvasHeight = 0

    private var rotateAngle: Float = 0.0F

    /**
     * 初始时的宽度值
     */
    private var initWidth = 0

    private var mScaleX = 1f
    private var mScaleY = 1f


    /**
     * 手势操作回调接口
     */
    var onDragGestureListener: OnDragGestureListener? = null

    var onCutSameActionCallback: CutSameActionCallback? = null

    var pvsLayer: PvsLayer? = null

    /**
     * 初始设置选中的值，做撤销重做需要
     */
    var pvsOldLayer: PvsLayer? = null

    var pvsEditView: PvsEditView? = null

    /**
     * 默认模式是海报编辑模式
     */
    var mode = EDIT_MODE_POSTER

    /**
     * 是否橡皮擦模式
     */
    var isEraser: Boolean = false


    companion object {

        /**
         * 图片编辑模式
         */
        const val TYPE_IMAGE = 1

        /**
         * 文字输入模式
         */
        const val TYPE_TEXT = 2

        const val TYPE_SHAPE = 3

        /**
         * 剪同款时图片编辑
         */
        const val TYPE_CUT_SAME_IMAGE = 4

        /**
         * 剪同款时文字编辑
         */
        const val TYPE_CUT_SAME_TEXT = 5

        //剪同款操作模块
        const val EDIT_MODE_CUT_SAME_TEMP = 6

        //海报模块
        const val EDIT_MODE_POSTER = 7

    }


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
        defStyleRes: Int,
    ) : super(context, attributeSet, defStyleAttr, defStyleRes) {
        initView()
    }

    fun setBolderColor(bolderColor: Int) {
        this.bolderColor = bolderColor
        bolderPaint.color = bolderColor
    }

    private fun initView() {
        setBackgroundColor(Color.TRANSPARENT)
        closeBitmap = BitmapFactory.decodeResource(resources, R.mipmap.lib_image_btn_guanbi)
        copyBitmap = BitmapFactory.decodeResource(resources, R.mipmap.lib_image_btn_fuzhi)
        replaceBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_replace_01)
        cropBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_cutting_01)
        editTextBitmap = BitmapFactory.decodeResource(resources, R.mipmap.lib_image_btn_bianji)
        rotateAndScaleBitmap = BitmapFactory.decodeResource(resources, R.mipmap.lib_image_btn_scale)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (type) {
            TYPE_IMAGE, TYPE_SHAPE -> {
                drawRect(canvas, false)
            }
            TYPE_TEXT -> {
                drawRect(canvas, true)
            }
            TYPE_CUT_SAME_IMAGE -> {
                drawCutSameImageRect(canvas)
            }
            TYPE_CUT_SAME_TEXT -> {
                drawCutSameTextRect(canvas)
            }
        }
    }

    private val MIN_SCALE = 0.25f


    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    fun updateRotateAndScale(dx: Float, dy: Float) {
        val c_x: Float = reviseRect.centerX()
        val c_y: Float = reviseRect.centerY()

        val x: Float = rotateScaleRect.centerX()
        val y: Float = rotateScaleRect.centerY()

        val n_x = x + dx
        val n_y = y + dy

        val xa = x - c_x
        val ya = y - c_y

        val xb = n_x - c_x
        val yb = n_y - c_y

        val srcLen = Math.sqrt((xa * xa + ya * ya).toDouble()).toFloat()
        val curLen = Math.sqrt((xb * xb + yb * yb).toDouble()).toFloat()

        val scale = curLen / srcLen // 计算缩放比


        val newWidth: Float = reviseRect.width() * scale
        if (newWidth / initWidth < MIN_SCALE) { // 最小缩放值检测
            return
        }
        pvsLayer?.matrix?.postScale(
            scale, scale, reviseRect.centerX(),
            reviseRect.centerY()
        ) // 存入scale矩阵
        RectUtils.scaleRect(reviseRect, scale) // 缩放目标矩形

        val halfWidth = iconWidth / 2

        closeRect.offsetTo(reviseRect.left - halfWidth, reviseRect.top - halfWidth)
        copyRect.offsetTo(reviseRect.right - halfWidth, reviseRect.top - halfWidth)
        editRect.offsetTo(reviseRect.left - halfWidth, reviseRect.bottom - halfWidth)
        rotateScaleRect.offsetTo(reviseRect.right - halfWidth, reviseRect.bottom - halfWidth)

        val cos = ((xa * xb + ya * yb) / (srcLen * curLen)).toDouble()
        if (cos > 1 || cos < -1) return
        var angle = Math.toDegrees(Math.acos(cos)).toFloat()

        val calMatrix = xa * yb - xb * ya // 行列式计算 确定转动方向

        val flag = if (calMatrix > 0) 1 else -1
        angle = flag * angle

        rotateAngle += angle
        pvsLayer?.matrix?.postRotate(
            angle, reviseRect.centerX(),
            reviseRect.centerY()
        )
        RectUtils.rotateRect(
            closeRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            copyRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            editRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            rotateScaleRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        pvsLayer?.let {
            mScaleX = reviseRect.width() / it.originWidth.toFloat()
        }
        invalidate()
    }


    fun drawRect(canvas: Canvas, editIconIsShow: Boolean) {

        pvsLayer?.let {
            canvas.save()
            canvas.rotate(rotateAngle, reviseRect.centerX(), reviseRect.centerY())
            canvas.drawRect(reviseRect, bolderPaint)
            canvas.restore()
        }
        closeBitmap?.let {
            canvas.drawBitmap(it, null, closeRect, paint)
        }
        copyBitmap?.let {
            canvas.drawBitmap(it, null, copyRect, paint)
        }
        if (editIconIsShow) {
            editTextBitmap?.let {
                canvas.drawBitmap(it, null, editRect, paint)
            }
        }
        rotateAndScaleBitmap?.let {
            canvas.drawBitmap(it, null, rotateScaleRect, paint)
        }
    }


    fun drawCutSameImageRect(canvas: Canvas) {
        pvsLayer?.let {
            canvas.save()
            canvas.rotate(rotateAngle, reviseRect.centerX(), reviseRect.centerY())
            canvas.drawRect(reviseRect, bolderPaint)
            canvas.restore()
        }
        replaceBitmap?.let {
            canvas.drawBitmap(it, null, replaceRect, paint)
        }
        cropBitmap?.let {
            canvas.drawBitmap(it, null, cropRect, paint)
        }
    }

    fun drawCutSameTextRect(canvas: Canvas) {
        pvsLayer?.let {
            canvas.save()
            canvas.rotate(rotateAngle, reviseRect.centerX(), reviseRect.centerY())
            canvas.drawRect(reviseRect, bolderPaint)
            canvas.restore()
        }
        editTextBitmap?.let {
            canvas.drawBitmap(it, null, editTextRect, paint)
        }
    }


    /**
     * 计算各个操作按钮的位置
     */
    fun calculateRect() {
        val halfWidth = iconWidth / 2
        closeRect.left = reviseRect.left - halfWidth
        closeRect.right = reviseRect.left + halfWidth
        closeRect.top = reviseRect.top - halfWidth
        closeRect.bottom = reviseRect.top + halfWidth

        replaceRect.left = reviseRect.left - halfWidth
        replaceRect.right = reviseRect.left + halfWidth
        replaceRect.top = reviseRect.top - halfWidth
        replaceRect.bottom = reviseRect.top + halfWidth

        editTextRect.left = reviseRect.left - halfWidth
        editTextRect.right = reviseRect.left + halfWidth
        editTextRect.top = reviseRect.top - halfWidth
        editTextRect.bottom = reviseRect.top + halfWidth

        copyRect.left = reviseRect.right - halfWidth
        copyRect.right = reviseRect.right + halfWidth
        copyRect.top = reviseRect.top - halfWidth
        copyRect.bottom = reviseRect.top + halfWidth

        editRect.left = reviseRect.left - halfWidth
        editRect.right = reviseRect.left + halfWidth
        editRect.top = reviseRect.bottom - halfWidth
        editRect.bottom = reviseRect.bottom + halfWidth

        rotateScaleRect.left = reviseRect.right - halfWidth
        rotateScaleRect.right = reviseRect.right + halfWidth
        rotateScaleRect.top = reviseRect.bottom - halfWidth
        rotateScaleRect.bottom = reviseRect.bottom + halfWidth

        cropRect.left = reviseRect.right - halfWidth
        cropRect.right = reviseRect.right + halfWidth
        cropRect.top = reviseRect.bottom - halfWidth
        cropRect.bottom = reviseRect.bottom + halfWidth
    }

    private fun isSubLayerMode(): Boolean {
        return type == TYPE_CUT_SAME_IMAGE || type == TYPE_CUT_SAME_TEXT
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount > 1) {
            return true
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                originClickDownX = event.x
                originClickDownY = event.y
                if (isSubLayerMode()) {
                    isTouchCutSameCrop = cropRect.contains(downX, downY)
                    if (type == TYPE_CUT_SAME_TEXT) {
                        isTouchCutSameEditText = editTextRect.contains(downX, downY)
                    }
                    isTouchCutSameReplace = replaceRect.contains(downX, downY)
                } else {
                    isTouchDrag = CheckUtils.checkIsTouchTargetLayer(downX, downY, pvsLayer)
                    isTouchDelete = closeRect.contains(downX, downY)
                    isTouchCopy = copyRect.contains(downX, downY)
                    isTouchEdit = editRect.contains(downX, downY)
                    isTouchScale = rotateScaleRect.contains(downX, downY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                val moveY = event.y
                val distanceX = moveX - downX
                val distanceY = moveY - downY
                if (isTouchDelete || isTouchCopy || isTouchEdit) {

                } else {
                    if (type == TYPE_IMAGE || type == TYPE_TEXT) {
                        if (isTouchDrag) {
                            reviseRect.offset(distanceX, distanceY)
                            closeRect.offset(distanceX, distanceY)
                            copyRect.offset(distanceX, distanceY)
                            editRect.offset(distanceX, distanceY)
                            rotateScaleRect.offset(distanceX, distanceY)
                            pvsLayer?.matrix?.postTranslate(distanceX, distanceY)
                            downX = moveX
                            downY = moveY
                            invalidate()
                            onDragGestureListener?.onTranslation(pvsLayer, RectF(reviseRect))
                        } else if (isTouchScale) {
                            updateRotateAndScale(distanceX, distanceY)
                            downX = moveX
                            downY = moveY
                            onDragGestureListener?.onScaleAndRotate(
                                pvsLayer,
                                RectF(reviseRect),
                                mScaleX,
                                rotateAngle
                            )
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                val disX = event.x - originClickDownX
                val disY = event.y - originClickDownY
                if (isTouchDrag) {
                    onDragGestureListener?.onTranslationEnd(
                        pvsOldLayer,
                        pvsLayer,
                        RectF(reviseRect)
                    )
                } else if (isTouchScale) {
                    onDragGestureListener?.onScaleAndRotateEnd(
                        pvsOldLayer,
                        pvsLayer,
                        RectF(reviseRect),
                        mScaleX,
                        rotateAngle
                    )
                } else {
                    if (checkTouchIsClick(disX, disY)) {
                        if (isTouchDelete) {
                            onDragGestureListener?.onClickDelete(pvsLayer)
                            dismiss()
                        } else if (isTouchCopy) {
                            onDragGestureListener?.onClickCopy(pvsLayer)
                        } else if (isTouchEdit) {
                            onDragGestureListener?.onClickEdit(pvsLayer)
                        } else if (isTouchScale) {

                        } else if (isTouchCutSameCrop) {
                            onCutSameActionCallback?.onClickCropImage(pvsLayer)
                        } else if (isTouchCutSameEditText) {
                            onCutSameActionCallback?.onClickEditText(pvsLayer)
                        } else if (isTouchCutSameReplace) {
                            onCutSameActionCallback?.onClickReplaceImage(pvsLayer)
                        } else {
                            val containsClick = editRect.contains(event.x, event.y)
                            if (!containsClick) {
                                dismiss()
                                onDragGestureListener?.onClickCloseEditStatus()
                            }
                        }
                    }
                }
                resetTouchStatus()
            }
            MotionEvent.ACTION_CANCEL -> {
                resetTouchStatus()
            }
        }
        return true
    }

    /**
     * 设置编辑模式并显示操作的view
     * @param rectType Int
     * @param rect Rect
     */
    fun setEditTypeAndShow(rectType: Int, pvsLayer: PvsLayer) {
        if (pvsLayer.isLocked || !pvsLayer.isShow) {
            dismiss()
            return
        }
        visibility = VISIBLE
        resetTouchStatus()
        this.pvsLayer = pvsLayer
        pvsOldLayer = pvsLayer.copy()
        type = rectType
        iconWidth = if (isSubLayerMode()) {
            33.dp.toInt()
        } else {
            24.dp.toInt()
        }
        rotateAngle = pvsLayer.rotation
        mScaleX = pvsLayer.scaleX
        initWidth = pvsLayer.pos.width().toInt()
        reviseRect = RectF(pvsLayer.pos)
        calculateRect()
        RectUtils.rotateRect(
            closeRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            copyRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            editRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            rotateScaleRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            replaceRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            editTextRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        RectUtils.rotateRect(
            cropRect, this.reviseRect.centerX(),
            this.reviseRect.centerY(), rotateAngle
        )
        invalidate()
    }

    fun resetTouchStatus() {
        isTouchDrag = false
        isTouchDelete = false
        isTouchCopy = false
        isTouchEdit = false
        isTouchScale = false
        isTouchCutSameCrop = false
        isTouchCutSameEditText = false
        isTouchCutSameReplace = false
        invalidate()
    }


    fun clearAllStatus() {
        resetTouchStatus()
        pvsLayer = null
        pvsOldLayer = null
        reviseRect.setEmpty()
        type = -1
        invalidate()
    }

    data class BackupObj(val isTouchDrag: Boolean,
                         val isTouchDelete: Boolean,
                         val isTouchCopy: Boolean,
                         val isTouchEdit: Boolean,
                         val isTouchScale: Boolean,
                         val isTouchCutSameCrop: Boolean,
                         val isTouchCutSameEditText: Boolean,
                         val isTouchCutSameReplace: Boolean,
                         val pvsLayer: PvsLayer?,
                         val pvsOldLayer: PvsLayer?,
                         val reviseRect: RectF,
                         val type: Int
                         )

    var backupObj: BackupObj? = null

    fun backupData() {
        backupObj = BackupObj(
            isTouchDrag,
            isTouchDelete,
            isTouchCopy,
            isTouchEdit,
            isTouchScale,
            isTouchCutSameCrop,
            isTouchCutSameEditText,
            isTouchCutSameReplace,
            pvsLayer,
            pvsOldLayer,
            reviseRect,
            type
            )
        isTouchDrag = false
        isTouchDelete = false
        isTouchCopy = false
        isTouchEdit = false
        isTouchScale = false
        isTouchCutSameCrop = false
        isTouchCutSameEditText = false
        isTouchCutSameReplace = false
        pvsLayer = null
        pvsOldLayer = null
        reviseRect = RectF()
        type = -1
    }

    fun resumeData() {
        backupObj?.let {
            isTouchDrag = it.isTouchDrag
            isTouchDelete = it.isTouchDelete
            isTouchCopy = it.isTouchCopy
            isTouchEdit = it.isTouchEdit
            isTouchScale = it.isTouchScale
            isTouchCutSameCrop = it.isTouchCutSameCrop
            isTouchCutSameEditText = it.isTouchCutSameEditText
            isTouchCutSameReplace = it.isTouchCutSameReplace
            pvsLayer = it.pvsLayer
            pvsOldLayer = it.pvsOldLayer
            reviseRect = it.reviseRect
            type = it.type
        }
    }


    fun dismiss() {
        clearAllStatus()
        onDragGestureListener?.onClickCloseEditStatus()
        visibility = INVISIBLE
    }


    override fun onCanvasSizeChange(canvasWidth: Int, canvasHeight: Int) {
        this.canvasWidth = canvasWidth
        this.canvasHeight = canvasHeight
        invalidate()
    }


    interface OnDragGestureListener {

        fun onScaleAndRotate(
            pvsLayer: PvsLayer?,
            realRect: RectF,
            mScale: Float,
            rotateAngle: Float,
        )

        fun onTranslation(pvsLayer: PvsLayer?, realRect: RectF)


        /**
         * 手势结束时
         * @param pvsLayer PvsLayer?
         * @param realRect RectF
         * @param mScale Float
         * @param rotateAngle Float
         */
        fun onScaleAndRotateEnd(
            pvsOldLayer: PvsLayer?,
            pvsLayer: PvsLayer?,
            realRect: RectF,
            mScale: Float,
            rotateAngle: Float,
        )

        fun onTranslationEnd(pvsOldLayer: PvsLayer?, pvsLayer: PvsLayer?, realRect: RectF)

        fun onClickDelete(pvsLayer: PvsLayer?)

        fun onClickCopy(pvsLayer: PvsLayer?)

        fun onClickEdit(pvsLayer: PvsLayer?)

        /**
         * 关闭编辑状态
         */
        fun onClickCloseEditStatus()

        /**
         * 切换图层的回调
         * @param pvsLayer PvsLayer?
         */
        fun onSwitchLayer(pvsLayer: PvsLayer?)
    }


    /**
     * 剪同款相关的回调
     */
    interface CutSameActionCallback {

        fun onClickReplaceImage(pvsLayer: PvsLayer?)
        fun onClickCropImage(pvsLayer: PvsLayer?)
        fun onClickEditText(pvsLayer: PvsLayer?)

    }


    /**
     * 剪同款相关的回调
     */
    interface LayerSelectCallback {
        fun onClickEditText(pvsLayer: PvsLayer?)
    }
}