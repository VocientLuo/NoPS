package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.xluo.lib_base.adapter.ViewBindAdapter
import com.xluo.lib_base.adapter.bindAdapter

import com.xluo.nops.bean.shapePenList
import com.xluo.nops.bean.shapeSelectedPicMap
import com.xluo.nops.databinding.ItemShapePenBinding
import com.xluo.nops.databinding.PopShapeSelectBinding
import com.xluo.pen.bean.PaintBean

class PopShapeSelector : PopupWindow {

    private lateinit var binding: PopShapeSelectBinding

    private var mContext: Context

    var onPaintChanged: ((bean: PaintBean) -> Unit)? = null

    var lastIndex = 0

    private val shapeList = arrayListOf<PaintBean>()
    private var shapeAdapter: ViewBindAdapter<PaintBean, ItemShapePenBinding>? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    fun notifyIndex() {
        if (shapeList.isNotEmpty()) {
            onPaintChanged?.invoke(shapeList[lastIndex])
        }
    }

    private fun initView() {
        binding = PopShapeSelectBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root

        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true
        shapeList.addAll(shapePenList)
        initList()
    }

    private fun initList() {
        shapeAdapter = binding.rlHistory.bindAdapter(shapeList, null, true) {
            layoutManger = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            onBindView { itemViewHolder, itemData, position ->
                with(itemViewHolder) {
                    if (lastIndex == position) {
                        ivPenShape.setImageResource(shapeSelectedPicMap[itemData.id]!!)
                    } else {
                        //ivPenShape.setImageResource(itemData.pic)
                    }
                }
            }
            onItemClick { itemViewHolder, itemData, position ->
                lastIndex = position
                onPaintChanged?.invoke(itemData)
            }
        }
    }
}