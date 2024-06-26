package com.xluo.lib_base.utils;

import android.text.TextUtils;

public class GlideUrlWrapper {

    private MyGlideUrl myGlideUrl;

    public GlideUrlWrapper(String url) {
        if (!TextUtils.isEmpty(url)) {
            myGlideUrl = new MyGlideUrl(url);
        }
    }

    public Object getUrl() {
        if (myGlideUrl != null) {
            return myGlideUrl;
        }
        return "";
    }

}
