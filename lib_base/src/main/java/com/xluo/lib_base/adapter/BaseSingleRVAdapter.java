package com.xluo.lib_base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.xluo.lib_base.listener.OnListClickListener;

import java.util.List;

/**
 * ===============================================
 * Create by kongbaige on 2021/4/19
 * Email 1531603384@qq.com
 * ===============================================
 * 针对多布局使用,不包含特殊的添加了header和footer的布局
 */
public abstract class BaseSingleRVAdapter<T, V extends ViewBinding> extends RecyclerView.Adapter<BaseSingleRVAdapter.BaseViewHolder<V>> {

    protected List<T> data;
    protected Context context;
    protected OnListClickListener<T> onListClickListener;

    public BaseSingleRVAdapter(Context context, List<T> data) {
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
        bindView(holder.binding, data.get(position), position);
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
    protected abstract void bindView(V binding, T entity, int position);


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
