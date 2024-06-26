package com.xluo.nops.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.xluo.core.constants.Constants
import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsLayer
import com.xluo.nops.adapter.BaseSingleDragRVAdapter

import com.xluo.nops.adapter.LayerManagerAdapter
import com.xluo.nops.databinding.PopLayerListBinding
import com.xluo.nops.operation.OperationUtils
import com.xluo.nops.operation.TimeLineData
import com.xluo.nops.utils.LayerSortTouchHelper

class PopLayerList: PopupWindow {

    private lateinit var binding: PopLayerListBinding

    private var mContext: Context

    private var actionCreate: (() -> Unit)? = null

    private var actionVisible: ((position: Int) -> Unit)? = null

    private var backgroundLayerImageOnClick: (() -> Unit)? = null

    private var layoutOnLongClick: ((position: Int) -> Unit)? = null

    var bgLayerChanged: (() -> Unit)? = null

    private lateinit var adapter: LayerManagerAdapter

    private var mLayerListData = arrayListOf<PvsLayer>()
    private var oldLayerList = arrayListOf<PvsLayer>()

    constructor(context: Context): super(context) {
        mContext = context
        initView()
    }

    fun onCreateAction(action: () -> Unit) {
        actionCreate = action
    }

   fun onVisibleAction(action: (position: Int) -> Unit) {
        actionVisible = action
    }

    fun onDataChanged(layerList: List<PvsLayer>) {
        mLayerListData.clear()
        mLayerListData.addAll(layerList)
        adapter.refreshData(mLayerListData)
    }

    fun backgroundLayerImageOnClick(action: () -> Unit){
        backgroundLayerImageOnClick = action
    }

    fun layoutOnLongClick(action: (position: Int) -> Unit){
        layoutOnLongClick = action
    }

    private fun initView() {
        binding = PopLayerListBinding.inflate(LayoutInflater.from(mContext))
        contentView = binding.root
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true
        isTouchable = true

        binding.layoutLayerCancle.setOnClickListener {
            dismiss()
        }
        binding.llEmpty.setOnClickListener {
            dismiss()
        }
        binding.tvLayerOk.setOnClickListener {
            actionCreate?.invoke()
        }
        binding.layerList.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        adapter = LayerManagerAdapter(mContext, mLayerListData)
        binding.layerList.adapter = adapter
        adapter.setOnItemClickLitener(object: LayerManagerAdapter.OnItemClickListener {
            override fun onShowClick(position: Int) {
                if (mLayerListData[position] is PvsBackgroundLayer) {
                    mLayerListData[position].isShow = !mLayerListData[position].isShow
                    bgLayerChanged?.invoke()
                    adapter.notifyDataSetChanged()
                } else {
                    val index = TimeLineData.findOldLayerIndex(mLayerListData[position].objectId)
                    // 没找到对应图层则不回调
                    if (index == -1) {
                        dismiss()
                        return
                    }
                    actionVisible?.invoke(index)
                }
            }

            override fun backgroundLayerImageOnClick() {
                backgroundLayerImageOnClick?.invoke()
            }

            override fun layoutOnLongClick(position: Int) {
                val index = TimeLineData.findOldLayerIndex(mLayerListData[position].objectId)
                // 没找到对应图层则不回调
                if (index == -1) {
                    dismiss()
                    return
                }
                layoutOnLongClick?.invoke(index)
            }
        })
        val layerSortTouchHelper = LayerSortTouchHelper(adapter, mLayerListData)
        layerSortTouchHelper.setMovePositionCallBack(object : LayerSortTouchHelper.MovePositionCallBack {
            override fun onTriggerMove() {

            }

            override fun onMoved(startPosition: Int, targetPosition: Int) {
                mLayerListData.let {
                    val list = arrayListOf<PvsLayer>()
                    var count = it.size-1
                    while (count >= 0) {
                        if (it[count].type() != Constants.LAYER_TYPE_BG) {
                            list.add(it[count])
                        }
                        count--
                    }
                    OperationUtils.sortAllLayer(oldLayerList, list)
                }
            }
        })
        val itemTouchHelper = ItemTouchHelper(layerSortTouchHelper)
        adapter.dragListener = object : LayerManagerAdapter.DragListener {
            override fun startDrag(holder: BaseSingleDragRVAdapter.BaseViewHolder<*>) {
                TimeLineData.pvsTimeLine?.layerList?.let {
                    oldLayerList = arrayListOf()
                    oldLayerList.addAll(it)
                }
                itemTouchHelper.startDrag(holder)
            }
        }
        itemTouchHelper.attachToRecyclerView(binding.layerList)
    }
}