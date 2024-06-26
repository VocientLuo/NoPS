package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.xluo.nops.databinding.PopToolsSelectBinding

class PopToolsSelector : PopupWindow {

    enum class ToolsType {
        ADD_PIC,
        ADD_TEXT,
        COLOR_PIPE,
        SHAPE,
        AREA_SELECT,
        PEN_LEAF,
        YE_HUA,
    }

    private lateinit var binding: PopToolsSelectBinding

    private var mContext: Context

    var actionTool: ((ToolsType) -> Unit)? = null


    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    private fun initView() {
        binding = PopToolsSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.tvAddPic.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.ADD_PIC)
        }
        binding.tvAddText.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.ADD_TEXT)
        }
        binding.tvShape.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.SHAPE)
        }
        binding.tvPipe.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.COLOR_PIPE)
        }
        binding.tvSelectArea.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.AREA_SELECT)
        }
        binding.tvLeaf.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.PEN_LEAF)
        }
        binding.tvYehua.setOnClickListener {
            dismiss()
            actionTool?.invoke(ToolsType.YE_HUA)
        }

    }
}