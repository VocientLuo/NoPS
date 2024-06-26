package com.xluo.nops.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;


import com.xluo.lib_base.listener.OnListClickListener;

import java.util.List;


public abstract class BaseSingleDragRVAdapter<T, V extends ViewBinding> extends RecyclerView.Adapter<BaseSingleDragRVAdapter.BaseViewHolder<V>> {

    protected List<T> data;
    protected Context context;
    protected OnListClickListener<T> onListClickListener;

    public BaseSingleDragRVAdapter(Context context, List<T> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder<V>(getViewBinding(viewType, LayoutInflater.from(context), parent));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<V> holder, int position) {
        bindView(holder,holder.binding, data.get(position), position);
    }

    public void setOnListClickListener(OnListClickListener<T> onListClickListener) {
        this.onListClickListener = onListClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /*
     *  获取   ViewBinding   实现类
     * */
    protected abstract V getViewBinding(int viewType, LayoutInflater from, ViewGroup parent);

    /*
     *  给ViewBinding   布局设置数据  点击事件等操作
     * */
    protected abstract void bindView(BaseViewHolder<V> holder,V binding, T entity, int position);


    public static class BaseViewHolder<V extends ViewBinding> extends RecyclerView.ViewHolder {

        public V binding;

        public BaseViewHolder(V binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public void refreshData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
