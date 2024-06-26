package com.xluo.nops.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.xluo.lib_base.adapter.BaseSingleRVAdapter
import com.xluo.nops.databinding.PopPaintTypeItemBinding
import com.xluo.pen.bean.PaintType

class PenTypeAdapter(context: Context, data: List<PaintType>) :
    BaseSingleRVAdapter<PaintType, PopPaintTypeItemBinding>(context, data) {
    var index = 0

    var onClick: ((Int) -> Unit)? = null

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater,
        parent: ViewGroup?
    ): PopPaintTypeItemBinding {
        return PopPaintTypeItemBinding.inflate(from, parent, false)
    }

    override fun bindView(
        binding: PopPaintTypeItemBinding,
        entity: PaintType,
        position: Int
    ) {
        binding.tvPaintType.text = "${entity.name}"
        if (position == index) {
            binding.tvPaintType.setTextColor(Color.WHITE)
        } else {
            binding.tvPaintType.setTextColor(Color.parseColor("#999999"))
        }
        binding.layoutPaint.setOnClickListener {
            index = position
            onClick?.invoke(position)
            notifyDataSetChanged()
        }
    }
}