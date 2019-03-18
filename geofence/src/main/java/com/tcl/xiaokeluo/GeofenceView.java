package com.tcl.xiaokeluo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * create by xiaoke.luo@tcl.com 2019/2/11 17:25
 * 安全范围自定义view
 */
public class GeofenceView extends View {

    /**
     * 圆形
     */
    public static final int TYPE_PROXIMITY = 1;
    /**
     * 六边形
     */
    public static final int TYPE_POLYGON = 2;
    /**
     * 当前的图形类别
     */
    private int mType;
    /**
     * 对应的画笔
     */
    private Paint linePaint, dotPaint;
    /**
     * 半径、线宽
     */
    private int dotRadius, tempDotRadius, lineWidth;
    /**
     * 半径（圆形） 两点之前的距离（正六边形）
     */
    private int mRadius, circleRadius;


    private Path path;
    /**
     * 圆形 和  六边形对应的点坐标
     * -     1
     * -  0     2
     * -     3
     * -
     * -     1    2
     * -
     * -  0          3
     * -
     * -     5    4
     */
    private float[][] arrayProximity, arrayPolygon;

    /**
     * 区域遮罩颜色
     */
    private Shader mShader;

    /**
     * 颜色
     */
    private int lineColor, dotLineColor;
    /**
     * 是否能拖动点
     * true 是
     * false 否
     */
    private boolean statueMove;
    /**
     * 拖动点的下标
     */
    private int moveDotIndex = -1;
    /**
     * 地图的缩放级别
     */
    private int mapZoom;

    public GeofenceView(Context context) {
        this(context, null);
    }

    public GeofenceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GeofenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeArray(context, attrs);
        init();
    }

    /**
     * 获取自定义属性值
     */
    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GeofenceView);
        mType = ta.getInt(R.styleable.GeofenceView_gv_type, TYPE_PROXIMITY);
        tempDotRadius = dotRadius = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_dot_radius, 16);
        circleRadius = mRadius = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_radius, 200);
        dotLineColor = ta.getColor(R.styleable.GeofenceView_gv_dot_line_color, 0xFF398EFF);
        lineColor = ta.getColor(R.styleable.GeofenceView_gv_line_color, 0xFF398EFF);
        lineWidth = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_line_width, 5);
        ta.recycle();
    }

    /**
     * 初始化
     */
    private void init() {
        mapZoom = 1;
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mShader = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                new int[]{0x1A398EFF, 0x1A398EFF}, null, Shader.TileMode.CLAMP);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineColor);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStrokeWidth(lineWidth);

        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initDotArray();
    }

    /**
     * 初始化 圆形 和 六边形 对应的六个点
     */
    private void initDotArray() {
        arrayProximity = new float[4][2];
        arrayProximity[0][0] = getMeasuredWidth() / 2 - circleRadius;
        arrayProximity[0][1] = getMeasuredHeight() / 2;
        arrayProximity[1][0] = getMeasuredWidth() / 2;
        arrayProximity[1][1] = getMeasuredHeight() / 2 - circleRadius;
        arrayProximity[2][0] = getMeasuredWidth() / 2 + circleRadius;
        arrayProximity[2][1] = getMeasuredHeight() / 2;
        arrayProximity[3][0] = getMeasuredWidth() / 2 + circleRadius;
        arrayProximity[3][1] = getMeasuredHeight() / 2 + circleRadius;

        arrayPolygon = new float[6][2];
        arrayPolygon[0][0] = getMeasuredWidth() / 2 - circleRadius;
        arrayPolygon[0][1] = getMeasuredHeight() / 2;
        arrayPolygon[1][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3));
        arrayPolygon[1][1] = getMeasuredHeight() / 2 - (int) (circleRadius * Math.sin(Math.PI / 3));
        arrayPolygon[2][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3)) + circleRadius;
        arrayPolygon[2][1] = getMeasuredHeight() / 2 - (int) (circleRadius * Math.sin(Math.PI / 3));
        arrayPolygon[3][0] = getMeasuredWidth() / 2 + circleRadius;
        arrayPolygon[3][1] = getMeasuredHeight() / 2;
        arrayPolygon[4][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3)) + circleRadius;
        arrayPolygon[4][1] = getMeasuredHeight() / 2 + (int) (circleRadius * Math.sin(Math.PI / 3));
        arrayPolygon[5][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3));
        arrayPolygon[5][1] = getMeasuredHeight() / 2 + (int) (circleRadius * Math.sin(Math.PI / 3));
    }

    /**
     * 设置地图缩放级别
     *
     * @param mapZoom 地图缩放
     */
    public void setMapZoom(int mapZoom) {
        this.mapZoom = mapZoom;
        circleRadius = mRadius / mapZoom;
        tempDotRadius = dotRadius / mapZoom;
        initDotArray();
        invalidate();
    }

    /**
     * 设置展示形状
     */
    public void setType(@GEOFENCETYPE int mType) {
        this.mType = mType;
        circleRadius = mRadius / mapZoom;
        tempDotRadius = dotRadius / mapZoom;
        initDotArray();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mType == TYPE_PROXIMITY) {
            drawProximity(canvas);
        } else {
            drawPolygonLine(canvas);
            drawPolygonDot(canvas);
        }
    }

    /**
     * 设置圆形的半径
     */
    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius / mapZoom;
        tempDotRadius = dotRadius / mapZoom;
        if (!isPolygon()) {
            invalidate();
        }
    }

    /**
     * 画圆形相关的
     */
    private void drawProximity(Canvas canvas) {
        linePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, circleRadius, linePaint);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setShader(mShader);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, circleRadius, linePaint);
        linePaint.setShader(null);
    }

    /**
     * 画六边形相关的先
     */
    private void drawPolygonLine(Canvas canvas) {
        path.moveTo(arrayPolygon[0][0], arrayPolygon[0][1]);
        path.lineTo(arrayPolygon[1][0], arrayPolygon[1][1]);
        path.lineTo(arrayPolygon[2][0], arrayPolygon[2][1]);
        path.lineTo(arrayPolygon[3][0], arrayPolygon[3][1]);
        path.lineTo(arrayPolygon[4][0], arrayPolygon[4][1]);
        path.lineTo(arrayPolygon[5][0], arrayPolygon[5][1]);
        path.close();
        linePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, linePaint);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setShader(mShader);
        canvas.drawPath(path, linePaint);
        linePaint.setShader(null);
        path.reset();
    }

    /**
     * 画六边形相关的点
     */
    private void drawPolygonDot(Canvas canvas) {
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.WHITE);
        drawPolygonDotCircle(canvas);
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setColor(dotLineColor);
        drawPolygonDotCircle(canvas);
    }

    /**
     * 画六边形相关的点圆
     */
    private void drawPolygonDotCircle(Canvas canvas) {
        canvas.drawCircle(arrayPolygon[0][0], arrayPolygon[0][1], tempDotRadius, dotPaint);
        canvas.drawCircle(arrayPolygon[1][0], arrayPolygon[1][1], tempDotRadius, dotPaint);
        canvas.drawCircle(arrayPolygon[2][0], arrayPolygon[2][1], tempDotRadius, dotPaint);
        canvas.drawCircle(arrayPolygon[3][0], arrayPolygon[3][1], tempDotRadius, dotPaint);
        canvas.drawCircle(arrayPolygon[4][0], arrayPolygon[4][1], tempDotRadius, dotPaint);
        canvas.drawCircle(arrayPolygon[5][0], arrayPolygon[5][1], tempDotRadius, dotPaint);
    }

    /**
     * 当前是否是 六边形模式
     */
    private boolean isPolygon() {
        return mType == TYPE_POLYGON;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (checkPoint(event.getX(), event.getY())) {
                    statueMove = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (statueMove && moveDotIndex != -1) {
                    movePoint(event.getX(), event.getY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                moveStatueReset();
                break;
            default:
                break;

        }
        return super.onTouchEvent(event);
    }


    /**
     * 检查触摸的位置是不是在点上
     */
    private boolean checkPoint(float x, float y) {
        if (isPolygon()) {
            return checkPointPolygon(x, y);
        } else {
            return false;
        }
    }

    /**
     * 六边形 检查触摸的位置是不是在点上
     */
    private boolean checkPointPolygon(float x, float y) {
        for (int i = 0; i < arrayPolygon.length; i++) {
            if (Math.abs(x - arrayPolygon[i][0]) < tempDotRadius && Math.abs(y - arrayPolygon[i][1]) < tempDotRadius) {
                moveDotIndex = i;
                statueMove = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 移动区域标记点
     */
    private void movePoint(float x, float y) {
        //边界检查
        if (x < tempDotRadius || y < tempDotRadius
                || x + tempDotRadius > getMeasuredWidth() || y + tempDotRadius > getMeasuredHeight()) {
            return;
        }
        if (isPolygon() && pointLegal(x, y)) {
            arrayPolygon[moveDotIndex][0] = x;
            arrayPolygon[moveDotIndex][1] = y;
            invalidate();
        }
    }

    /**
     * 判断是否可以组成不交叉的六边形
     * 点 线 不相交
     */
    private boolean pointLegal(float x, float y) {
        return !polygonDotIntersect(x, y) && !polygonLineIntersect(x, y);
    }

    /**
     * 和其他五个点是否相交
     *
     * @return true 有相交 不重绘
     */
    private boolean polygonDotIntersect(float x, float y) {
        for (int i = 0; i < arrayPolygon.length; i++) {
            //除去当前点 然后当前点和其他点中心距离 差距要大于两个半径
            if (i != moveDotIndex
                    && Math.abs(x - arrayPolygon[i][0]) <= tempDotRadius * 2
                    && Math.abs(y - arrayPolygon[i][1]) <= tempDotRadius * 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否组成 线段不相交的刘表逆行
     */
    private boolean polygonLineIntersect(float x, float y) {
        //分别判断移动点的两边和其他不相连的三边是否相交  相交则return true
        int s = arrayPolygon.length;
        if (checkLineIntersect(x, y,
                arrayPolygon[(moveDotIndex + s + 1) % s][0], arrayPolygon[(moveDotIndex + s + 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 2) % s][0], arrayPolygon[(moveDotIndex + s + 2) % s][1],
                arrayPolygon[(moveDotIndex + s + 3) % s][0], arrayPolygon[(moveDotIndex + s + 3) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                arrayPolygon[(moveDotIndex + s + 1) % s][0], arrayPolygon[(moveDotIndex + s + 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 3) % s][0], arrayPolygon[(moveDotIndex + s + 3) % s][1],
                arrayPolygon[(moveDotIndex + s + 4) % s][0], arrayPolygon[(moveDotIndex + s + 4) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                arrayPolygon[(moveDotIndex + s + 1) % s][0], arrayPolygon[(moveDotIndex + s + 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 4) % s][0], arrayPolygon[(moveDotIndex + s + 4) % s][1],
                arrayPolygon[(moveDotIndex + s + 5) % s][0], arrayPolygon[(moveDotIndex + s + 5) % s][1])) {
            return true;
        }

        if (checkLineIntersect(x, y,
                arrayPolygon[(moveDotIndex + s - 1) % s][0], arrayPolygon[(moveDotIndex + s - 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 1) % s][0], arrayPolygon[(moveDotIndex + s + 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 2) % s][0], arrayPolygon[(moveDotIndex + s + 2) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                arrayPolygon[(moveDotIndex + s - 1) % s][0], arrayPolygon[(moveDotIndex + s - 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 2) % s][0], arrayPolygon[(moveDotIndex + s + 2) % s][1],
                arrayPolygon[(moveDotIndex + s + 3) % s][0], arrayPolygon[(moveDotIndex + s + 3) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                arrayPolygon[(moveDotIndex + s - 1) % s][0], arrayPolygon[(moveDotIndex + s - 1) % s][1],
                arrayPolygon[(moveDotIndex + s + 3) % s][0], arrayPolygon[(moveDotIndex + s + 3) % s][1],
                arrayPolygon[(moveDotIndex + s + 4) % s][0], arrayPolygon[(moveDotIndex + s + 4) % s][1])) {
            return true;
        }
        return false;
    }

    /**
     * 检查 两条线段是否相交
     *
     * @return true 相交  false  不相交
     */
    private boolean checkLineIntersect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        //这里的确如此，这一步是判定两矩形是否相交
        //1.线段ab的低点低于cd的最高点（可能重合） 2.cd的最左端小于ab的最右端（可能重合）
        //3.cd的最低点低于ab的最高点（加上条件1，两线段在竖直方向上重合） 4.ab的最左端小于cd的最右端（加上条件2，两直线在水平方向上重合）
        //综上4个条件，两条线段组成的矩形是重合的
        //特别要注意一个矩形含于另一个矩形之内的情况
        if (!(
                Math.min(x1, x2) <= Math.max(x3, x4) && Math.min(y3, y4) <= Math.max(y1, y2)
                        && Math.min(x3, x4) <= Math.max(x1, x2) && Math.min(y1, y2) <= Math.max(y3, y4)
        )) {
            //不相交
            return false;
        }

        //跨立实验：
        // 如果两条线段相交，那么必须跨立，就是以一条线段为标准，另一条线段的两端点一定在这条线段的两段
        //也就是说a b两点在线段cd的两端，c d两点在线段ab的两端
        double u, v, w, z;//分别记录两个向量
        u = (x3 - x1) * (y2 - y1) - (x2 - x1) * (y3 - y1);
        v = (x4 - x1) * (y2 - y1) - (x2 - x1) * (y4 - y1);
        w = (x1 - x3) * (y4 - y3) - (x4 - x3) * (y1 - y3);
        z = (x2 - x3) * (y4 - y3) - (x4 - x2) * (y2 - y3);
        return (u * v <= 0 && w * z <= 0);
    }


    /**
     * 重置拖动状态控制值
     */
    private void moveStatueReset() {
        statueMove = false;
        moveDotIndex = -1;
    }


    /**
     * 安全范围类型
     */
    @IntDef({TYPE_PROXIMITY, TYPE_POLYGON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GEOFENCETYPE {
    }
}
