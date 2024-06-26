package com.xluo.nops.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.xluo.core.entity.PvsLayer;
import com.xluo.nops.adapter.LayerManagerAdapter;

import java.util.Collections;
import java.util.List;

/**
 * 自定义视频排序拖动处理
 */
public class LayerSortTouchHelper extends ItemTouchHelper.Callback {
    private String TAG = getClass().getName();

    private LayerManagerAdapter baseAdapter;
    private List<PvsLayer> pvsLayerList;
    private int startPosition = -1;
    private int targetPosition = -1;
    private MovePositionCallBack movePositionCallBack;

    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;


    public LayerSortTouchHelper(LayerManagerAdapter baseAdapter, List<PvsLayer> pvsLayerList) {
        this.baseAdapter = baseAdapter;
        this.pvsLayerList = pvsLayerList;
    }

    public void setMovePositionCallBack(MovePositionCallBack movePositionCallBack) {
        this.movePositionCallBack = movePositionCallBack;
    }

    /**
     * 设置是否能长按拖动
     *
     * @param canDrag
     */
    public void setCanDrag(boolean canDrag) {
        isCanDrag = canDrag;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags;//GridLayoutManager可拖动的方向分为上 下 左 右 LinearLayoutManager可拖动的方向分为上 下
        int swipFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipFlags = 0;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipFlags = 0;
        }
        return makeMovementFlags(dragFlags, swipFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAbsoluteAdapterPosition();//得到拖动ViewHolder的position
        int toPosition = target.getAbsoluteAdapterPosition();//得到目标ViewHolder的position
        targetPosition = toPosition;
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                if (pvsLayerList.size() > i+1) {
                    Collections.swap(pvsLayerList, i, i + 1);
                }
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                if (pvsLayerList.size() > 1) {
                    Collections.swap(pvsLayerList, i, i - 1);
                }
            }
        }
        baseAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (movePositionCallBack != null) {
                movePositionCallBack.onTriggerMove();
            }
            if (startPosition == -1) {
                startPosition = viewHolder.getAdapterPosition();
            }
            //if (viewHolder instanceof BaseSingleRVAdapter.BaseViewHolder) {
            //    LayerManagerAdapter.BaseViewHolder holder = (LayerManagerAdapter.BaseViewHolder) viewHolder;
            //    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            //    layoutParams.topMargin = -DensityUtil.dip2px(mActivity, 10);
            //    holder.itemView.setLayoutParams(layoutParams);
            //    ItemVideoSortLayoutBinding binding = (ItemVideoSortLayoutBinding) holder.binding;
            //    binding.vSelectStatus.setVisibility(View.VISIBLE);
            //}
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //if (viewHolder instanceof VideoSortAdapter.BaseViewHolder) {
        //    VideoSortAdapter.BaseViewHolder holder = (VideoSortAdapter.BaseViewHolder) viewHolder;
        //    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
        //    layoutParams.topMargin = 0;
        //    holder.itemView.setLayoutParams(layoutParams);
        //    ItemVideoSortLayoutBinding binding = (ItemVideoSortLayoutBinding) holder.binding;
        //    binding.vSelectStatus.setVisibility(View.GONE);
        //}
        //baseAdapter.setSelectPosition(-1);
        Log.d(TAG, "移动的参数：" + startPosition + "------->" + targetPosition);
        if (movePositionCallBack != null && startPosition != targetPosition && targetPosition != -1) {
            movePositionCallBack.onMoved(startPosition, targetPosition);
        }
        startPosition = -1;
        targetPosition = -1;
        super.clearView(recyclerView, viewHolder);
    }

    public interface MovePositionCallBack {

        /**
         * 触发了拖动操作
         */
        void onTriggerMove();

        void onMoved(int startPosition, int targetPosition);
    }

}
