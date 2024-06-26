package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.xluo.core.utils.PvsDistortUtils
import com.xluo.nops.bean.distortInfo
import com.xluo.nops.bean.updateDrawableLeft
import com.xluo.nops.databinding.PopDistortSelectBinding

class PopDistortSelector : PopupWindow {

    private lateinit var binding: PopDistortSelectBinding

    private var mContext: Context

    var actionDistort: ((PvsDistortUtils.DistortType) -> Unit)? = null


    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopDistortSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.popBg.setOnClickListener {
            dismiss()
        }

        binding.tvPush.setOnClickListener {
            updateList(PvsDistortUtils.DistortType.Push)
            dismiss()
        }
        binding.tvReset.setOnClickListener {
            updateList(PvsDistortUtils.DistortType.Reset)
            dismiss()
        }
        binding.tvLtoR.setOnClickListener {
            updateList(PvsDistortUtils.DistortType.LtoR)
            dismiss()
        }
        binding.tvRtoL.setOnClickListener {
            updateList(PvsDistortUtils.DistortType.RtoL)
            dismiss()
        }
        binding.tvShrink.setOnClickListener {
            updateList(PvsDistortUtils.DistortType.Shrink)
            dismiss()
        }
        binding.tvExtend.setOnClickListener {
            updateList(PvsDistortUtils.DistortType.Extend)
            dismiss()
        }
    }

    private fun updateList(type: PvsDistortUtils.DistortType) {
        for ((_, v) in distortInfo) {
            v.drawableBean.selected = false
        }
        distortInfo[type]?.let {
            it.drawableBean.selected = true
        }
        binding.tvPush.updateDrawableLeft(distortInfo[PvsDistortUtils.DistortType.Push]!!.drawableBean)
        binding.tvReset.updateDrawableLeft(distortInfo[PvsDistortUtils.DistortType.Reset]!!.drawableBean)
        binding.tvLtoR.updateDrawableLeft(distortInfo[PvsDistortUtils.DistortType.LtoR]!!.drawableBean)
        binding.tvRtoL.updateDrawableLeft(distortInfo[PvsDistortUtils.DistortType.RtoL]!!.drawableBean)
        binding.tvShrink.updateDrawableLeft(distortInfo[PvsDistortUtils.DistortType.Shrink]!!.drawableBean)
        binding.tvExtend.updateDrawableLeft(distortInfo[PvsDistortUtils.DistortType.Extend]!!.drawableBean)
        actionDistort?.invoke(type)
    }
}