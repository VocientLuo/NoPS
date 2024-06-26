package com.xluo.pen.bean;

/**
 * Created by xluo on 2023/4/2.
 */
public class ControllerPoint {
    public float x;
    public float y;

    // 当前点的压力值[0,1]
    public float p;

    public int primevalPointIndex = -1;
    public float width;
    public int alpha = 255;
    public float size;

    public ControllerPoint() {
    }

    public ControllerPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public ControllerPoint(float x, float y, float p) {
        this.x = x;
        this.y = y;
        this.p = p;
    }

    public void set(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.width = w;
    }


    public void set(ControllerPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.size = point.size;
        this.width = point.width;
    }


    public String toString() {
        String str = "X = " + x + "; Y = " + y + "; W = " + width;
        return str;
    }

    public ControllerPoint getMirroredPoint(ControllerPoint pt) {
        return new ControllerPoint(
                x + 2*(pt.x -x),
                y + 2*(pt.y-y),
                p + 2*(pt.p-p)
        );
    }

    public double getDistance(ControllerPoint pt) {
        float dx = x - pt.x;
        float dy = y - pt.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public ControllerPoint getMidPoint(ControllerPoint pt) {
        return new ControllerPoint(
                (x + pt.x)/2,
                (y + pt.y)/2,
                (p + pt.p)/2
        );
    }
}
