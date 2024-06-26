package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.xluo.nops.databinding.PopPenSetMenuBinding

class PopPenSetMenu: PopupWindow {


    private lateinit var binding: PopPenSetMenuBinding

    private var mContext: Context

    var menuClick: (() -> Unit)? = null


    constructor(context: Context): super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopPenSetMenuBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.llReset.setOnClickListener {
            menuClick?.invoke()
            dismiss()
        }
        binding.llEmpty.setOnClickListener {
            dismiss()
        }

    }
}