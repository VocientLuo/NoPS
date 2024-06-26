package com.xluo.nops.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.xluo.core.constants.Constants;
import com.xluo.core.entity.PvsBackgroundLayer;
import com.xluo.core.entity.PvsLayer;
import com.xluo.nops.R;

import java.util.ArrayList;


public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.VideoHolder> {

    private ArrayList<PvsLayer> mList;
    private Context mcontext;

    private OnItemClickListener mOnItemClickLitener;

    //定义点击接口
    public interface OnItemClickListener {
        void onShowClick(int position);
        void backgroundLayerImageOnClick();
        void layoutOnLongClick(int position);
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public LayerAdapter(Context mcontext, ArrayList<PvsLayer> mList) {
        this.mcontext = mcontext;
        this.mList = mList;
    }

    public void updateData(ArrayList<PvsLayer> mList) {
        this.mList = mList;
        freshListUI();
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.draw_item_layer, parent, false);
        return new VideoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, @SuppressLint("RecyclerView") final int position) {
        PvsLayer layer = mList.get(position);
        boolean show = layer.isShow();
        holder.tv_name.setText(layer.getName());
        holder.tv_nums.setText((position+1)+"");
        if (show) {
            holder.img_sel.setImageResource(R.mipmap.draw_aa_icon_xianshi);
        } else {
            holder.img_sel.setImageResource(R.mipmap.draw_aa_icon_yincang);
        }
        if (layer.isSelected()) {
            holder.layout.setBackgroundColor(mcontext.getResources().getColor(R.color.black));
        } else {
            holder.layout.setBackgroundColor(mcontext.getResources().getColor(R.color.draw_drawing_bg));
        }
        // 背景图层
        if (layer instanceof PvsBackgroundLayer) {
            PvsBackgroundLayer bgLayer = (PvsBackgroundLayer) layer;
            if (bgLayer.getBgType() == Constants.BG_TYPE_COLOR) {
                holder.img_show.setBackgroundColor(bgLayer.getBgColor());
                holder.img_show.setImageBitmap(null);
            } else {
                holder.img_show.setBackgroundColor(Color.parseColor("#00000000"));
                holder.img_show.setImageBitmap(bgLayer.getDecodeInfo().getBitmap());
            }
            holder.ivMore.setVisibility(View.GONE);
        } else {
            holder.ivMore.setVisibility(View.VISIBLE);
            holder.img_show.setImageBitmap(layer.getBitmap());
            holder.img_show.setBackgroundColor(Color.parseColor("#00000000"));
        }

        holder.img_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layer.isSelected()) {
                    mOnItemClickLitener.onShowClick(position);
                } else {
                    selectPosition(position);
                }
            }
        });

        holder.ivMore.setOnClickListener(v -> {
            if (!layer.isSelected()) {
                selectPosition(position);
            }else {
                PvsLayer pvsLayer = mList.get(position);
                if (position < mList.size() - 1 && pvsLayer.isSelected()) {
                    mOnItemClickLitener.layoutOnLongClick(position);
                }
            }
        });

        holder.img_show.setOnClickListener(v -> {
            if (position == mList.size() - 1) {
                mOnItemClickLitener.backgroundLayerImageOnClick();
            }else {
                if (!layer.isSelected()) {
                    selectPosition(position);
                }else {
                    PvsLayer pvsLayer = mList.get(position);
                    if (position < mList.size() - 2 && pvsLayer.isSelected()) {
                        mOnItemClickLitener.layoutOnLongClick(position);
                    }
                }
            }
        });
    }

    private void selectPosition(int position) {
        for (int i = 0; i < mList.size(); i++) {
            if (position == i) {
                mList.get(i).setSelected(true);
            } else {
                mList.get(i).setSelected(false);
            }
        }
        freshListUI();
    }

    private void freshListUI() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {


        private TextView tv_name,tv_nums;
        private ImageView img_show, img_sel, ivMore;
        private View layout;

        VideoHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            img_show = itemView.findViewById(R.id.img_show);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_nums = itemView.findViewById(R.id.tv_nums);
            img_sel = itemView.findViewById(R.id.img_sel);
            ivMore = itemView.findViewById(R.id.ivMore);
        }
    }
}