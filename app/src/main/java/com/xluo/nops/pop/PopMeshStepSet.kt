package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.SeekBar

import com.xluo.nops.databinding.PopMeshResetBinding

class PopMeshStepSet: PopupWindow {

    private lateinit var binding: PopMeshResetBinding

    private var mContext: Context

    private var mStep: Int = 100

    var onStepChanged: ((size: Int) -> Unit)? = null

    constructor(context: Context): super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopMeshResetBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.tvStep.text = "$mStep"
        binding.sbStep.progress = mStep
        binding.sbStep.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mStep = p1
                binding.tvStep.text = "$mStep"
                onStepChanged?.invoke(mStep)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
    }

}