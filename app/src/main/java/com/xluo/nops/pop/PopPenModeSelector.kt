package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.xluo.nops.adapter.PenModeAdapter
import com.xluo.nops.bean.penMixModeList
import com.xluo.nops.databinding.PopPenModeSelectBinding
import com.xluo.pen.bean.PaintBean
import com.xluo.pen.core.PenMixMode

class PopPenModeSelector : PopupWindow {

    private lateinit var binding: PopPenModeSelectBinding

    private var mContext: Context

    private var penModeAdapter: PenModeAdapter? = null

    private var penMode = penMixModeList[0]

    var onPenModeChanged: ((PenMixMode) -> Unit)? = null

    constructor(context: Context, mode: PenMixMode?) : super(context) {
        mContext = context
        mode?.let {
            penMode = it
        }
        initView()
    }

    private fun initView() {
        binding = PopPenModeSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true
        binding.llEmpty.setOnClickListener {
            dismiss()
        }
        initPaintInfo()
    }

    private fun initPaintInfo() {
        penModeAdapter = PenModeAdapter(mContext, penMixModeList)
        val typeLayoutManager = LinearLayoutManager(mContext)
        typeLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvPenModeList.layoutManager = typeLayoutManager
        binding.rvPenModeList.adapter = penModeAdapter
        penModeAdapter?.onClick = {
            onPenModeChanged?.invoke(it)
            dismiss()
        }
        var index = 0
        penMixModeList.forEachIndexed { i, penMixMode ->
            if (penMixMode.id == penMode.id) {
                index = i
            }
        }
        penModeAdapter!!.index = index
        penModeAdapter!!.notifyDataSetChanged()
    }

}