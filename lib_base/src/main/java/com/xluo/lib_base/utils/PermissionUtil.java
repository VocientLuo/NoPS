package com.xluo.lib_base.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

public class PermissionUtil {
/**
 * 权限申请
 * api 'com.guolindev.permissionx:permissionx:1.6.0'
 */
    public static void requestPermisions(FragmentActivity activity, String[] permissions, RequestCallback callback) {
        PermissionX.init(activity)
                .permissions(permissions)
                .explainReasonBeforeRequest()
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "确定", "取消");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "去设置开启权限", "去设置", "取消");
                    }
                })
                .request(callback);
    }


    public static void requestPermisions(Fragment fragment, String[] permissions, RequestCallback callback) {
        PermissionX.init(fragment)
                .permissions(permissions)
                .explainReasonBeforeRequest()
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "确定", "取消");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "去设置开启权限", "去设置", "取消");
                    }
                })
                .request(callback);
    }
}
