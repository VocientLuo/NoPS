package com.xluo.nops.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xluo.nops.bean.Size;
import com.xluo.nops.R;

import java.util.ArrayList;

public class PaintSizeAdapter extends RecyclerView.Adapter<PaintSizeAdapter.VideoHolder> {

    private ArrayList<Size> mList;

    private OnItemClickLitener mOnItemClickLitener;


    //定义点击接口
    public interface OnItemClickLitener {
        void onItemClick(int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public PaintSizeAdapter(Context mcontext, ArrayList<Size> mList) {
        this.mList = mList;

    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_canvas_create, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, @SuppressLint("RecyclerView") final int position) {
        Size size = mList.get(position);
        holder.view.setImageResource(size.getResId());
        holder.tv_size.setText(size.getName());
        if (size.getHeight() != size.getRealHeight()) {
            holder.tv_chicun.setText(size.getWidth()+"x"+size.getHeight()+"mm");
        } else {
            holder.tv_chicun.setText(size.getWidth()+"x"+size.getHeight()+"px");
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(position);
            }
        });
        holder.tvLayerCount.setText(size.getMaxLayer() + "图层");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {


        private ImageView view;
        private TextView tv_size,tv_chicun, tvLayerCount;
        private LinearLayout layout;


        VideoHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            view = itemView.findViewById(R.id.image_size);
            tv_size = itemView.findViewById(R.id.tv_size_bili);
            tv_chicun = itemView.findViewById(R.id.tv_size_chicun);
            tvLayerCount = itemView.findViewById(R.id.tv_layer_count);
        }
    }
}