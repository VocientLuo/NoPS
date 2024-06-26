package com.xluo.lib_base.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xluo.lib_base.R;

/**
 * 通用提示选择对话框
 */
public class CommonTipsDialog extends Dialog {

    private TextView tvContent;
    private TextView tvCancel;
    private TextView tvConfirm;

    private String content = "";
    private String cancelName = "取消";
    private String confirmName = "确定";

    private View.OnClickListener cancelListener;
    private View.OnClickListener confirmListener;
    //是否点击按钮时自动关闭对话框
    private boolean isAutoDismiss = true;


    public CommonTipsDialog(@NonNull Context context, String content) {
        super(context, R.style.StyleBaseDialog);
        this.content = content;
    }

    public CommonTipsDialog(@NonNull Context context, String content, String cancelName, String confirmName) {
        super(context, R.style.StyleBaseDialog);
        this.content = content;
        this.cancelName = cancelName;
        this.confirmName = confirmName;
    }


    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setAutoDismiss(boolean autoDismiss) {
        isAutoDismiss = autoDismiss;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common_tips_layout);
        initView();
    }


    private void initView() {
        tvContent = findViewById(R.id.tvContent);
        tvCancel = findViewById(R.id.tvCancel);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvCancel.setText(cancelName);
        tvConfirm.setText(confirmName);
        tvContent.setText(content);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoDismiss) {
                    dismiss();
                }
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoDismiss) {
                    dismiss();
                }
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
    }


}

