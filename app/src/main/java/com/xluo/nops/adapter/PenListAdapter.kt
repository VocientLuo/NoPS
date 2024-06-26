package com.xluo.nops.adapter

import android.content.Context
import android.graphics.Color

import android.view.LayoutInflater
import android.view.ViewGroup
import com.xluo.lib_base.adapter.BaseSingleRVAdapter
import com.xluo.nops.R
import com.xluo.nops.databinding.PopPaintListItemBinding
import com.xluo.pen.PenManager
import com.xluo.pen.bean.PaintBean


/**
 * Create by AjjAndroid
 * Time:2022/3/8
 */
class PenListAdapter(context: Context, data: List<PaintBean>) :
    BaseSingleRVAdapter<PaintBean, PopPaintListItemBinding>(context, data) {

    var onClick: ((PaintBean) -> Unit)? = null

    private var selectedPenId = -1

    fun updateSelectedPen(id: Int) {
        if (selectedPenId != id) {
            selectedPenId = id
            notifyDataSetChanged()
        }
    }

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater,
        parent: ViewGroup?
    ): PopPaintListItemBinding {
        return PopPaintListItemBinding.inflate(from, parent, false)
    }

    override fun bindView(
        binding: PopPaintListItemBinding,
        entity: PaintBean,
        position: Int
    ) {
        binding.tvPaintName.text = "${entity.name}"
        if (entity.id == selectedPenId) {
            binding.tvPaintName.setTextColor(Color.parseColor("#ffffff"))
            binding.imagePaintType.setBackgroundResource(R.drawable.draw_huabi_item_bg_sel)
        } else {
            binding.tvPaintName.setTextColor(Color.parseColor("#999999"))
            binding.imagePaintType.setBackgroundResource(R.drawable.draw_huabi_item_bg)
        }
        // 这里不要用缓存pen对象，很麻烦。
        val pen = PenManager.getPen(entity, false)
        binding.imagePaintType.bindPen(pen)
        binding.layoutPaint.setOnClickListener {
            onClick?.invoke(entity)
            selectedPenId = entity.id
            notifyDataSetChanged()
        }
    }
}