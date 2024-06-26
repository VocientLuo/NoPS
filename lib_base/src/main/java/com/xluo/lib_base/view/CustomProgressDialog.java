package com.xluo.lib_base.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xluo.lib_base.R;


/**
 * 自定义等待对话框，带提示
 */
public class CustomProgressDialog extends Dialog {

    private String tips = "请等待...";
    private TextView tvProgress;
    private LinearLayout cancelContainer;
    private boolean isShowProgressValue = false;


    public CustomProgressDialog(Context context) {
        super(context, R.style.StyleBaseDialog);
    }


    public CustomProgressDialog(Context context, String tips) {
        super(context, R.style.StyleBaseDialog);
        this.tips = tips;
    }

    /**
     * @param context
     * @param tips
     * @param isShowProgressValue 是否显示进度值
     */
    public CustomProgressDialog(Context context, String tips, boolean isShowProgressValue) {
        super(context, R.style.StyleBaseDialog);
        this.tips = tips;
        this.isShowProgressValue = isShowProgressValue;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog_layout);
        TextView tvTips = findViewById(R.id.tvTips);
        tvTips.setText(tips);
        tvProgress = findViewById(R.id.tvProgress);
        if (isShowProgressValue) {
            tvProgress.setVisibility(View.VISIBLE);
        } else {
            tvProgress.setVisibility(View.GONE);
        }

    }

    public void setProgress(int progress) {
        if (tvProgress != null) {
            tvProgress.setText(progress + "%");
        }
    }

}
