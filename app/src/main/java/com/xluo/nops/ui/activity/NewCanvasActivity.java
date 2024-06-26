package com.xluo.nops.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.xluo.lib_base.ui.BaseActivity;
import com.xluo.nops.adapter.PaintSizeAdapter;
import com.xluo.nops.bean.CanvasBean;
import com.xluo.nops.bean.CanvasSizeDataKt;
import com.xluo.nops.bean.Size;
import com.xluo.nops.utils.PermissUtils;
import com.xluo.nops.databinding.ActivityCanvasCreateBinding;
import com.xluo.nops.utils.GlideEngine;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class NewCanvasActivity extends BaseActivity {
    private ActivityCanvasCreateBinding binding;
    private RecyclerView recyclerView;
    private PaintSizeAdapter adapter;
    private ArrayList<Size> dataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCanvasCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.sizeList.setLayoutManager(manager);
        dataList = CanvasSizeDataKt.getNewCanvasSizeList(this);
        adapter = new PaintSizeAdapter(this, dataList);
        binding.sizeList.setAdapter(adapter);
        adapter.setOnItemClickLitener(new PaintSizeAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(int position) {
                Size size = dataList.get(position);
                CanvasBean bean = new CanvasBean();
                bean.setMaxLayerCount(size.getMaxLayer());
                bean.setWidth(size.getRealWith());
                bean.setHeight(size.getRealHeight());
                startCreateCanvas(bean);
            }
        });
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.layoutZdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewCanvasActivity.this, DrawZdyActivity.class));
                finish();
            }
        });

        binding.layoutPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPermission();
            }
        });
    }

    /**
     * 选择图片
     */
    private void chosePic() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {

                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result == null || result.size() == 0) {
                            return;
                        }
                        String path = result.get(0).getRealPath();
                        CanvasBean bean = new CanvasBean();
                        bean.setWidth(result.get(0).getWidth());
                        bean.setHeight(result.get(0).getHeight());
                        bean.setImgPath(path);
                        startCreateCanvas(bean);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void startCreateCanvas(CanvasBean bean) {
        DrawBoardActivity.Companion.startDraw(NewCanvasActivity.this, bean);
        finish();
    }

    /**
     * 申请权限 默认申请读取手机信息 和读写的权限
     */
    public void applyPermission() {
        PermissUtils.INSTANCE.getPicturePermission(this, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                chosePic();
                return null;
            }
        });
    }
}
