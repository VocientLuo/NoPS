package com.xluo.nops.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CanvasBean implements Parcelable {
    private int pic;
    private String size;
    private int width;
    private int height;
    private int maxLayerCount;
    private String imgPath;

    public CanvasBean() {
    }

    public CanvasBean(Parcel in) {
        pic = in.readInt();
        size = in.readString();
        width = in.readInt();
        height = in.readInt();
        maxLayerCount = in.readInt();
        imgPath = in.readString();
    }

    public static final Creator<CanvasBean> CREATOR = new Creator<CanvasBean>() {
        @Override
        public CanvasBean createFromParcel(Parcel in) {
            return new CanvasBean(in);
        }

        @Override
        public CanvasBean[] newArray(int size) {
            return new CanvasBean[size];
        }
    };

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getMaxLayerCount() {
        return maxLayerCount;
    }

    public void setMaxLayerCount(int maxLayerCount) {
        this.maxLayerCount = maxLayerCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(pic);
        parcel.writeString(size);
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeInt(maxLayerCount);
        parcel.writeString(imgPath);
    }
}
