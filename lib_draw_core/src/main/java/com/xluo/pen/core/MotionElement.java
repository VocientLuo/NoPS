package com.xluo.pen.core;

/**
 * Created by xluo on 2023/8/18.
 */
public class MotionElement {

        public float x;
        public float y;
        //压力值  物理设备决定的，和设计的设备有关系，在此Demo中没有用到 ，但是这个坑  记录下
        public float pressure = 1.0f;
        public float size = 1.0f;
        //绘制的工具是否是手指或者是笔（触摸笔）
        public int tooltype;

        public MotionElement(float mx, float my, float mp, float size, int ttype) {
            x = mx;
            y = my;
            pressure = mp;
            this.size = size;
            tooltype = ttype;
        }

    @Override
    public String toString() {
        return "MotionElement{" +
                "x=" + x +
                ", y=" + y +
                ", pressure=" + pressure +
                ", size=" + size +
                ", tooltype=" + tooltype +
                '}';
    }
}
