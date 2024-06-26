package com.xluo.nops.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;

import com.xluo.lib_base.ui.BaseActivity;
import com.xluo.nops.bean.CanvasBean;
import com.xluo.nops.databinding.ActivityCanvasCustomSizeBinding;
import com.xluo.nops.utils.ScreenUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;

public class DrawZdyActivity extends BaseActivity {
    private static final int MAX_SIZE = 3000;
    private static final int MIN_SIZE = 20;
    private ActivityCanvasCustomSizeBinding binding;
    private int mwidth,mheight,mppi;
    private int screenWidth = 1080;
    private int screenHeight = 1920;
    private int mMaxLayerCount = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCanvasCustomSizeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.tvRange.setText("范围 (" + MIN_SIZE + "-" + MAX_SIZE + ")");
        //获取设备的分辨率
        ArrayList<Integer> size = ScreenUtils.Companion.getScreenSize(this);
        screenWidth = size.get(0);
        screenHeight = size.get(1);
        binding.etWidth.setText(screenWidth+"");
        binding.etHeight.setText(screenHeight+"");
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.etWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLayerCount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLayerCount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String width = binding.etWidth.getText().toString();
                String height = binding.etHeight.getText().toString();
                String ppi = binding.etPpi.getText().toString();
                if (width == null || width.isEmpty()||height == null||height.isEmpty()){
                    ToastUtils.showShort("请输入画布的宽高！");
                    return;
                }
                if (ppi == null||ppi.isEmpty()){
                    ToastUtils.showShort("请输入画布的像素大小！");
                    return;
                }
                if (width.length() > 4 || height.length() > 4) {
                    ToastUtils.showShort("不支持该尺寸，请重新设置");
                    return;
                }
                if (ppi.length() > 3) {
                    ToastUtils.showShort("不支持该ppi，请重新设置");
                    return;
                }
                mheight = Integer.parseInt(height);
                mwidth = Integer.parseInt(width);
                mppi = Integer.parseInt(ppi);
                if (mheight < MIN_SIZE || mheight > MAX_SIZE ||mwidth < MIN_SIZE || mwidth > MAX_SIZE){
                    ToastUtils.showShort("不支持该尺寸，请重新设置");
                    return;
                }
                if (mppi < 10||mppi > 600){
                    ToastUtils.showShort("不支持该ppi，请重新设置");
                    return;
                }
                mMaxLayerCount = ScreenUtils.Companion.getMaxLayerCount(mwidth, mheight);
                CanvasBean bean = new CanvasBean();
                bean.setWidth(mwidth);
                bean.setHeight(mheight);
                bean.setMaxLayerCount(mMaxLayerCount);
                DrawBoardActivity.Companion.startDraw(DrawZdyActivity.this, bean);
                finish();
            }
        });
        updateLayerCount();
    }

    private void updateLayerCount() {
        String width = binding.etWidth.getText().toString();
        String height = binding.etHeight.getText().toString();
        if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
            return;
        }
        try {
            if (height.length() > 5 || width.length() > 5) {
                // 禁止数值过大
                return;
            }
            mheight = Integer.parseInt(height);
            mwidth = Integer.parseInt(width);
            mMaxLayerCount = ScreenUtils.Companion.getMaxLayerCount(mwidth, mheight);
            binding.tvMaxLayer.setText(mMaxLayerCount + "");
        } catch (NumberFormatException exception) {
            LogUtils.e("number parse error");
        }
    }
}
