package com.xluo.nops.adapter


import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xluo.core.constants.Constants
import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsLayer
import com.xluo.nops.R
import com.xluo.nops.databinding.DrawItemLayerBinding


class LayerManagerAdapter(context: Context, menuList: List<PvsLayer>) : BaseSingleDragRVAdapter<PvsLayer, DrawItemLayerBinding>(context, menuList) {
    var dragListener: DragListener? = null

    private var mOnItemClickLitener: OnItemClickListener? = null

    //定义点击接口
    interface OnItemClickListener {
        fun onShowClick(position: Int)
        fun backgroundLayerImageOnClick()
        fun layoutOnLongClick(position: Int)
    }

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.mOnItemClickLitener = mOnItemClickLitener
    }

    override fun getViewBinding(viewType: Int, from: LayoutInflater, parent: ViewGroup?): DrawItemLayerBinding {
        return DrawItemLayerBinding.inflate(from, parent, false)
    }

    override fun bindView(holder: BaseViewHolder<DrawItemLayerBinding>, binding: DrawItemLayerBinding, entity: PvsLayer, position: Int) {
        binding.tvName.text = entity.name
        binding.tvNums.text = "${data.size - position}"
        if (entity.isShow) {
            binding.imgSel.setImageResource(R.mipmap.draw_aa_icon_xianshi)
        } else {
            binding.imgSel.setImageResource(R.mipmap.draw_aa_icon_yincang)
        }
        if (entity.isSelected) {
            binding.layout.setBackgroundColor(context.resources.getColor(R.color.black))
        } else {
            binding.layout.setBackgroundColor(context.resources.getColor(R.color.draw_drawing_bg))
        }
        if (entity is PvsBackgroundLayer) {
            if (entity.bgType == Constants.BG_TYPE_COLOR) {
                binding.imgShow.setBackgroundColor(entity.bgColor)
                binding.imgShow.setImageBitmap(null)
            } else {
                binding.imgShow.setBackgroundColor(Color.parseColor("#00000000"))
                binding.imgShow.setImageBitmap(entity.decodeInfo!!.bitmap)
            }
            binding.ivMore.visibility = View.GONE
        } else {
            binding.ivMore.visibility = View.VISIBLE
            binding.imgShow.setImageBitmap(entity.bitmap)
            binding.imgShow.setBackgroundColor(Color.parseColor("#00000000"))
        }
        binding.imgSel.setOnClickListener {
            if (entity.isSelected) {
                mOnItemClickLitener?.onShowClick(position)
            } else {
                selectPosition(position)
            }
        }
        binding.ivMore.setOnClickListener {
            if (!entity.isSelected) {
                selectPosition(position)
            } else {
                if (position < data.size - 1) {
                    mOnItemClickLitener?.layoutOnLongClick(position)
                }
            }
        }
        binding.imgShow.setOnClickListener {
            if (position == data.size-1) {
                mOnItemClickLitener?.backgroundLayerImageOnClick()
            } else {
                if (!entity.isSelected) {
                    selectPosition(position)
                } else {
                    if (position < data.size-2) {
                        mOnItemClickLitener?.layoutOnLongClick(position)
                    }
                }
            }
        }
        binding.root.setOnLongClickListener {
            if (entity !is PvsBackgroundLayer) {
                dragListener?.startDrag(holder)
            }
            return@setOnLongClickListener true
        }
        binding.imgSel.setOnLongClickListener {
            if (entity !is PvsBackgroundLayer) {
                dragListener?.startDrag(holder)
            }
            return@setOnLongClickListener true
        }
        binding.ivMore.setOnLongClickListener {
            if (entity !is PvsBackgroundLayer) {
                dragListener?.startDrag(holder)
            }
            return@setOnLongClickListener true
        }
        binding.imgShow.setOnLongClickListener {
            if (entity !is PvsBackgroundLayer) {
                dragListener?.startDrag(holder)
            }
            return@setOnLongClickListener true
        }
    }

    private fun selectPosition(position: Int) {
        for (i in data.indices) {
            data[i].isSelected = position == i
        }
        Handler().post {
            notifyDataSetChanged()
        }
    }
    interface DragListener {
        fun startDrag(holder: BaseViewHolder<*>)
    }
}