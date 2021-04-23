package com.tiger.platedetection.Process;

public class PlateInfo{


    private String namePlate;
    private int top;
    private int left;
    private int bottom;
    private int right;
    private boolean show;


    public PlateInfo(String namePlate, int top, int left, int bottom, int right) {
        this.namePlate = namePlate;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.show = false;
    }

    public String getNamePlate() {
        return namePlate;
    }

    public void setNamePlate(String namePlate) {
        this.namePlate = namePlate;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}


