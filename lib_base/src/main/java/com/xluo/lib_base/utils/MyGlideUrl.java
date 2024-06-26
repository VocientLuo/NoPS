package com.xluo.lib_base.utils;

import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;

public class MyGlideUrl extends GlideUrl {

    private static final String TAG = MyGlideUrl.class.getSimpleName();
    private String mUrl;

    public MyGlideUrl(String url) {
        super(url);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        if (TextUtils.isEmpty(mUrl)) {
            return super.getCacheKey();
        }
        int index = mUrl.indexOf("?");
        if (index < 0) {
            return super.getCacheKey();
        }
        try {
            String subStr = mUrl.substring(index + 1);
            if (subStr.contains("auth_key=")) {
                String keyStr = mUrl.substring(0, index);
                return keyStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getCacheKey();
    }
}
