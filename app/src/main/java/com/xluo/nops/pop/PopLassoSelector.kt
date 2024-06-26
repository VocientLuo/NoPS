package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.xluo.core.widget.PvsCutView
import com.xluo.nops.bean.lassoInfo
import com.xluo.nops.bean.updateDrawableLeft
import com.xluo.nops.databinding.PopLassoSelectBinding

class PopLassoSelector : PopupWindow {

    private lateinit var binding: PopLassoSelectBinding

    private var mContext: Context

    var actionLasso: ((PvsCutView.LassoType) -> Unit)? = null


    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopLassoSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.popBg.setOnClickListener {
            dismiss()
        }

        binding.tvLasso.setOnClickListener {
            updateList(PvsCutView.LassoType.LASSO)
            dismiss()
        }
        binding.tvPaint.setOnClickListener {
            updateList(PvsCutView.LassoType.PAINT)
            dismiss()
        }
        binding.tvRect.setOnClickListener {
            updateList(PvsCutView.LassoType.RECT)
            dismiss()
        }
        binding.tvOval.setOnClickListener {
            updateList(PvsCutView.LassoType.OVAL)
            dismiss()
        }
        binding.tvMagic.setOnClickListener {
            updateList(PvsCutView.LassoType.MAGIC)
            dismiss()
        }

    }

    private fun updateList(type: PvsCutView.LassoType) {
        for ((_, v) in lassoInfo) {
            v.drawableBean.selected = false
        }
        lassoInfo[type]?.let {
            it.drawableBean.selected = true
        }
        binding.tvLasso.updateDrawableLeft(lassoInfo[PvsCutView.LassoType.LASSO]!!.drawableBean)
        binding.tvPaint.updateDrawableLeft(lassoInfo[PvsCutView.LassoType.PAINT]!!.drawableBean)
        binding.tvRect.updateDrawableLeft(lassoInfo[PvsCutView.LassoType.RECT]!!.drawableBean)
        binding.tvOval.updateDrawableLeft(lassoInfo[PvsCutView.LassoType.OVAL]!!.drawableBean)
        binding.tvMagic.updateDrawableLeft(lassoInfo[PvsCutView.LassoType.MAGIC]!!.drawableBean)
        actionLasso?.invoke(type)
    }
}