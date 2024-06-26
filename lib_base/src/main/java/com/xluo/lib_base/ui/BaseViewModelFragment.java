package com.xluo.lib_base.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewbinding.ViewBinding;


/**
 * 为了兼容以前已经开发了的项目 添加第二种类型的，防止再以前模块导入的时候报错
 *
 * @param <T>
 * @param <B>
 */
public abstract class BaseViewModelFragment<T extends BaseViewModel, B extends ViewBinding> extends BaseFragment {

    private boolean isFirstLoad = true; // 是否第一次加载

    /**
     * 创建这个页面的viewmodel
     *
     * @return
     */
    protected abstract T createViewModel();

    /**
     * 返回这个页面的viewbinding对象
     *
     * @return
     */
    protected abstract B createViewBinding();

    /**
     * view初始化成功
     */
    protected abstract void initView(View containerView);

    protected abstract void initData();

    /**
     * 是否使用懒加载来初始化fragment
     *
     * @return
     */
    protected boolean isUseLazyLoad() {
        return true;
    }

    /**
     * 注册通用的ui改变监听使用
     */
    protected T model;

    protected B binding;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoad = true;
        binding = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = createViewBinding();
        if (binding == null) {
            throw new NullPointerException("binding is must not be null");
        }
        model = createViewModel();
        if (model == null) {
            throw new NullPointerException("ViewModel is must not be null");
        }
        initView(binding.getRoot());
        if (!isUseLazyLoad()) {
            //不使用懒加载 则就在创建view的时候去加载数据
            initData();
        }
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad && isUseLazyLoad()) {
            // 将数据加载逻辑放到onResume()方法中
            initData();
            isFirstLoad = false;
        }
    }


}
