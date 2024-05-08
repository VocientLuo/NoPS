package com.xluo.nops.model

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.lifecycle.MutableLiveData

import com.xluo.nops.bean.DRAW_DRAFT_TYPE
import com.xluo.nops.bean.DraftBean
import com.xluo.nops.bean.Layer
import com.xluo.nops.utils.CacheUtil
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xluo.core.constants.Constants
import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsImageDecodeInfo
import com.xluo.core.entity.PvsImageLayer
import com.xluo.core.entity.PvsLayer
import com.xluo.core.entity.PvsRichLayer
import com.xluo.core.entity.PvsTextLayer
import com.xluo.core.utils.DecodeUtils
import com.xluo.core.utils.RectUtils
import com.xluo.core.widget.PvsEditView
import com.xluo.lib_base.file.IFileUtils
import com.xluo.lib_base.file.IImageType

import com.xluo.lib_base.ui.BaseViewModel
import com.xluo.lib_base.utils.TimeUtils
import com.xluo.nops.db.DraftDB
import com.xluo.nops.db.entity.DraftEntity

import kotlinx.coroutines.launch
import kotlin.math.max

class MainCanvasModel : BaseViewModel() {

    var viewWidth: Int = 0
    var viewHeight: Int = 0

    val bgLayerData = MutableLiveData<PvsBackgroundLayer>()
    // 图层数据流
    var layerLiveData = MutableLiveData<PvsLayer>()
    // 预加载图层数据流
    var preLayerLiveData = MutableLiveData<PvsLayer>()
    var layerListLiveData = MutableLiveData<ArrayList<PvsLayer>>()
    var saveSuccessData = MutableLiveData<Boolean>()
    var brushPictureData = MutableLiveData<String>()

    //读写权限
    private var permissionArray = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    fun initFromDraft(draftBean: DraftBean) {
        launch {
            viewWidth = draftBean.width
            viewHeight = draftBean.height
            if (draftBean.bgType == Constants.BG_TYPE_IMAGE) {
                initBgImageLayer(
                    draftBean.width,
                    draftBean.height,
                    draftBean.bgImage!!,
                    draftBean.isShow
                )
            } else {
                initBgColorLayer(
                    draftBean.width,
                    draftBean.height,
                    draftBean.bgColor!!,
                    draftBean.isShow
                )
            }
            val layerList = arrayListOf<PvsLayer>()
            draftBean.layerList.forEach {
                layerList.add(getRichLayer(it))
            }
            layerListLiveData.postValue(layerList)
        }
    }

    fun replaceBgColor(viewWidth: Int, viewHeight: Int, color: Int) {
        val pvsBackgroundLayer = PvsBackgroundLayer()
        pvsBackgroundLayer.bgType = Constants.BG_TYPE_COLOR
        pvsBackgroundLayer.bgColor = color
        pvsBackgroundLayer.name = "背景"
        pvsBackgroundLayer.isSelected = true
        val opts = BitmapFactory.Options()
        opts.outWidth = viewWidth
        opts.outHeight = viewHeight
        val decodeInfo = PvsImageDecodeInfo()
        val simpleSize = ImageUtils.calculateInSampleSize(opts, viewWidth, viewHeight)
        decodeInfo.scaleWidth = viewWidth
        decodeInfo.scaleHeight = viewHeight
        decodeInfo.inSimpleSize = simpleSize
        decodeInfo.originWidth = viewWidth
        decodeInfo.originHeight = viewHeight
        pvsBackgroundLayer.decodeInfo = decodeInfo
        pvsBackgroundLayer.bgLayerWidth = decodeInfo.scaleWidth
        pvsBackgroundLayer.bgLayerHeight = decodeInfo.scaleHeight
        bgLayerData.postValue(pvsBackgroundLayer)
    }

    fun addBgColorLayer(viewWidth: Int, viewHeight: Int, color: Int) {
        initBgColorLayer(viewWidth, viewHeight, color)
        addEmptyLayer(null)
    }

    private fun initBgColorLayer(
        viewWidth: Int,
        viewHeight: Int,
        color: Int,
        isShow: Boolean = true
    ) {
        val pvsBackgroundLayer = PvsBackgroundLayer()
        pvsBackgroundLayer.bgType = Constants.BG_TYPE_COLOR
        pvsBackgroundLayer.bgColor = color
        pvsBackgroundLayer.name = "背景"
        val opts = BitmapFactory.Options()
        opts.outWidth = viewWidth
        opts.outHeight = viewHeight
        val decodeInfo = PvsImageDecodeInfo()
        val simpleSize = ImageUtils.calculateInSampleSize(opts, viewWidth, viewHeight)
        decodeInfo.scaleWidth = viewWidth
        decodeInfo.scaleHeight = viewHeight
        decodeInfo.inSimpleSize = simpleSize
        decodeInfo.originWidth = viewWidth
        decodeInfo.originHeight = viewHeight
        pvsBackgroundLayer.decodeInfo = decodeInfo
        pvsBackgroundLayer.bgLayerWidth = decodeInfo.scaleWidth
        pvsBackgroundLayer.bgLayerHeight = decodeInfo.scaleHeight
        pvsBackgroundLayer.isShow = isShow
        bgLayerData.postValue(pvsBackgroundLayer)
    }

    fun addBgImageLayer(viewWidth: Int, viewHeight: Int, bgPath: String) {
        initBgImageLayer(viewWidth, viewHeight, bgPath)
        addEmptyLayer(null)
    }

    private fun initBgImageLayer(
        viewWidth: Int,
        viewHeight: Int,
        bgPath: String,
        isShow: Boolean = true
    ) {
        val pvsBackgroundLayer = PvsBackgroundLayer()
        val decodeInfo = DecodeUtils.decodeBitmapByPath(bgPath, viewWidth, viewHeight)
        pvsBackgroundLayer.bgType = Constants.BG_TYPE_IMAGE
        pvsBackgroundLayer.bgLayerWidth = decodeInfo.scaleWidth
        pvsBackgroundLayer.bgLayerHeight = decodeInfo.scaleHeight
        pvsBackgroundLayer.decodeInfo = decodeInfo
        pvsBackgroundLayer.name = "背景"
        pvsBackgroundLayer.isShow = isShow
        bgLayerData.postValue(pvsBackgroundLayer)
    }

    private fun getRichLayer(layerData: Layer): PvsRichLayer {
        val bitmap = BitmapFactory.decodeFile(layerData.fileName)
        val richLayer = PvsRichLayer(layerData.width, layerData.height, bitmap)
        richLayer.name = layerData.layerName
        richLayer.isShow = layerData.isShow
        richLayer.alpha = layerData.alpha
        return richLayer
    }

    fun addEmptyLayer(name: String?) {
        val layer = PvsRichLayer(viewWidth, viewHeight)
        layer.name = name ?: "图层1"
        layerLiveData.postValue(layer)
    }

    fun addCopyLayer(copyLayer: PvsLayer) {
        layerLiveData.postValue(copyLayer)
    }

    fun exportProduct(pvsEditView: PvsEditView) {
        launch {
            pvsEditView.saveToPhoto(false)?.let {
                val success = IFileUtils.instance.saveBitmap2Gallery(it, IImageType.PNG)
                if (success) {
                    saveSuccessData.postValue(false)
                } else {
                    ToastUtils.showShort("保存失败，请重试")
                }
            }
        }
    }

    fun saveDraft(pvsEditView: PvsEditView, oldId: Long) {
        launch {
            // 草稿箱强制显示背景
            pvsEditView.saveToPhoto(true)?.let {
                val savePath = IFileUtils.instance.saveBitmap2Png(it)
                val json = GsonUtils.toJson(getDraftBean(pvsEditView))
                val oldEntity = DraftDB.instance.draftDao().query(oldId)
                if (oldEntity == null) {
                    val draftEntity = DraftEntity()
                    val currentTime = System.currentTimeMillis()
                    val draftName = "作品-${TimeUtils.getMonthAndDayTime(currentTime)}"
                    draftEntity.name = draftName
                    draftEntity.draftType = DRAW_DRAFT_TYPE
                    draftEntity.coverPath = savePath
                    draftEntity.modifyTime = System.currentTimeMillis()
                    draftEntity.jsonData = json
                    DraftDB.instance.draftDao().insert(draftEntity)
                } else {
                    oldEntity.coverPath = savePath
                    oldEntity.modifyTime = System.currentTimeMillis()
                    oldEntity.jsonData = json
                    DraftDB.instance.draftDao().update(oldEntity)
                }
            }
            saveSuccessData.postValue(true)
        }
    }

    private fun getDraftBean(pvsEditView: PvsEditView): DraftBean {
        val bgType = pvsEditView.pvsTimeLine.bgLayer!!.bgType
        val bgColor = pvsEditView.pvsTimeLine.bgLayer!!.bgColor
        val width = pvsEditView.pvsTimeLine.bgLayer!!.bgLayerWidth
        val height = pvsEditView.pvsTimeLine.bgLayer!!.bgLayerHeight
        val isShow = pvsEditView.pvsTimeLine.bgLayer!!.isShow
        var layerList = arrayListOf<Layer>()
        var bgImage = if (bgType == Constants.BG_TYPE_IMAGE) {
            val savePath = "${CacheUtil.TEMP_EXPORT_IMAGE_FOLDER}${System.currentTimeMillis()}.png"
            CacheUtil.bitmapSaveToLocal(
                pvsEditView.pvsTimeLine.bgLayer!!.decodeInfo!!.bitmap!!,
                savePath
            )
            savePath
        } else {
            null
        }
        pvsEditView.pvsTimeLine.layerList.forEach { layer ->
            layer.bitmap?.let {
                val savePath =
                    "${CacheUtil.TEMP_EXPORT_IMAGE_FOLDER}${System.currentTimeMillis()}.png"
                CacheUtil.bitmapSaveToLocal(it, savePath)
                layerList.add(
                    Layer(
                        layer.name,
                        savePath,
                        width,
                        height,
                        layer.isShow,
                        layer.alpha,
                    )
                )
            }
        }
        return DraftBean(
            bgType,
            width,
            height,
            bgImage,
            bgColor,
            isShow,
            layerList,
        )
    }

    fun addPhotoLayer(
        fileName: String,
        width: Int,
        height: Int
    ) {
        val decodeInfo = DecodeUtils.decodeBitmapByFileToScale(fileName, width/2, height/2)
        addImageLayer(decodeInfo, width, height)
    }

    /**
     * 预加载添加一个图片图层
     * @param imageUri Uri  图片的uri
     */
    private fun addImageLayer(
        decodeInfo: PvsImageDecodeInfo,
        viewWidth: Int,
        viewHeight: Int
    ) {
        if (decodeInfo.bitmap == null) {
            LogUtils.d("add image error")
            return
        }
        val pvsImageLayer = PvsImageLayer()
        pvsImageLayer.bitmap = decodeInfo.bitmap
        val rect = RectF()
        rect.left = ((viewWidth - decodeInfo.scaleWidth) / 2).toFloat()
        rect.right = rect.left + decodeInfo.scaleWidth
        rect.top = ((viewHeight - decodeInfo.scaleHeight) / 2).toFloat()
        rect.bottom = rect.top + decodeInfo.scaleHeight
        pvsImageLayer.originWidth = rect.width().toInt()
        pvsImageLayer.originHeight = rect.height().toInt()
        pvsImageLayer.pos = rect
        pvsImageLayer.matrix.postTranslate(rect.left, rect.top)
        preLayerLiveData.postValue(pvsImageLayer)
    }

    fun addBitmapLayer(bitmap: Bitmap, rectF: RectF) {
        val pvsImageLayer = PvsImageLayer()
        pvsImageLayer.bitmap = bitmap
        pvsImageLayer.originWidth = rectF.width().toInt()
        pvsImageLayer.originHeight = rectF.height().toInt()
        pvsImageLayer.pos = rectF
        pvsImageLayer.matrix.postTranslate(rectF.left, rectF.top)
        preLayerLiveData.postValue(pvsImageLayer)
    }

    fun addTextLayer(
        content: String,
        viewWidth: Int,
        viewHeight: Int
    ) {
        val pvsTextLayer = PvsTextLayer()
        pvsTextLayer.text = content
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = pvsTextLayer.color
            textSize = pvsTextLayer.textSize
            textAlign = pvsTextLayer.align
            letterSpacing = Constants.TEXT_LETTER_SPACE
        }
        val maxDrawWidth = (viewWidth - (Constants.TEXT_DEFAULT_BOLDER_MARGIN * 2)).toInt()
        val textRect = Rect()
        paint.getTextBounds("你", 0, 1, textRect)
        val staticLayout =
            StaticLayout(
                pvsTextLayer.text,
                paint,
                maxDrawWidth,
                Layout.Alignment.ALIGN_NORMAL,
                Constants.TEXT_LINE_SPACE,
                0f,
                false
            )
        var maxWidth = 0
        for (index in 0 until staticLayout.lineCount) {
            LogUtils.d("当前第${index + 1}行的宽度：" + staticLayout.getLineWidth(index))
            maxWidth = max(maxWidth, staticLayout.getLineWidth(index).toInt())
        }

        val textWidth = maxWidth
        val textHeight = staticLayout.height
        val rect = RectF()
        rect.left = ((viewWidth - textWidth) / 2).toFloat()
        rect.right = rect.left + textWidth
        rect.top = ((viewHeight - textHeight) / 2).toFloat()
        rect.bottom = rect.top + textHeight

        pvsTextLayer.originWidth = textWidth
        pvsTextLayer.originHeight = textHeight

        pvsTextLayer.lineMaxWidth = maxWidth
        pvsTextLayer.maxHeight = textHeight
        pvsTextLayer.pos = rect
        pvsTextLayer.matrix.postTranslate(rect.left, rect.top)
        pvsTextLayer.staticLayout = staticLayout
        preLayerLiveData.postValue(pvsTextLayer)
    }

    fun updateTextLayer(pvsTextLayer: PvsTextLayer, canvasWidth: Int) {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = pvsTextLayer.color
            textSize = pvsTextLayer.textSize
            textAlign = pvsTextLayer.align
            typeface = pvsTextLayer.typeFace
            letterSpacing = pvsTextLayer.horizontalExtra
        }

        val maxDrawWidth = (canvasWidth - (Constants.TEXT_DEFAULT_BOLDER_MARGIN * 2)).toInt()
        val staticLayout = StaticLayout(pvsTextLayer.text, paint, maxDrawWidth, Layout.Alignment.ALIGN_NORMAL, pvsTextLayer.verticalExtra, 0f, false)

        var maxWidth = 0
        for (index in 0 until staticLayout.lineCount) {
            maxWidth = max(maxWidth, staticLayout.getLineMax(index).toInt())
        }
        val oldCenterX = pvsTextLayer.pos.centerX()
        val oldCenterY = pvsTextLayer.pos.centerY()
        val oldLeft = pvsTextLayer.pos.left
        val oldTop = pvsTextLayer.pos.top

        val textWidth = maxWidth
        val textHeight = staticLayout.height
        // 以原图层中心不变重绘新图层
        val newLeft = oldCenterX - textWidth / 2
        val newTop = oldCenterY - textHeight / 2

        val rect = RectF()

        rect.left = newLeft
        rect.right = rect.left + textWidth.toFloat()
        rect.top = newTop
        rect.bottom = rect.top + textHeight.toFloat()


        if (pvsTextLayer.rotation == 0f) {
            rect.offsetTo(oldLeft, oldTop)
        }

        pvsTextLayer.matrix.reset()
        pvsTextLayer.matrix.postRotate(pvsTextLayer.rotation, rect.centerX(), rect.centerY())
        pvsTextLayer.matrix.postScale(pvsTextLayer.scaleX, pvsTextLayer.scaleY, rect.centerX(), rect.centerY())
        pvsTextLayer.matrix.preTranslate(rect.left, rect.top)
        val targetRect = RectF(rect)
        RectUtils.scaleRect(targetRect, pvsTextLayer.scaleX)

        LogUtils.e("当前图层旋转额角度：" + pvsTextLayer.rotation)
        pvsTextLayer.pos = targetRect
        pvsTextLayer.lineMaxWidth = maxWidth
        pvsTextLayer.maxHeight = staticLayout.height
        pvsTextLayer.staticLayout = staticLayout
    }
}