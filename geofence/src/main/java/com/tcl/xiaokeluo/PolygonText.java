package com.tcl.xiaokeluo;

/**
 * @author xiaoke.luo@tcl.com 2019/5/15 17:48
 * 极端两点之前 要显示文字的坐标 大小  距离 角度
 */
public class PolygonText {
    /**
     * 左上点
     */
    public float leftTopX;
    public float leftTopY;
    /**
     * 右下点
     */
    public float rightBottomX;
    public float rightBottomY;

    /**
     * 两点之间的距离
     */
    public double distance;
    /**
     * 文字的宽度
     */
    public float textW, textH;
    /**
     * 文字需要旋转的角度
     */
    public float angelDegree;
    /**
     * 中心点
     */
    public float centerX, centerY;

    /**
     * 获取左上 右下点的位置 以及 两点之前的距离 文字的宽高
     */
    public void getValues(float x1, float y1, float x2, float y2) {
        centerX = (x1 + x2) / 2.0F;
        centerY = (y1 + y2) / 2.0F;
        if (x1 > x2) {
            float temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        //文字区域对角线的长度
        double rectDiagonal = Math.sqrt(Math.pow(textH, 2) + Math.pow(textW, 2));
        //对角线和 文字宽 的角度
        float rectDegree = Math.round(180 * Math.atan(textH * 1.0f / textW) / Math.PI);
        if (y1 < y2) {
            //右下方向

            //两个点的连线 与 x轴的角度
            angelDegree = Math.round(180 * Math.atan((y2 - y1) / (x2 - x1)) / Math.PI);
            //文字区域对角线  与 x轴的角度
            float mDegree = angelDegree + rectDegree;
            leftTopX = Math.round((centerX - Math.abs(rectDiagonal / 2.0f * Math.cos(mDegree))));
            leftTopY = Math.round((centerY - Math.abs(rectDiagonal / 2.0f * Math.sin(mDegree))));
            rightBottomX = Math.round((leftTopX + rectDiagonal * Math.cos(mDegree)));
            rightBottomY = Math.round((leftTopY + rectDiagonal * Math.sin(mDegree)));
        } else if (y1 == y2) {
            angelDegree = 0;
            leftTopX = centerX - textW / 2.0f;
            leftTopY = centerY - textH / 2.0f;
            rightBottomX = centerX + textW / 2.0f;
            rightBottomY = centerY + textH / 2.0f;
        } else {
            //右上方向

            //两个点的连线 与 x轴的角度
            angelDegree = Math.round(180 * Math.atan((y1 - y2) / (x2 - x1)) / Math.PI);
            //文字区域对角线  与 x轴的角度
            float mDegree = angelDegree - rectDegree;
            leftTopX = Math.round((centerX - Math.abs(rectDiagonal / 2.0f * Math.cos(mDegree))));
            leftTopY = Math.round((centerY + Math.abs(rectDiagonal / 2.0f * Math.sin(mDegree))));
            rightBottomX = Math.round((leftTopX + rectDiagonal * Math.cos(mDegree)));
            rightBottomY = Math.round((leftTopY + rectDiagonal * Math.sin(mDegree)));
            //旋转时 顺时针旋转 所以取负数
            angelDegree = -angelDegree;
        }
    }
}
