package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.xluo.core.widget.PvsCutView
import com.xluo.nops.bean.fillInfo
import com.xluo.nops.bean.updateDrawableLeft
import com.xluo.nops.databinding.PopLeafSelectBinding

class PopFillSelector : PopupWindow {

    private lateinit var binding: PopLeafSelectBinding

    private var mContext: Context

    var actionLeaf: ((PvsCutView.FillType) -> Unit)? = null


    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopLeafSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.popBg.setOnClickListener {
            dismiss()
        }

        binding.tvFill.setOnClickListener {
            dismiss()
            updateList(PvsCutView.FillType.ADD)
        }
        binding.tvClear.setOnClickListener {
            dismiss()
            updateList(PvsCutView.FillType.REDUCE)
        }
    }

    private fun updateList(type: PvsCutView.FillType) {
        for ((_, v) in fillInfo) {
            v.drawableBean.selected = false
        }
        fillInfo[type]?.let {
            it.drawableBean.selected = true
        }
        binding.tvFill.updateDrawableLeft(fillInfo[PvsCutView.FillType.ADD]!!.drawableBean)
        binding.tvClear.updateDrawableLeft(fillInfo[PvsCutView.FillType.REDUCE]!!.drawableBean)
        actionLeaf?.invoke(type)
    }
}