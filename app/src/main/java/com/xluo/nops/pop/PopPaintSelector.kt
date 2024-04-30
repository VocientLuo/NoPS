package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.xluo.nops.adapter.PenListAdapter
import com.xluo.nops.adapter.PenTypeAdapter
import com.xluo.nops.databinding.PopPaintSelectBinding
import com.xluo.pen.PenManager
import com.xluo.pen.bean.PaintBean
import com.xluo.pen.bean.PaintType
import com.xluo.pen.bean.PenType
import com.xluo.pen.bean.penCircleList
import com.xluo.pen.bean.penLineList
import com.xluo.pen.bean.penMaterialList
import com.xluo.pen.bean.penNormalList
import com.xluo.pen.bean.penTypeList

class PopPaintSelector : PopupWindow {

    private lateinit var binding: PopPaintSelectBinding

    private var mContext: Context

    var onPaintChanged: ((bean: PaintBean) -> Unit)? = null

    var addBrushListener: (() -> Unit)? = null

    var onPenSetting: (() -> Unit)? = null

    private var paintAdapter: PenListAdapter? = null
    private var penTypeAdapter: PenTypeAdapter? = null

    private val historyPenList = arrayListOf<PaintBean>()

    private var penType = penTypeList[1]

    private var currentPaint: PaintBean? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    private fun initPaintInfo() {
        penTypeAdapter = PenTypeAdapter(mContext, penTypeList)
        val typeLayoutManager = LinearLayoutManager(mContext)
        typeLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvPenType.layoutManager = typeLayoutManager
        binding.rvPenType.adapter = penTypeAdapter
        penTypeAdapter?.onClick = {
            onTypeChanged(penTypeList[it])
        }
        onTypeChanged(penType)
    }

    private fun onTypeChanged(type: PaintType) {
        when (type.penType) {
            PenType.HISTORY -> {
                renderPenList(historyPenList)
            }
            PenType.NORMAL -> {
                renderPenList(penNormalList)
            }
            PenType.CIRCLE -> {
                renderPenList(penCircleList)
            }
            PenType.LINE -> {
                renderPenList(penLineList)
            }
            PenType.MATERIAL -> {
                renderPenList(penMaterialList)
            }
            else -> {
            }
        }
    }

    private fun renderPenList(list: ArrayList<PaintBean>) {
        paintAdapter = PenListAdapter(mContext, list)
        val penLayoutManager = LinearLayoutManager(mContext)
        penLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvPenList.layoutManager = penLayoutManager
        binding.rvPenList.adapter = paintAdapter
        paintAdapter?.onClick = {
            onPaintChanged?.invoke(it)
        }
    }

    fun updateCurrentPen(pen: PaintBean) {
        paintAdapter?.notifyDataSetChanged()
        // 200-300都是工具笔，不做处理
        if (pen.id < 200 || pen.id >= 300) {
            // 橡皮擦和涂抹不做处理
            currentPaint = pen.copy()
        }
        currentPaint?.let {
            binding.tvCurrentPen.text = it.name
            binding.ivCurrentPen.postDelayed({
                binding.ivCurrentPen.updatePen(PenManager.getPen(it))
            }, 100)
            paintAdapter?.updateSelectedPen(it.id)
        }
    }

    fun freshMonitor() {
        currentPaint?.let {
            binding.ivCurrentPen.updatePen(PenManager.getPen(it))
        }
        paintAdapter?.notifyDataSetChanged()
    }

    private fun initView() {
        binding = PopPaintSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        binding.ivPenSetting.setOnClickListener {
            onPenSetting?.invoke()
        }
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true
        binding.llEmpty.setOnClickListener {
            dismiss()
        }
        initPaintInfo()
    }

    fun freshHistoryPen(list: List<PaintBean>) {
        historyPenList.clear()
        historyPenList.addAll(list)
        if (penType.penType == PenType.HISTORY) {
            renderPenList(historyPenList)
        }
    }
}