package com.xluo.core.entity;

import android.graphics.Path;

public class EraseEntity {
    private Path path;
    private int paintSize;
    private int alpha;
    private PvsLayer pvsLayer;

    public Path getPath() {
        return path;
    }

    public EraseEntity(Path path, int size, int alpha, PvsLayer layer) {
        this.path = path;
        this.paintSize = size;
        this.alpha = alpha;
        this.pvsLayer = layer;
    }

    public int getPaintSize() {
        return paintSize;
    }

    public void setPaintSize(int paintSize) {
        this.paintSize = paintSize;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public PvsLayer getPvsLayer() {
        return pvsLayer;
    }

    public void setPvsLayer(PvsLayer pvsLayer) {
        this.pvsLayer = pvsLayer;
    }
}
