package com.xluo.nops.adapter

import android.content.Context
import android.graphics.Point
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup

import com.blankj.utilcode.util.ScreenUtils
import com.xluo.core.utils.gone
import com.xluo.core.utils.visible
import com.xluo.lib_base.BaseApplication
import com.xluo.lib_base.adapter.BaseSingleRVAdapter
import com.xluo.lib_base.listener.OnListClickListener
import com.xluo.lib_base.utils.ImageLoadManager
import com.xluo.nops.R
import com.xluo.nops.databinding.CanvasDraftItemLayoutBinding
import com.xluo.nops.db.entity.DraftEntity


class DraftListAdapter(context: Context, data: List<DraftEntity>) :
    BaseSingleRVAdapter<DraftEntity, CanvasDraftItemLayoutBinding>(context, data) {

    /**
     * 存放每个item的宽高信息
     */
    val sizeMap = SparseArray<Point>()

    var onDraftDeleteListener: OnListClickListener<DraftEntity>? = null

    var longClickPosition = -1

    private val itemWidth =
        (ScreenUtils.getAppScreenWidth() - BaseApplication.instance.resources.getDimension(
            R.dimen.dp_44
        )) / 2

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater,
        parent: ViewGroup?
    ): CanvasDraftItemLayoutBinding {
        return CanvasDraftItemLayoutBinding.inflate(from, parent, false)
    }

    override fun bindView(
        binding: CanvasDraftItemLayoutBinding,
        entity: DraftEntity,
        position: Int
    ) {
        ImageLoadManager.loadForImageViewNoProxy(binding.ivCover, entity.coverPath)
        binding.tvDraftName.text = entity.name
        binding.ivCover.setOnClickListener {
            onListClickListener?.itemClick(position, entity)
        }
        binding.ivDelete.setOnClickListener {
            onDraftDeleteListener?.itemClick(position, entity)
        }
        binding.longActionView.setOnClickListener {
            longClickPosition = -1
            notifyDataSetChanged()
        }
        if (longClickPosition == position) {
            binding.longActionView.visible()
        } else {
            binding.longActionView.gone()
        }
        binding.ivCover.setOnLongClickListener {
            if (longClickPosition != position) {
                if (longClickPosition != -1) {
                    notifyItemChanged(longClickPosition, "load2")
                }
                longClickPosition = position
            }
            notifyItemChanged(position, "load")
            true
        }
    }

}