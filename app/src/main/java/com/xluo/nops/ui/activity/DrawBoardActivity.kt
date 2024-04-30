package com.xluo.nops.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.ajj.module_canvas_lxz.utils.DebounceUtils
import com.xluo.nops.utils.PermissUtils

import com.xluo.nops.copyFileToCacheDir

import com.xluo.nops.dialog.LayerRenameDialog
import com.xluo.nops.model.MainCanvasModel

import com.xluo.nops.utils.ScreenUtils
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.Crop.REQUEST_CROP
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.xluo.core.constants.Constants
import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsLayer
import com.xluo.core.entity.PvsRichLayer
import com.xluo.core.entity.PvsTextLayer
import com.xluo.core.utils.RectUtils
import com.xluo.core.utils.dp
import com.xluo.core.widget.DragRectView
import com.xluo.core.widget.PvsEditView
import com.xluo.core.widget.ZoomView
import com.xluo.lib_base.file.IFileType
import com.xluo.lib_base.file.IFileUtils
import com.xluo.lib_base.ui.BaseViewModelActivity
import com.xluo.lib_base.utils.MmkvUtils
import com.xluo.lib_base.utils.PermissionUtil
import com.xluo.lib_base.view.CommonTipsDialog
import com.xluo.nops.R
import com.xluo.nops.bean.CanvasBean

import com.xluo.nops.bean.DraftBean
import com.xluo.nops.bean.MenuState
import com.xluo.nops.bean.bottomMenuList
import com.xluo.nops.bean.distortInfo
import com.xluo.nops.bean.fillInfo
import com.xluo.nops.bean.lassoInfo
import com.xluo.nops.bean.leafInfo
import com.xluo.nops.bean.menuColorIcon
import com.xluo.nops.bean.menuEraseIcon
import com.xluo.nops.bean.menuPaintIcon
import com.xluo.nops.bean.setMenuState
import com.xluo.nops.bean.updateDrawableTop
import com.xluo.nops.bean.updateImageDrawable
import com.xluo.nops.databinding.ActivityCanvasMainBinding
import com.xluo.nops.databinding.MenuBarCutBinding
import com.xluo.nops.databinding.MenuBarDistortBinding
import com.xluo.nops.databinding.MenuBarFillBinding
import com.xluo.nops.databinding.PopOperationBinding
import com.xluo.nops.db.DraftDB
import com.xluo.nops.hide
import com.xluo.nops.operation.OperaLayerInfo
import com.xluo.nops.operation.OperationListener
import com.xluo.nops.operation.OperationType
import com.xluo.nops.operation.OperationUtils
import com.xluo.nops.operation.TimeLineData
import com.xluo.nops.pop.PopColorSelector
import com.xluo.nops.pop.PopDistortSelector
import com.xluo.nops.pop.PopFillSelector
import com.xluo.nops.pop.PopLassoSelector
import com.xluo.nops.pop.PopLayerList
import com.xluo.nops.pop.PopMeshStepSet
import com.xluo.nops.pop.PopPaintSelector
import com.xluo.nops.pop.PopSettingMenu
import com.xluo.nops.pop.PopShapeSelector
import com.xluo.nops.pop.PopTextToolsBar
import com.xluo.nops.pop.PopToolsSelector
import com.xluo.nops.show
import com.xluo.nops.utils.GlideEngine
import com.xluo.pen.PenLeaf
import com.xluo.pen.PenManager
import com.xluo.pen.bean.PaintBean
import com.xluo.pen.bean.PenType
import com.xluo.pen.bean.getLeafPen
import com.xluo.pen.bean.penPencil
import com.xluo.pen.core.BasePen
import com.xluo.pen.pic.PicPen
import java.io.File
import kotlin.math.min
class DrawBoardActivity : BaseViewModelActivity<MainCanvasModel, ActivityCanvasMainBinding>() {

    companion object {
        const val EXTRA_IS_FROM_DRAFT: String = "is_from_draft"
        const val EXTRA_DRAFT_ID: String = "draft_id"

        fun startDraw(context: Context, bean: CanvasBean) {
            val intent = Intent(context, DrawBoardActivity::class.java)
            intent.putExtra("canvas_data", bean)
            context.startActivity(intent)
        }
    }

    private lateinit var canvasData: CanvasBean

    private var mCanvasWidth: Int = 0
    private var mCanvasHeight: Int = 0

    private lateinit var popColorSelector: PopColorSelector
    private lateinit var popBgColorSelector: PopColorSelector
    private lateinit var popLayerList: PopLayerList
    private lateinit var popTextToolsBar: PopTextToolsBar
    private lateinit var popPaintSelector: PopPaintSelector
    private lateinit var popSettingMenu: PopSettingMenu
    private lateinit var popShapeSelector: PopShapeSelector
    private lateinit var popToolsSelector: PopToolsSelector
    private lateinit var popLassoSelector: PopLassoSelector
    private lateinit var popFillSelector: PopFillSelector
    private lateinit var popDistortSelector: PopDistortSelector
    private lateinit var popMeshStepSet: PopMeshStepSet

    private var mCurrentPaint: PaintBean = penPencil
    private lateinit var mPen: BasePen

    private lateinit var cutBinding: MenuBarCutBinding
    private lateinit var distortBinding: MenuBarDistortBinding
    private lateinit var fillBinding: MenuBarFillBinding

    private var draftId: Long = 0
    private var isFromDraft: Boolean = false

    private var maxLayerCount: Int = 100

    private var isCreateNew: Boolean = false

    private var shouldEnsure: Boolean = false

    /**
     * 本次copy操作的次数
     */
    private var copyTimes = 1

    private val copyDeviationDistance = 20.dp

    private var isEditTextLayer = false

    private var drawTimes = 0

    // 图片选取相关
    private val historyPenList = arrayListOf<PaintBean>()
    private val historyPenMax = 6
    private val historyPenKey = "key_history_pen"

    private val penSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // popPaintSelector.freshMonitor(mPen)
    }

    private fun newCacheFile(): File {
        return IFileUtils.instance.createFile(IFileType.PNG)
    }

    private val pickPictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val cacheFile = newCacheFile()
            activityResult.data?.also {
                copyFileToCacheDir(cacheFile, it.data!!)
            }
            if (!cacheFile.exists()) {
                LogUtils.e("pick file not exists")
                return@registerForActivityResult
            }
            if (!cacheFile.canRead()) {
                LogUtils.e("pick file failed")
                return@registerForActivityResult
            }
            val distinctUri = Uri.fromFile(newCacheFile())
//            UCrop.of(Uri.fromFile(cacheFile), distinctUri)
//                .withAspectRatio(1f, 1f)
//                .withMaxResultSize(100, 100)
//                //.start(this@DrawBoardActivity)
//                .start(this@DrawBoardActivity, REQUEST_CROP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                data?.also {
//                    val imageUri: Uri = UCrop.getOutput(it) ?: return
//                    val file = newCacheFile()
//                    copyFileToCacheDir(file, imageUri)
//                    addCustomPen(file.absolutePath)
                }
            } else {
                LogUtils.e("handleCropError: ")
            }

        }
    }

    private fun addCustomPen(path: String) {
        mCurrentPaint.id = Constants.PEN_ID_CUSTOM
        mCurrentPaint.path = path
        onPaintChanged()
        val count = historyPenList.filter {
            it.penType == PenType.CUSTOM
        }.size
        val bean = PaintBean(Constants.PEN_ID_CUSTOM, PenType.CUSTOM, "自定义画笔$count", path, 0, 1)
        updateHistoryPen(bean)
    }

    private fun updateHistoryPen(bean: PaintBean) {
        // 检测是否重复
        val hasBean = historyPenList.find {
            it.id == bean.id && it.path == bean.path
        }
        if (hasBean != null) {
            // 先删掉栈中元素，再添加到第一个
            historyPenList.remove(hasBean)
            freshHistory(bean)
            return
        }
        // 超过6个, 先删掉最后一个
        if (historyPenList.size >= historyPenMax) {
            val removePen = historyPenList.removeLast()
            deleteHistoryPen(removePen.path)
        }
        freshHistory(bean)
    }

    private fun freshHistory(bean: PaintBean) {
        historyPenList.add(0, bean)
        popPaintSelector.freshHistoryPen(historyPenList)
    }

    private fun deleteHistoryPen(path: String) {
        if (TextUtils.isEmpty(path)) {
            return
        }
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 图层操作监听
     */
    private val operationListener = object : OperationListener {
        override fun onUpdateBackground(pvsBackgroundLayer: PvsBackgroundLayer) {

        }

        override fun onDataChange() {
            val pvsLayer = TimeLineData.pvsTimeLine?.layerList?.filter {
                return@filter it.isSelected
            }
            if (pvsLayer.isNullOrEmpty() && !TimeLineData.pvsTimeLine?.bgLayer!!.isSelected) {
                var size = TimeLineData.pvsTimeLine!!.layerList!!.size
                while (size > 0) {
                    if (TimeLineData.pvsTimeLine!!.layerList!![size - 1].isShow) {
                        TimeLineData.pvsTimeLine!!.layerList!![size - 1].isSelected = true
                        break
                    }
                    size--
                }
            }
            if (TimeLineData.pvsTimeLine!!.layerList!!.size == 0) {
                model.addEmptyLayer(null)
            }
            binding.pvsEditView.onUpdate()
            onLayerListUpdate()
            shouldEnsure = true
        }
    }

    override fun createViewModel(): MainCanvasModel {
        return MainCanvasModel()
    }

    override fun createViewBinding(): ActivityCanvasMainBinding {
        return ActivityCanvasMainBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        PenManager.init(this.applicationContext)
        binding.pvsEditView.addCanvasSizeChangeListener(binding.dragRectView)
        binding.dragRectView.pvsEditView = binding.pvsEditView
        binding.dragRectView.mode = DragRectView.EDIT_MODE_POSTER
        binding.dragRectView.setBolderColor(Color.parseColor("#FF0000"))
        // 返回
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.ivMenu.setOnClickListener {
            popSettingMenu.showAsDropDown(it, Gravity.RIGHT, 0, 0)
        }
        binding.imageHuabi.setOnClickListener {
            onPaintChanged()
            // 画笔选择
            showBottomMenu(menuPaintIcon)
            popPaintSelector.showAsDropDown(it, Gravity.RIGHT, 0, 0)
            popPaintSelector.updateCurrentPen(mCurrentPaint)
        }
        binding.imageColor.setOnClickListener {
            // 颜色选择器
            showBottomMenu(menuColorIcon)
            popColorSelector.showAsDropDown(it, Gravity.RIGHT, 0, 0)
        }
        binding.imageXp.setOnClickListener {
            // 橡皮擦
            showBottomMenu(menuEraseIcon)
            mCurrentPaint.id = Constants.PEN_ID_ERASE
            onPaintChanged()
        }
        binding.imageLayer.setOnClickListener {
            // 图层展示，先更新图层数据，确保数据对齐
            onLayerListUpdate()
            popLayerList.showAsDropDown(it)
            onPaintChanged()
        }
        // 缩放监听
        binding.fmZoomView.setZoomLayoutGestureListener(object: ZoomView.ZoomLayoutGestureListener {
            override fun onZoomChanged(scale: Int, rotation: Int) {
                val text = "缩放 $scale%, 旋转 $rotation"
                showCanvasInfo(text, true)
            }

            override fun onZooming() {
                if (binding.pvsEditView.isSimulation) {
                    binding.pvsEditView.isSimulation = false
                    binding.pvsEditView.onUpdate()
                }
                if (binding.pvsEditView.editMode == PvsEditView.EditMode.DISTORT) {
                    binding.pvsEditView.stopDistort()
                    //onPaintChanged()
                }
            }
        })

        // 修改背景
        binding.pvsEditView.setOnClickListener {
            if (binding.pvsEditView.pvsTimeLine.bgLayer!!.isSelected && binding.pvsEditView.pvsTimeLine.bgLayer!!.isShow) {
                updateBgColor()
            } else {
                ToastUtils.showShort("隐藏图层无法绘画")
            }
        }

        binding.pvsEditView.onLayerUpdate { old, current ->
            shouldEnsure = true
            OperationUtils.updateRichLayer(old as PvsRichLayer, current as PvsRichLayer)
        }

        binding.ivOkText.setOnClickListener {
            val text = binding.etTextAdd.text.toString()
            if (TextUtils.isEmpty(text)) {
                ToastUtils.showShort("请输入内容")
                return@setOnClickListener
            }
            if (isEditTextLayer) {
                if (binding.pvsEditView.pvsTimeLine.preAddLayer ==  null) {
                    return@setOnClickListener
                }
                val pvsLayer = binding.pvsEditView.pvsTimeLine.preAddLayer as PvsTextLayer
                pvsLayer.text = text
                model.updateTextLayer(pvsLayer, mCanvasWidth)
                binding.pvsEditView.onUpdate()
                binding.dragRectView.setEditTypeAndShow(DragRectView.TYPE_TEXT, pvsLayer)
            } else {
                addTextLayer(text)
            }
            binding.etTextAdd.setText("")
            hideKeyBoard(binding.etTextAdd)
            binding.rlEditText.hide()
        }
        binding.pvsEditView.drawTimesChanged = {
            addDrawTimes()
        }
        binding.ivTenMenu.setOnClickListener {
            popToolsSelector.showAsDropDown(it)
        }
        binding.pickColorView.onColorPicked = {
            mCurrentPaint.color = it
            onPaintChanged()
            popColorSelector.showAsDropDown(binding.imageColor, Gravity.RIGHT, 0, 0)
        }
        cutBinding = MenuBarCutBinding.inflate(layoutInflater)
        distortBinding = MenuBarDistortBinding.inflate(layoutInflater)
        fillBinding = MenuBarFillBinding.inflate(layoutInflater)
        initHistoryPen()
        initPopups()
        initCutBar()
        initDistortBar()
        initUndoRedo()
        observeData()
        initPaint()
        initData()
        initSeekBars()
    }

    private fun initHistoryPen() {
        val customStr = MmkvUtils.get(historyPenKey, "")
        if (!TextUtils.isEmpty(customStr)) {
            historyPenList.addAll(
                Gson().fromJson<ArrayList<PaintBean>>(
                    customStr, TypeToken.getParameterized(
                        ArrayList::class.java, PaintBean::class.java
                    ).type
                ))
        }
    }

    private fun initDistortBar() {
        with(distortBinding) {
            clDistortType.setOnClickListener {
                popDistortSelector.showAtLocation(it, Gravity.LEFT or Gravity.BOTTOM, 150, 0)
            }
            clDistortReset.setOnClickListener {
                popMeshStepSet.showAtLocation(it, Gravity.LEFT or Gravity.BOTTOM, 350, 0)
            }
            clDistortClear.setOnClickListener {
                binding.pvsEditView.resetDistort()
            }
            sbDistortSize.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.pvsEditView.pvsDistortUtils.radius = progress.toFloat()
                    showCanvasInfo("尺寸：${progress}", false)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            sbDistortStrong.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.pvsEditView.pvsDistortUtils.strong = progress.toFloat()/1000
                    showCanvasInfo("强度：${progress}", false)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })

        }
    }

    /**
     * 选区bar
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initCutBar() {
        with(cutBinding) {
            // 拉索类型
            clLasso.setOnClickListener {
                popLassoSelector.showAtLocation(it, Gravity.LEFT or Gravity.BOTTOM, 50, 0)
            }
            // 填充类型
            clAreaType.setOnClickListener {
                popFillSelector.showAtLocation(it, Gravity.LEFT or Gravity.BOTTOM, 150, 0)
            }
            tvCopy.setOnClickListener {
                if (binding.cutView.canOperate()) {
                    binding.pvsEditView.genCopyLayerBmp(binding.cutView)
                    binding.rlBtmToolBar.removeAllViews()
                } else {
                    ToastUtils.showShort("请选择区域")
                }
            }
            tvCut.setOnClickListener {
                if (binding.cutView.canOperate()) {
                    binding.pvsEditView.genCutLayerBmp(binding.cutView)
                    binding.rlBtmToolBar.removeAllViews()
                } else {
                    ToastUtils.showShort("请选择区域")
                }
            }
            tvReverse.setOnClickListener {
                binding.cutView.onReverse()
            }
            tvClearSelect.setOnClickListener {
                binding.rlBtmToolBar.removeAllViews()
                binding.cutView.finish()
                binding.pvsEditView.onUpdate()
            }
            binding.cutView.actionShowLoading = {
                showLoadingDialog()
            }
            binding.cutView.actionHideLoading = {
                hideLoadingDialog()
            }
            binding.cutView.actionBitmap = {
                model.addBitmapLayer(it, RectF(0f, 0f, it.width.toFloat(), it.height.toFloat()))
            }
        }
        with(fillBinding) {
            tvClearArea.setOnClickListener {
                updateLeafStatus(PenLeaf.LeafType.CLEAN)
            }
            tvFillArea.setOnClickListener {
                updateLeafStatus(PenLeaf.LeafType.FILL)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (popPaintSelector.isShowing) {
            popPaintSelector.freshMonitor()
        }
    }

    private fun updateLeafStatus(type: PenLeaf.LeafType) {
        when (type) {
            PenLeaf.LeafType.CLEAN -> {
                var drawable = leafInfo[PenLeaf.LeafType.CLEAN]!!
                drawable.selected = true
                fillBinding.tvClearArea.updateDrawableTop(drawable)
                drawable = leafInfo[PenLeaf.LeafType.FILL]!!
                drawable.selected = false
                fillBinding.tvFillArea.updateDrawableTop(drawable)
            }
            PenLeaf.LeafType.FILL -> {
                var drawable = leafInfo[PenLeaf.LeafType.CLEAN]!!
                drawable.selected = false
                fillBinding.tvClearArea.updateDrawableTop(drawable)
                drawable = leafInfo[PenLeaf.LeafType.FILL]!!
                drawable.selected = true
                fillBinding.tvFillArea.updateDrawableTop(drawable)
            }
        }
        binding.pvsEditView.currentPen?.let {
            if (it is PenLeaf) {
                it.leafType = type
            }
        }
    }

    private fun addDrawTimes() {
        drawTimes++
        binding.tvDrawTimes.text = "绘制${drawTimes}笔"
    }

    private fun showCanvasInfo(text: String, canClickable: Boolean) {
        binding.tvCanvasInfo.show()
        binding.tvCanvasInfo.text = text
        if (canClickable) {
            binding.tvCanvasInfo.setOnClickListener {
                binding.fmZoomView.resetRotation()
                binding.fmZoomView.resetScale()
                binding.tvCanvasInfo.setOnClickListener(null)
            }
        }
        // 延迟消失
        DebounceUtils.debounce {
            binding.tvCanvasInfo.hide()
            binding.tvCanvasInfo.setOnClickListener(null)
        }
    }

    private fun editText(text: String? = "") {
        binding.etTextAdd.setText(text)
        val length = text?.length
        if (length != null) {
            binding.etTextAdd.setSelection(length)
        }
        binding.rlEditText.show()
        binding.etTextAdd.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.etTextAdd.onFocusChangeListener = null
                    showKeyBoard(v)
                }
            }
        binding.etTextAdd.requestFocus()
    }

    private fun exportPicture() {
        PermissUtils.getPicturePermission(this@DrawBoardActivity) {
            showLoadingDialog()
            model.exportProduct(binding.pvsEditView)
        }
    }

    private fun initData() {
        draftId = intent.getLongExtra(EXTRA_DRAFT_ID, 0)
        isFromDraft = intent.getBooleanExtra(EXTRA_IS_FROM_DRAFT, false)
        if (!isFromDraft) {
            canvasData = intent.getParcelableExtra<CanvasBean>("canvas_data") as CanvasBean
        }
        initCanvas()
        // 形状画笔出图
        PvsRichLayer.onShapeGenerated = {bmp, rectF ->
            model.addBitmapLayer(bmp, rectF)
        }
        binding.pvsEditView.onCutBmpGenerated = {bmp, rectF ->
            model.addBitmapLayer(bmp, rectF)
        }
    }

    private fun updateBgColor() {
        val dialog = CommonTipsDialog(
            this@DrawBoardActivity, "是否修改背景？",
            "取消", "修改"
        )
        dialog.setConfirmListener {
            showPopup(popBgColorSelector, binding.imageColor)
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun initUndoRedo() {
        //撤销重做回调
        OperationUtils.operationListener = operationListener
        binding.imageChexiao.setOnClickListener {
            // 撤销
            if (OperationUtils.hasUndoOperation()) {
                OperationUtils.undo()
            } else {
                ToastUtils.showShort("没有上一步啦！")
            }
            onPaintChanged()
        }
        binding.imageRedo.setOnClickListener {
            // 重做
            if (OperationUtils.hasRedoOperation()) {
                OperationUtils.redo()
            }
            onPaintChanged()
        }
    }

    private fun initSeekBars() {
        binding.sbSize.progress = mPen.size
        binding.sbAlpha.progress = 255 - mPen.alpha
        binding.sbSize.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (binding.cutView.isCutting) {
                    binding.cutView.penSize = progress.toFloat()
                } else {
                    mPen.size = progress
                    showCanvasInfo("粗细：$progress", false)
                    onPaintChanged()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.sbAlpha.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mPen.alpha = 255 - progress
                showCanvasInfo("透明度：${progress}", false)
                onPaintChanged()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun initPaint() {
        // 默认画笔为铅笔
        mPen = PenManager.getPen(mCurrentPaint)
        onPaintChanged()
    }

    private fun showPopup(popup: PopupWindow, parent: View) {
        if (popup is PopColorSelector) {
            popColorSelector.setSelectColor(mCurrentPaint.color)
        }
        popup.showAtLocation(parent, Gravity.BOTTOM, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPopups() {
        popSettingMenu = PopSettingMenu(this@DrawBoardActivity)
        popSettingMenu.menuClick = {
            when (it) {
                PopSettingMenu.MenuType.clear -> {
                    cleanAll()
                }
                PopSettingMenu.MenuType.create -> {
                    saveDraft(true)
                }
                PopSettingMenu.MenuType.export -> {
                    // 导出图片
                    exportPicture()
                }
                PopSettingMenu.MenuType.save -> {
                    // 保存草稿
                    saveDraft(false, false)
                }
            }
        }
        popBgColorSelector = PopColorSelector(this@DrawBoardActivity, Color.WHITE)
        popBgColorSelector.setColorChanged {
            model.replaceBgColor(mCanvasWidth, mCanvasHeight, it)
            shouldEnsure = true
            return@setColorChanged
        }
        popColorSelector = PopColorSelector(this@DrawBoardActivity, mCurrentPaint.color)
        popColorSelector.setColorChanged {
            if (isEditTextLayer) {
                if (binding.pvsEditView.pvsTimeLine.preAddLayer !is PvsTextLayer) {
                    return@setColorChanged
                }
                val textLayer = binding.pvsEditView.pvsTimeLine.preAddLayer as PvsTextLayer
                textLayer.color = it
                binding.pvsEditView.onUpdate()
                return@setColorChanged
            }
            mCurrentPaint.color = it
            onPaintChanged()
        }
        popPaintSelector = PopPaintSelector(this@DrawBoardActivity)
        popPaintSelector.onPaintChanged = {bean ->
            updateHistoryPen(bean)
            updatePaint(bean)
        }
        popPaintSelector.onPenSetting = {
            PenSettingActivity.pen = mPen
            penSettingLauncher.launch(Intent(this, PenSettingActivity::class.java))
        }
        popPaintSelector.addBrushListener = {
            val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            PermissionUtil.requestPermisions(this@DrawBoardActivity, list) { allGranted, grantedList, deniedList ->
                if (allGranted){
                    pickPictureLauncher.launch(getPickImageIntent())
                }
            }
        }
        popPaintSelector.freshHistoryPen(historyPenList)
        initPopLayerList()


        popTextToolsBar = PopTextToolsBar(this@DrawBoardActivity)
        popTextToolsBar.colorChanged = {color ->
            val layer = binding.pvsEditView.pvsTimeLine.preAddLayer
            layer?.let {
                val textLayer = it as PvsTextLayer
                textLayer.color = color
                binding.pvsEditView.onUpdate()
            }
        }
        popTextToolsBar.sizeChanged = {size ->
            val layer = binding.pvsEditView.pvsTimeLine.preAddLayer
            layer?.let {
                val textLayer = it as PvsTextLayer
                textLayer.textSize = size
                binding.pvsEditView.onUpdate()
            }
        }
        initShapePop()
        initLassoPop()
    }

    private fun initShapePop() {
        // 形状笔pop
        popShapeSelector = PopShapeSelector(this@DrawBoardActivity)
        popShapeSelector.onPaintChanged = {
            updatePaint(it)
        }
        // 工具菜单pop
        popToolsSelector = PopToolsSelector(this@DrawBoardActivity)
        popToolsSelector.actionTool = {
            onPaintChanged()
            when (it) {
                PopToolsSelector.ToolsType.PEN_LEAF -> {
                    // 柳叶笔
                    val paint = getLeafPen()
                    updatePaint(paint)

                }
                PopToolsSelector.ToolsType.SHAPE -> {
                    // 形状笔
                    popShapeSelector.showAtLocation(binding.pvsEditView, Gravity.BOTTOM, 0, 0)
                    popShapeSelector.notifyIndex()
                }
                PopToolsSelector.ToolsType.ADD_PIC -> {
                    addPictureLayer()
                }
                PopToolsSelector.ToolsType.ADD_TEXT -> {
                    editText()
                }
                PopToolsSelector.ToolsType.COLOR_PIPE -> {
                    // 吸管
                    binding.pickColorView.startPick(binding.pvsEditView)
                }
                PopToolsSelector.ToolsType.AREA_SELECT -> {
                    binding.rlBtmToolBar.removeAllViews()
                    binding.rlBtmToolBar.addView(cutBinding.root)
                    binding.cutView.start(binding.pvsEditView)
                }
                PopToolsSelector.ToolsType.YE_HUA -> {
                    binding.rlBtmToolBar.removeAllViews()
                    binding.rlBtmToolBar.addView(distortBinding.root)
                    binding.pvsEditView.startDistort()
                }
            }
        }
    }

    private fun initLassoPop() {
        popLassoSelector = PopLassoSelector(this@DrawBoardActivity)
        popLassoSelector.actionLasso = { type ->
            val bean = lassoInfo[type]
            bean?.let {
                cutBinding.tvLasso.text = it.name
                cutBinding.ivLasso.updateImageDrawable(it.drawableBean)
                binding.cutView.lassoType = type
            }
        }
        popFillSelector = PopFillSelector(this@DrawBoardActivity)
        popFillSelector.actionLeaf = {type ->
            val bean = fillInfo[type]
            bean?.let {
                cutBinding.tvAreaType.text = it.name
                cutBinding.ivAreaType.updateImageDrawable(it.drawableBean)
                binding.cutView.fillType = type
            }
        }
        popDistortSelector = PopDistortSelector(this@DrawBoardActivity)
        popDistortSelector.actionDistort = { type ->
            val bean = distortInfo[type]
            bean?.let {
                distortBinding.tvDistortType.text = it.name
                distortBinding.ivDistortType.updateImageDrawable(it.drawableBean)
                binding.pvsEditView.setDistortType(type)
            }
        }

        popMeshStepSet = PopMeshStepSet(this@DrawBoardActivity)
        popMeshStepSet.onStepChanged = {
            binding.pvsEditView.setMeshStep(it)
        }
    }

    private fun updatePaint(it: PaintBean) {
        mCurrentPaint.id = it.id
        mCurrentPaint.path = it.path
        mCurrentPaint.penType = it.penType
        mCurrentPaint.name = it.name
        // 如果没有设置默认颜色，要保留原来画笔的颜色
        if (it.color != Color.BLACK) {
            mCurrentPaint.color = it.color
        }
        popPaintSelector.updateCurrentPen(mCurrentPaint)
        onPaintChanged()
    }

    private fun getPickImageIntent(): Intent = Intent(Intent.ACTION_PICK).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    }

    /**
     * 图层设置popup
     */
    private fun initLayerConfigPop(position: Int) {
        val targetLayer = binding.pvsEditView.pvsTimeLine.layerList[position]
        val layerBinding = PopOperationBinding.inflate(layoutInflater, null, false)
        val mPopupWindow = PopupWindow(layerBinding.root)
        mPopupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
        mPopupWindow.height = ViewGroup.LayoutParams.MATCH_PARENT
        mPopupWindow.isFocusable = true

        layerBinding.llEmpty.setOnClickListener {
            mPopupWindow.dismiss()
        }

        val originAlpha: Int = (targetLayer.alpha / 2.55).toInt()
        layerBinding.tcAlphaBar.progress = originAlpha
        layerBinding.tcAlpha.text = "${originAlpha}%"
        layerBinding.tcAlphaBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                var p = p1
                targetLayer.alpha = (p * 2.55).toInt()
                layerBinding.tcAlpha.text = "${p1}%"
                binding.pvsEditView.onUpdate()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                OperationUtils.addRichLayerAttr(
                    OperaLayerInfo(
                        OperationType.UPDATE_LAYER_ALPHA,
                        targetLayer.objectId,
                        originAlpha,
                        targetLayer.alpha
                    )
                )
            }
        })

        layerBinding.renameBt.setOnClickListener {
            val layerRenameDialog = LayerRenameDialog(this)

            layerRenameDialog.show()
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            layerRenameDialog.window?.setLayout((width * 0.75).toInt(), (height * 0.25).toInt())

            layerRenameDialog.setConfigListener { layerName ->
                val layerList = binding.pvsEditView.pvsTimeLine.layerList
                if (position == layerList.size) {
                    Toast.makeText(this, "背景层不能重命名", Toast.LENGTH_SHORT).show()
                    return@setConfigListener
                }
                targetLayer.name = layerName
                // 更新pop layer列表
                onLayerListUpdate()
                layerRenameDialog.cancel()
            }
            layerRenameDialog.setCancelListener {
                layerRenameDialog.cancel()
            }
            mPopupWindow.dismiss()
        }
        layerBinding.layerCopyBt.setOnClickListener {
            val layerList = binding.pvsEditView.pvsTimeLine.layerList
            val size = layerList.size + 1
            if (size > maxLayerCount) {
                ToastUtils.showShort("最多只能添加${maxLayerCount}个图层")
                return@setOnClickListener
            }
            if (position == layerList.size) {
                Toast.makeText(this, "背景层不能复制", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pvsRichLayer = targetLayer.copy() as PvsRichLayer
            pvsRichLayer.objectId = System.nanoTime()
            pvsRichLayer.name = "图层" + (layerList.size + 1)
            model.addCopyLayer(pvsRichLayer)
            mPopupWindow.dismiss()
        }
        layerBinding.clearCanve.setOnClickListener {
            cleanLayer(targetLayer as PvsRichLayer)
            mPopupWindow.dismiss()
        }
        layerBinding.delCanve.setOnClickListener {
            OperationUtils.deleteRichLayer(position, targetLayer as PvsRichLayer)
            mPopupWindow.dismiss()
        }

        layerBinding.downNext.setOnClickListener {
            val layerList = binding.pvsEditView.pvsTimeLine.layerList
            if (position > 0) {
                val oldList = arrayListOf<PvsLayer>()
                val newList = arrayListOf<PvsLayer>()
                oldList.addAll(layerList)
                val pre = layerList[position - 1]
                val next = layerList[position]
                layerList[position] = pre
                layerList[position - 1] = next
                newList.addAll(layerList)
                // 通过操作类触发UI更新
                OperationUtils.sortAllLayer(oldList, newList)
            }
            mPopupWindow.dismiss()
        }

        layerBinding.downMerge.setOnClickListener {
            if (position > 1) {
                val layerList = binding.pvsEditView.pvsTimeLine.layerList
                val oldList = arrayListOf<PvsLayer>()
                val newList = arrayListOf<PvsLayer>()
                oldList.addAll(layerList)
                val target = layerList[position-1] as PvsRichLayer
                val current = layerList[position] as PvsRichLayer
                target.mergeLayer(current)
                layerList.removeAt(position)
                newList.addAll(layerList)
                // 通过操作类触发UI更新
                OperationUtils.sortAllLayer(oldList, newList)
            } else {
                ToastUtils.showShort("已无法合并")
            }
            mPopupWindow.dismiss()
        }

        mPopupWindow.showAtLocation(binding.fmCanvas, Gravity.BOTTOM, 0, 0)
    }

    private fun addPictureLayer() {
        PermissUtils.getPicturePermission(this@DrawBoardActivity) {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(object: OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        if (!result.isNullOrEmpty()) {
                            val path = result[0].realPath
                            if (path != null) {
                                model.addPhotoLayer(path, mCanvasWidth,mCanvasHeight)
                            }
                            popLayerList.dismiss()
                        }
                    }
                    override fun onCancel() {

                    }
                })
        }
    }

    private fun addTextLayer(text: String) {
        model.addTextLayer(text, mCanvasWidth, mCanvasHeight)
    }

    /**
     * 图层弹窗单独初始化，需要背景layer填充
     */
    private fun initPopLayerList() {
        popLayerList = PopLayerList(this@DrawBoardActivity)
        popLayerList.onCreateAction {
            createLayer()
        }
        popLayerList.onVisibleAction { position ->
            if (position == binding.pvsEditView.pvsTimeLine.layerList.size) {
                // 背景图层
                binding.pvsEditView.pvsTimeLine.bgLayer!!.isShow =
                    !binding.pvsEditView.pvsTimeLine.bgLayer!!.isShow
                onLayerListUpdate()
                binding.pvsEditView.onUpdate()
                shouldEnsure = true
            } else {
                val layer = binding.pvsEditView.pvsTimeLine.layerList[position]
                OperationUtils.addRichLayerAttr(
                    OperaLayerInfo(
                        OperationType.UPDATE_LAYER_VISIBLE,
                        layer.objectId,
                        layer.isShow,
                        !layer.isShow
                    )
                )
            }
        }

        popLayerList.backgroundLayerImageOnClick {
            popLayerList.let {
                if (it.isShowing) {
                    it.dismiss()
                    updateBgColor()
                }
            }
        }

        popLayerList.bgLayerChanged = {
            binding.pvsEditView.onUpdate()
        }

        popLayerList.layoutOnLongClick { position ->
            initLayerConfigPop(position)
        }
    }

    private fun onPaintChanged() {
        if (mPen.id != mCurrentPaint.id) {
            mPen = PenManager.getPen(mCurrentPaint)
            if (mPen is PicPen) {
                //(mPen as PicPen).setResIds(getPicResListByPenId(mCurrentPaint.id))
            }
        }
        mPen.color = mCurrentPaint.color
        binding.cutView.finish()
        binding.pvsEditView.editMode = PvsEditView.EditMode.EDITOR
        binding.rlBtmToolBar.removeAllViews()
        if (mPen is PenLeaf) {
            binding.rlBtmToolBar.addView(fillBinding.root)
        }
        binding.pvsEditView.onUpdate()
        //内置画笔
        binding.pvsEditView.currentPen = mPen
        binding.pvsEditView.editMode = PvsEditView.EditMode.EDITOR
        binding.sbAlpha.isEnabled = mPen.alphaEnabled
        binding.imageColor.isEnabled = mPen.colorEnabled
        binding.sbAlpha.progress = 255-mPen.alpha
        binding.sbSize.progress = mPen.size
        popColorSelector.setSelectColor(mPen.color)
    }

    // 底部菜单展示
    private fun showBottomMenu(target: MenuState?) {
        bottomMenuList.forEach {
            it.isSelect = false
        }
        target?.let {
            it.isSelect = true
        }
        updateBottomMenu()
    }

    private fun updateBottomMenu() {
        binding.imageHuabi.setMenuState(menuPaintIcon)
        binding.imageXp.setMenuState(menuEraseIcon)
        binding.imageColor.setMenuState(menuColorIcon)
    }

    private fun initCanvas() {
        TimeLineData.pvsTimeLine = binding.pvsEditView.pvsTimeLine
        // 草稿箱加载
        if (isFromDraft) {
            val draftEntity = DraftDB.instance.draftDao().query(draftId)!!
            val draftBean: DraftBean =
                GsonUtils.fromJson(draftEntity.jsonData, DraftBean::class.java)
            mCanvasWidth = draftBean.width
            mCanvasHeight = draftBean.height
            initLayerSize()
            binding.pvsEditView.post {
                model.initFromDraft(draftBean)
            }
            return
        }
        if (canvasData.imgPath == null) {
            mCanvasWidth = canvasData.width
            mCanvasHeight = canvasData.height
            initLayerSize()
            binding.pvsEditView.post {
                model.addBgColorLayer(canvasData.width, canvasData.height, Color.WHITE)
            }
        } else {
            val bitmap = BitmapFactory.decodeFile(canvasData.imgPath)
            mCanvasWidth = bitmap.width
            mCanvasHeight = bitmap.height
            initLayerSize()
            binding.pvsEditView.post {
                model.addBgImageLayer(bitmap.width, bitmap.height, canvasData.imgPath)
            }
        }
    }

    private fun initLayerSize() {
        maxLayerCount = ScreenUtils.getMaxLayerCount(mCanvasWidth, mCanvasHeight)
        model.viewWidth = mCanvasWidth
        model.viewHeight = mCanvasHeight
        binding.pvsEditView.pvsTimeLine.viewWidth = mCanvasWidth
        binding.pvsEditView.pvsTimeLine.viewHeight = mCanvasHeight
        val layoutParams = binding.fmCanvas.layoutParams
        layoutParams.width = mCanvasWidth
        layoutParams.height = mCanvasHeight
        binding.fmCanvas.layoutParams = layoutParams
        binding.fmZoomView.post {
            val w = binding.fmZoomView.width
            val h = binding.fmZoomView.height
            // 将画布缩放到最适合屏幕的大小
            val scaleX = w.toFloat() / mCanvasWidth.toFloat()
            val scaleY = h.toFloat() / mCanvasHeight.toFloat()
            val targetScale: Float = min(scaleX, scaleY)
            if (targetScale < 1f) {
                binding.fmZoomView.initScaleSize(targetScale)
            }
        }
    }

    private fun getNoneBitmap(): Bitmap {
        return BitmapFactory.decodeResource(resources, R.mipmap.no_background)
    }

    private fun observeData() {
        // 背景图层
        model.bgLayerData.observe(this) {
            val showRect = RectUtils.getShowRect(
                binding.pvsEditView.width,
                binding.pvsEditView.height,
                it.bgLayerWidth,
                it.bgLayerHeight
            )
            // 加上空白背景图层
            it.bitmap = getNoneBitmap()
            binding.pvsEditView.changeCanvasSize(showRect.width(), showRect.height())
            binding.pvsEditView.init(it)
            onLayerListUpdate()
        }
        // 添加图层
        model.layerLiveData.observe(this) {
            val layerListCount = binding.pvsEditView.pvsTimeLine.layerList.size
            binding.pvsEditView.pvsTimeLine.layerList.forEach { layer ->
                layer.isSelected = false
            }
            binding.pvsEditView.pvsTimeLine.bgLayer!!.isSelected = false
            it.isSelected = true
            // 第一个空白图层不计入回退操作
            if (layerListCount == 0) {
                binding.pvsEditView.pvsTimeLine.layerList.add(it)
            } else {
                OperationUtils.addRichLayer(layerListCount, it as PvsRichLayer)
            }
            // 更新pop layer列表
            onLayerListUpdate()
            binding.pvsEditView.onUpdate()
        }
        // 预加载图层
        model.preLayerLiveData.observe(this) {
            addPreEditToLayer()
            binding.pvsEditView.pvsTimeLine.preAddLayer = it
            binding.pvsEditView.onUpdate()
            binding.dragRectView.pvsLayer = it
            val rectType = if (it is PvsTextLayer) {
                isEditTextLayer = true
                DragRectView.TYPE_TEXT
            } else {
                DragRectView.TYPE_IMAGE
            }
            disableToolsBar()
            binding.dragRectView.setEditTypeAndShow(rectType, it)
        }
        model.layerListLiveData.observe(this) {
            if (it.size > 0) {
                it[it.size - 1].isSelected = true
            }
            binding.pvsEditView.pvsTimeLine.layerList = it
            // 更新pop layer列表
            onLayerListUpdate()
            binding.pvsEditView.onUpdate()
        }
        model.saveSuccessData.observe(this) {
            if (isCreateNew) {
                startActivity(Intent(this@DrawBoardActivity, NewCanvasActivity::class.java))
                finish()
                return@observe
            }
            hideLoadingDialog()
            ToastUtils.showShort("保存成功")
            if (it) {
                finish()
            }
        }

        //编辑框手势操作回调接口
        binding.dragRectView.onDragGestureListener = dragGestureListener
    }

    private fun onLayerListUpdate() {
        val layerList = arrayListOf<PvsLayer>()
        // 背景图层
        layerList.add(binding.pvsEditView.pvsTimeLine.bgLayer!!)
        // 富图层
        layerList.addAll(binding.pvsEditView.pvsTimeLine.layerList)
        // 装载倒序列表
        popLayerList.onDataChanged(layerList.reversed())
    }

    /**
     * 创建图层
     */
    private fun createLayer() {
        val size = binding.pvsEditView.pvsTimeLine.layerList.size + 1
        if (size > maxLayerCount) {
            ToastUtils.showShort("最多只能添加${maxLayerCount}个图层")
            return
        }
        model.addEmptyLayer("图层$size")
    }

    private fun cleanAll() {
        val dialog = CommonTipsDialog(
            this@DrawBoardActivity, "清屏不可撤销，确认清屏？",
            "取消", "确认"
        )
        dialog.setConfirmListener {
            binding.pvsEditView.pvsTimeLine.layerList.clear()
            OperationUtils.clear()
            // 添加空白图层
            model.addEmptyLayer(null)
            //initPopups()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun cleanLayer(layer: PvsRichLayer) {
        val old = layer.copy()
        layer.reset()
        binding.pvsEditView.onUpdate()
        onLayerListUpdate()
        shouldEnsure = true
        OperationUtils.updateRichLayer(old as PvsRichLayer, layer)
    }

    private fun hideKeyBoard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyBoard(view: View) {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    private fun saveDraft(isReCreate: Boolean, isExists: Boolean = true) {
        isCreateNew = isReCreate
        val msg = if (isReCreate) {
            "是否保存当前画布再新建"
        } else {
            "是否保存当前画布"
        }
        val dialog = CommonTipsDialog(
            this@DrawBoardActivity, msg,
            "不保存", "保存"
        )
        dialog.setConfirmListener {
            PermissUtils.getPicturePermission(this@DrawBoardActivity) {
                showLoadingDialog()
                model.saveDraft(binding.pvsEditView, draftId)
            }
        }
        dialog.setCancelListener {
            if (isReCreate) {
                startActivity(Intent(this@DrawBoardActivity, NewCanvasActivity::class.java))
            }
            if (isExists) {
                finish()
            }
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    override fun onBackPressed() {
        if (shouldEnsure) {
            saveDraft(false)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        TimeLineData.pvsTimeLine = null
        OperationUtils.operationListener = null
        OperationUtils.clear()
        PenManager.penCache.clear()
        PvsRichLayer.onShapeGenerated = null
        if (historyPenList.size > 0) {
            MmkvUtils.save(historyPenKey, JSON.toJSONString(historyPenList))
        }
        super.onDestroy()
    }

    private fun disableToolsBar() {
        with(binding) {
            rlSideBar.children.forEach {
                it.isEnabled = false
            }

            imageChexiao.isEnabled = false
            imageRedo.isEnabled = false
            ivMenu.isEnabled = false
            ivTenMenu.isEnabled = false
            if (isEditTextLayer) {
                // 编辑文本时，可以选择颜色
                imageColor.isEnabled = true
                showBottomMenu(menuColorIcon)
            }
        }
    }

    private fun enabledToolsBar() {
        with(binding) {
            rlSideBar.children.forEach {
                it.isEnabled = true
            }

            imageChexiao.isEnabled = true
            imageRedo.isEnabled = true
            ivMenu.isEnabled = true
            ivTenMenu.isEnabled = true
        }
    }

    private fun addPreEditToLayer() {
        if (binding.pvsEditView.pvsTimeLine.preAddLayer != null) {
            binding.pvsEditView.addSubLayer(binding.pvsEditView.pvsTimeLine.preAddLayer!!)
            binding.pvsEditView.pvsTimeLine.preAddLayer = null
        }
        binding.pvsEditView.onUpdate()
    }

    /**
     * =================图片、文本编辑=================
     */
    /**
     * 编辑框手势操作回调接口
     */
    private val dragGestureListener = object : DragRectView.OnDragGestureListener {
        override fun onScaleAndRotate(
            pvsLayer: PvsLayer?,
            realRect: RectF,
            mScale: Float,
            rotateAngle: Float
        ) {
            copyTimes = 1
            pvsLayer?.pos = realRect
            pvsLayer?.scaleX = mScale
            pvsLayer?.scaleY = mScale
            pvsLayer?.rotation = rotateAngle
            binding.pvsEditView.onUpdate()
        }

        override fun onTranslation(pvsLayer: PvsLayer?, realRect: RectF) {
            pvsLayer?.pos = realRect
            copyTimes = 1
            binding.pvsEditView.onUpdate()
        }

        override fun onScaleAndRotateEnd(
            pvsOldLayer: PvsLayer?,
            pvsLayer: PvsLayer?,
            realRect: RectF,
            mScale: Float,
            rotateAngle: Float
        ) {
            LogUtils.d("onScaleAndRotateEnd: 缩放旋转结束")
        }

        override fun onTranslationEnd(
            pvsOldLayer: PvsLayer?,
            pvsLayer: PvsLayer?,
            realRect: RectF
        ) {
            LogUtils.d("onTranslationEnd: 平移结束")

        }

        override fun onClickDelete(pvsLayer: PvsLayer?) {
            copyTimes = 1
            binding.pvsEditView.pvsTimeLine.preAddLayer = null
        }

        override fun onClickCopy(pvsLayer: PvsLayer?) {
            val copy = pvsLayer?.copy()
            val offset = copyTimes * copyDeviationDistance
            copy?.let {
                it.objectId = System.currentTimeMillis()
                it.pos.offset(offset, offset)
                it.matrix.postTranslate(offset, offset)
                model.preLayerLiveData.postValue(it)
            }
        }

        override fun onClickEdit(pvsLayer: PvsLayer?) {
            if (pvsLayer !is PvsTextLayer) {
                return
            }
            editText(pvsLayer.text)
        }

        override fun onClickCloseEditStatus() {
            copyTimes = 1
            addPreEditToLayer()
            enabledToolsBar()
            popTextToolsBar.dismiss()
            isEditTextLayer = false
            onPaintChanged()
            addDrawTimes()
        }

        override fun onSwitchLayer(pvsLayer: PvsLayer?) {
            //切换选中图层
            copyTimes = 1
        }
    }
}