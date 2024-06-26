package com.xluo.nops.adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import com.xluo.lib_base.adapter.BaseSingleRVAdapter
import com.xluo.nops.databinding.ItemPenShapeSelectBinding


class PenShapeSelectAdapter(context: Context, data: List<Int>) :
    BaseSingleRVAdapter<Int, ItemPenShapeSelectBinding>(context, data) {

        var onClick: ((Int) -> Unit)? = null
    var selectRes = -1

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater,
        parent: ViewGroup?
    ): ItemPenShapeSelectBinding {
        return ItemPenShapeSelectBinding.inflate(from, parent, false)
    }

    override fun bindView(
        binding: ItemPenShapeSelectBinding,
        entity: Int,
        position: Int
    ) {
        binding.ivPenShape.setImageResource(entity)
        binding.ivPenShape.isSelected = selectRes == entity
        binding.root.setOnClickListener {
            onClick?.invoke(entity)
        }
    }
}