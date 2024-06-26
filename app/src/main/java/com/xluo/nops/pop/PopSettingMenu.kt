package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.xluo.nops.databinding.PopSettingMenuBinding

class PopSettingMenu: PopupWindow {
    enum class MenuType{
        clear,
        create,
        save,
        export
    }

    private lateinit var binding: PopSettingMenuBinding

    private var mContext: Context

    var menuClick: ((MenuType) -> Unit)? = null


    constructor(context: Context): super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopSettingMenuBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.llClear.setOnClickListener {
            menuClick?.invoke(MenuType.clear)
            dismiss()
        }
        binding.llCreate.setOnClickListener {
            menuClick?.invoke(MenuType.create)
            dismiss()
        }
        binding.llSave.setOnClickListener {
            menuClick?.invoke(MenuType.save)
            dismiss()
        }
        binding.llExport.setOnClickListener {
            menuClick?.invoke(MenuType.export)
            dismiss()
        }

        binding.llEmpty.setOnClickListener {
            dismiss()
        }

    }
}