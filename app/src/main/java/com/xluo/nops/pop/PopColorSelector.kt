package com.xluo.nops.pop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.xluo.nops.databinding.PopColorSelectorBinding

class PopColorSelector: PopupWindow {

    private lateinit var binding: PopColorSelectorBinding

    private var mContext: Context

    private var selectColor = Color.BLACK

    private var lastColor = Color.BLACK

    private var onColorChanged: ((color: Int) -> Unit)? = null

    constructor(context: Context, color: Int): super(context) {
        mContext = context
        selectColor = color
        lastColor = color
        initView()
    }


    fun setColorChanged(action: ((color: Int) -> Unit)) {
        onColorChanged = action
    }

    fun setSelectColor(color: Int) {
        lastColor = selectColor
        binding.lastColorView.setPaintColor(lastColor)
        binding.colorPickerView.setInitialColor(color)
    }

    private fun initView() {
        binding = PopColorSelectorBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.tvColorCancle.setOnClickListener {
            dismiss()
        }
        binding.llEmpty.setOnClickListener {
            dismiss()
        }
        binding.tvColorOk.setOnClickListener {
            dismiss()
        }
        binding.colorPickerView.setInitialColor(selectColor)
        val flag = BubbleFlag(mContext)
        flag.flagMode = FlagMode.FADE
        binding.colorPickerView.flagView = flag
        binding.colorPickerView.setColorListener(object: ColorEnvelopeListener {
            override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                envelope?.let {
                    selectColor = it.color
                    binding.alphaTileView.setPaintColor(selectColor)
                }
            }
        })
        binding.colorPickerView.attachAlphaSlider(binding.alphaSlideBar)
        binding.colorPickerView.attachBrightnessSlider(binding.brightnessSlide)

        setOnDismissListener {
            onColorChanged?.invoke(selectColor)
        }
    }

}