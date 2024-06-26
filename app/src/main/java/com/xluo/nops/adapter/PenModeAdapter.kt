package com.xluo.nops.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.xluo.lib_base.adapter.BaseSingleRVAdapter
import com.xluo.nops.R
import com.xluo.nops.databinding.PopPenModeItemBinding
import com.xluo.pen.core.PenMixMode

class PenModeAdapter(context: Context, data: List<PenMixMode>) :
    BaseSingleRVAdapter<PenMixMode, PopPenModeItemBinding>(context, data) {

        var onClick: ((PenMixMode) -> Unit)? = null

    var index = 0

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater,
        parent: ViewGroup?
    ): PopPenModeItemBinding {
        return PopPenModeItemBinding.inflate(from, parent, false)
    }

    override fun bindView(
        binding: PopPenModeItemBinding,
        entity: PenMixMode,
        position: Int
    ) {
        binding.ivMode.setImageResource(entity.icon)
        binding.tvMode.text = entity.name
        if (index == position) {
            binding.ivSelect.setImageResource(R.drawable.icon_xuanzhong)
        } else {
            binding.ivSelect.setImageResource(0)
        }
        binding.layoutPaint.setOnClickListener {
            onClick?.invoke(entity)
            index = position
            notifyDataSetChanged()
        }
    }
}