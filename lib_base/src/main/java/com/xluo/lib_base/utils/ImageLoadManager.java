package com.xluo.lib_base.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xluo.lib_base.R;

/**
 * 图片加载工具封装
 */
public class ImageLoadManager {

    public static void loadForImageView(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(new GlideUrlWrapper(url).getUrl())
                .apply(initRequestOptions())
                .into(imageView);
    }

    public static void loadCacheForImageView(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(new GlideUrlWrapper(url).getUrl())
                .apply(getCacheOptions())
                .into(imageView);
    }

    public static void loadForImageViewNoProxy(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(initRequestOptions())
                .into(imageView);
    }

    public static void loadForImageViewNoProxy(ImageView imageView, Uri uri) {
        Glide.with(imageView.getContext())
                .load(uri)
                .apply(initRequestOptions())
                .into(imageView);
    }

    public static void loadForImageView(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(new GlideUrlWrapper(url).getUrl())
                .apply(initRequestOptions())
                .into(imageView);
    }

    public static void loadForImageView(ImageView imageView, int resId) {
        Glide.with(imageView.getContext())
                .load(resId)
                .apply(initRequestOptions())
                .into(imageView);
    }

    private static RequestOptions requestOptions;

    private static RequestOptions initRequestOptions() {
        if (requestOptions == null) {
            requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        }
        return requestOptions;
    }


    private static RequestOptions cacheOptions;

    private static RequestOptions getCacheOptions() {
        if (cacheOptions == null) {
            cacheOptions = new RequestOptions()
                    .onlyRetrieveFromCache(true);

        }
        return cacheOptions;
    }


    public static void loadForViewGroup(final View view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .apply(initRequestOptions())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void loadForViewGroup(final View view, int resId) {
        Glide.with(view.getContext())
                .load(resId)
                .apply(initRequestOptions())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void loadForViewGroup(Context context, final View view, int resId) {
        Glide.with(context)
                .load(resId)
                .apply(initRequestOptions())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


}
