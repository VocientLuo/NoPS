package com.xluo.nops.pop

import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.recyclerview.widget.GridLayoutManager
import com.xluo.nops.bean.HistoryColor
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.xluo.lib_base.adapter.ViewBindAdapter
import com.xluo.nops.databinding.ModuleCanvasBottomTextConfigBinding
import com.xluo.nops.databinding.ModuleNoteItemHistoryColorBinding
import com.xluo.lib_base.adapter.bindAdapter
import com.xluo.nops.hide
import com.xluo.nops.show


class PopTextToolsBar: PopupWindow {

    private lateinit var binding: ModuleCanvasBottomTextConfigBinding
    private lateinit var largeAdapter: ViewBindAdapter<HistoryColor, ModuleNoteItemHistoryColorBinding>

    var colorChanged: ((color: Int) -> Unit)? = null
    var sizeChanged: ((size: Float) -> Unit)? = null

    private var mContext: Context

    constructor(context: Context): super(context) {
        mContext = context
        initView()
    }


    private fun initView() {
        binding = ModuleCanvasBottomTextConfigBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        initBottomTextConfigBehavior()
    }

    var textColor: Int = ColorUtils.string2Int("#000000")
        set(value) {
            field = value
            LogUtils.d("设置文本颜色: ${ColorUtils.int2ArgbString(field)}")
        }

    var textSize: Float = 12f
        set(value) {
            field = value
            LogUtils.d("设置文本大小: $textSize")
        }


    private fun initBottomTextConfigBehavior() {

        binding.includeTextSize.tvTitle.setOnClickListener {
            textSize = 21f
            sizeChanged?.invoke(textSize)
            binding.includeTextSize.seekBar.progress = textSize.toInt()
        }

        binding.includeTextSize.tvSmallTitle.setOnClickListener {
            textSize = 16f
            sizeChanged?.invoke(textSize)
            binding.includeTextSize.seekBar.progress = textSize.toInt()
        }

        binding.includeTextSize.tvSubTitle.setOnClickListener {
            textSize = 14f
            sizeChanged?.invoke(textSize)
            binding.includeTextSize.seekBar.progress = textSize.toInt()
        }

        binding.includeTextSize.tvTextTitle.setOnClickListener {
            textSize = 12f
            sizeChanged?.invoke(textSize)
            binding.includeTextSize.seekBar.progress = textSize.toInt()
        }

        binding.tvColor.setOnClickListener {
            setColorSelect()
        }

        binding.tvSize.setOnClickListener {
            setSizeSelect()
        }

        initHistoryList()
        initSeekBar()
        toggleFunc(TextConst.TextSize)
    }

    private fun setColorSelect() {
        toggleFunc(TextConst.TextColor)
        binding.includeHistoryColor.container.hide()
        binding.includeTextSize.container.hide()
    }

    private fun setSizeSelect() {
        toggleFunc(TextConst.TextSize)
    }


    private fun initSeekBar() {
        binding.includeTextSize.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = (if (progress < 1) 1 else progress)
                binding.includeTextSize.tvSize.text = "$size"
                textSize = size * 1f
                sizeChanged?.invoke(textSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.includeTextSize.seekBar.progress = (textSize).toInt()
    }

    private fun initHistoryList() {
        largeAdapter = binding.includeHistoryColor.rclHistoryBig.bindAdapter<HistoryColor, ModuleNoteItemHistoryColorBinding> {
            layoutManger = GridLayoutManager(mContext, 10)

            onBindView { itemViewHolder, itemData, position ->
                itemViewHolder.ivColor.setBackgroundColor(itemData.historyColor)
                if (textColor == itemData.historyColor) {
                    itemViewHolder.ivSelect.show()
                } else {
                    itemViewHolder.ivSelect.hide()
                }
            }

            onItemClick { itemViewHolder, itemData, position ->
                textColor = itemData.historyColor
                initCurrentPickColor()
                largeAdapter.notifyDataSetChanged()
            }

            addItemDecoration { outRect, view, parent, state ->
                val index = parent.getChildAdapterPosition(view)
                outRect.left = 0
                outRect.top = 0
                outRect.right = 0
                outRect.bottom = 0
                if (index > 9) {
                    outRect.top = SizeUtils.dp2px(13f)
                }
            }
        }


//        context.dbModel.loadAllHistory().observe(context) { list ->
//            if (list.isNotEmpty()) {
//                smallAdapter.submitList(list.take(3))
//                largeAdapter.submitList(list)
//            }
//            initCurrentPickColor(false)
//        }
    }


    fun initCurrentPickColor(need: Boolean = true) {
        val int2ArgbString = ColorUtils.int2ArgbString(textColor)
        binding.includeHistoryColor.tvHistoryColor.text = int2ArgbString
        binding.includeHistoryColor.ivvHistoryColor.setBackgroundColor(textColor)
        if (need) {
            //context.noteTextToolFragment.updateConfig(textColor, textSize.dp, textFont)
        }
    }


    private fun toggleFunc(textConst: TextConst) {
        when (textConst) {
            TextConst.TextColor -> {
                binding.tvColor.isSelected = true
                binding.tvSize.isSelected = false
            }

            TextConst.TextSize -> {
                binding.tvColor.isSelected = false
                binding.tvSize.isSelected = true
            }
        }
    }
}

sealed interface TextConst {
    object TextSize : TextConst
    object TextColor : TextConst
}