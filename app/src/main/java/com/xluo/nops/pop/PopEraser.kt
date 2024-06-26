package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.SeekBar
import com.xluo.nops.databinding.PopEraserBinding


class PopEraser: PopupWindow {

    private lateinit var binding: PopEraserBinding

    private var mContext: Context

    private var mSize: Int = 30

    lateinit var onSizeChanged: ((size: Int) -> Unit)

    fun setSizeChanged(action: (size: Int) -> Unit) {
        onSizeChanged = action
    }

    constructor(context: Context, size: Int): super(context) {
        mContext = context
        mSize = size
        initView()
    }

    private fun initView() {
        binding = PopEraserBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.llEmpty.setOnClickListener {
            dismiss()
        }
        binding.tvXpSize.text = "$mSize"
        binding.seekbarXp.progress = mSize
        binding.seekbarXp.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mSize = p1
                binding.tvXpSize.text = "$mSize"
                onSizeChanged?.invoke(mSize)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

}