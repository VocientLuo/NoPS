package com.xluo.lib_base.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * 为了兼容以前已经开发了的项目 添加第二种类型的，防止再以前模块导入的时候报错
 * @param <T>
 * @param <B>
 */
public abstract class BaseViewModelActivity<T extends BaseViewModel, B extends ViewBinding> extends BaseActivity {

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
    protected abstract void initView();

    /**
     * 注册通用的ui改变监听使用
     */
    protected T model;

    protected B binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = createViewBinding();
        if (binding == null) {
            throw new NullPointerException("binding is must not be null");
        }
        setContentView(binding.getRoot());
        model = createViewModel();

        initView();
    }


}
