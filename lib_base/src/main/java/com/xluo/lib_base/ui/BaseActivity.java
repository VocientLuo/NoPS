package com.xluo.lib_base.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xluo.lib_base.view.CustomProgressDialog;


public class BaseActivity extends AppCompatActivity {

    CustomProgressDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isHideActionBar()) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
    }

    public boolean isHideActionBar() {
        return true;
    }

    /**
     * 显示系统等待框
     */
    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new CustomProgressDialog(this);
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 关闭等待框
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

}
