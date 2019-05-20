package com.tcl.xiaokeluo;

/**
 * @author xiaoke.luo@tcl.com 2019/5/15 17:48
 * 六边形 两点之间 要显示文字的坐标 大小  距离 角度 的参数
 */
public class PolygonTextParams {
    /**
     * 文字区域的左上点
     */
    public float leftTopX;
    public float leftTopY;
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

    public float x1, y1, x2, y2;

    /**
     * 获取左上 右下点的位置 以及 两点之前的距离 文字的宽高
     */
    public void getValues(float dx1, float dy1, float dx2, float dy2) {
        x1 = dx1;
        y1 = dy1;
        x2 = dx2;
        y2 = dy2;
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

        leftTopX = Math.round(x1 + (distance - textW) / 2.0f);
        leftTopY = Math.round(y1 - textH / 2.0f);

        if (y1 <= y2) {
            //右下方向
            //两个点的连线 与 x轴的角度
            angelDegree = Math.round(180 * Math.atan((y2 - y1) / (x2 - x1)) / Math.PI);
        } else {
            //右上方向
            //两个点的连线 与 x轴的角度
            angelDegree = Math.round(180 * Math.atan((y1 - y2) / (x2 - x1)) / Math.PI);

            //旋转时 顺时针旋转 所以取负数
            angelDegree = -angelDegree;
        }
    }
}
