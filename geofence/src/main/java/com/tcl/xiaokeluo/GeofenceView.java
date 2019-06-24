package com.tcl.xiaokeluo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
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
    private Paint linePaint, dotPaint, textPaint, textBgPaint;
    /**
     * 半径、线宽
     */
    private float dotRadius, lineWidth, textBgLineWidth;
    /**
     * 设置的初始半径
     */
    private float mRadius;
    /**
     * 半径（圆形） 两点之前的距离（正六边形） 随缩放改变
     */
    private float circleRadius;
    /**
     * 设置的圆的半径 不随缩放改变
     */
    private float circleSetRadius;

    private Path polygonLinePath, polygonTextBgPath;
    /**
     * 圆形 和  六边形对应的点坐标  始终是不缩放的坐标
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
     * 保存缩放变化之后的坐标
     */
    private float[][] tempArrayProximity, tempArrayPolygon;

    /**
     * 区域遮罩颜色
     */
    private Shader mShader, mTextBgShader;

    /**
     * 颜色
     */
    private int lineColor, dotLineColor, areaColor, textBgColor, textStrokeColor, textColor;
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
    private float mapZoom;
    /**
     * 是否已经init 坐标数组的大小
     */
    private boolean hasInitArray;
    /**
     * 距离对应的值  默认 200
     * 计算距离  y = mapZoom *  x / distanceRatio
     */
    private float distanceRatio;
    /**
     * 距离文字
     */
    private String disText;
    /**
     * 距离文字格式  100m
     */
    private String disTextFormat = "%dm";
    /**
     * 多边形距离的文字大小
     */
    private int textSize;
    /**
     * 计算文字的位置
     */
    private PolygonTextParams polygonTextParams;
    /**
     * 文字属性
     */
    private Paint.FontMetricsInt fontMetricsInt;
    /**
     * 文字基线高度
     */
    private int baseline;
    /**
     * 文字高度
     */
    private int textHeight;
    /**
     * 文字背景
     */
    private Rect textBgRect;
    /**
     * 是否显示六边形长度文字
     */
    private boolean showText;

    public GeofenceView(Context context) {
        this(context, null);
    }

    public GeofenceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GeofenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hasInitArray = false;
        initTypeArray(context, attrs);
        init();
    }

    /**
     * 获取自定义属性值
     */
    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GeofenceView);
        mType = ta.getInt(R.styleable.GeofenceView_gv_type, TYPE_PROXIMITY);
        dotRadius = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_dot_radius, 16);
        distanceRatio = circleSetRadius = circleRadius = mRadius = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_radius, 200);
        dotLineColor = ta.getColor(R.styleable.GeofenceView_gv_dot_line_color, 0xFF398EFF);
        lineColor = ta.getColor(R.styleable.GeofenceView_gv_line_color, 0xFF398EFF);
        areaColor = ta.getColor(R.styleable.GeofenceView_gv_area_color, 0x1A398EFF);
        showText = ta.getBoolean(R.styleable.GeofenceView_gv_show_text, true);
        textBgColor = ta.getColor(R.styleable.GeofenceView_gv_text_bg_color, 0xFFFFFFFF);
        textStrokeColor = ta.getColor(R.styleable.GeofenceView_gv_text_bg_stroke_color, 0x61398EFF);
        textColor = ta.getColor(R.styleable.GeofenceView_gv_text_color, 0xFF398EFF);
        lineWidth = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_line_width, 5);
        textBgLineWidth = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_text_bg_line_width, 5);
        textSize = ta.getDimensionPixelOffset(R.styleable.GeofenceView_gv_text_size, 25);
        ta.recycle();
    }

    /**
     * 初始化
     */
    private void init() {
        mapZoom = 1;
        textBgRect = new Rect();
        polygonTextParams = new PolygonTextParams();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mShader = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                new int[]{areaColor, areaColor}, null, Shader.TileMode.CLAMP);
        mTextBgShader = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                new int[]{textBgColor, textBgColor}, null, Shader.TileMode.CLAMP);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineColor);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStrokeWidth(lineWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        updateTextAttrs();

        textBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


        polygonLinePath = new Path();
        polygonTextBgPath = new Path();
    }

    /**
     * 修改文字参数
     */
    private void updateTextAttrs() {
        fontMetricsInt = textPaint.getFontMetricsInt();
        baseline = Math.abs(fontMetricsInt.leading) + Math.abs(fontMetricsInt.ascent);
        textHeight = Math.abs(fontMetricsInt.bottom - fontMetricsInt.top);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!hasInitArray) {
            initDotArray();
            hasInitArray = true;
        }
    }

    /**
     * 初始化 圆形 和 六边形 对应的六个点
     */
    private void initDotArray() {
        arrayProximity = new float[4][2];
        tempArrayProximity = new float[4][2];
        tempArrayProximity[0][0] = arrayProximity[0][0] = getMeasuredWidth() / 2 - circleRadius;
        tempArrayProximity[0][1] = arrayProximity[0][1] = getMeasuredHeight() / 2;
        tempArrayProximity[1][0] = arrayProximity[1][0] = getMeasuredWidth() / 2;
        tempArrayProximity[1][1] = arrayProximity[1][1] = getMeasuredHeight() / 2 - circleRadius;
        tempArrayProximity[2][0] = arrayProximity[2][0] = getMeasuredWidth() / 2 + circleRadius;
        tempArrayProximity[2][1] = arrayProximity[2][1] = getMeasuredHeight() / 2;
        tempArrayProximity[3][0] = arrayProximity[3][0] = getMeasuredWidth() / 2 + circleRadius;
        tempArrayProximity[3][1] = arrayProximity[3][1] = getMeasuredHeight() / 2 + circleRadius;

        arrayPolygon = new float[6][2];
        tempArrayPolygon = new float[6][2];
        tempArrayPolygon[0][0] = arrayPolygon[0][0] = getMeasuredWidth() / 2 - circleRadius;
        tempArrayPolygon[0][1] = arrayPolygon[0][1] = getMeasuredHeight() / 2;
        tempArrayPolygon[1][0] = arrayPolygon[1][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3));
        tempArrayPolygon[1][1] = arrayPolygon[1][1] = getMeasuredHeight() / 2 - (int) (circleRadius * Math.sin(Math.PI / 3));
        tempArrayPolygon[2][0] = arrayPolygon[2][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3)) + circleRadius;
        tempArrayPolygon[2][1] = arrayPolygon[2][1] = getMeasuredHeight() / 2 - (int) (circleRadius * Math.sin(Math.PI / 3));
        tempArrayPolygon[3][0] = arrayPolygon[3][0] = getMeasuredWidth() / 2 + circleRadius;
        tempArrayPolygon[3][1] = arrayPolygon[3][1] = getMeasuredHeight() / 2;
        tempArrayPolygon[4][0] = arrayPolygon[4][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3)) + circleRadius;
        tempArrayPolygon[4][1] = arrayPolygon[4][1] = getMeasuredHeight() / 2 + (int) (circleRadius * Math.sin(Math.PI / 3));
        tempArrayPolygon[5][0] = arrayPolygon[5][0] = getMeasuredWidth() / 2 - (int) (circleRadius * Math.cos(Math.PI / 3));
        tempArrayPolygon[5][1] = arrayPolygon[5][1] = getMeasuredHeight() / 2 + (int) (circleRadius * Math.sin(Math.PI / 3));
    }

    /**
     * 获取当前的图形类型
     */
    public int getType() {
        return mType;
    }

    /**
     * 设置展示形状
     */
    public void setType(@GEOFENCETYPE int mType) {
        if (this.mType == mType) {
            return;
        }
        this.mType = mType;
        circleRadius = mRadius / mapZoom;
        updateDotArray();
        invalidate();
    }

    /**
     * 设置电子围栏的 区域颜色
     */
    public void setAreaColor(int areaColor) {
        this.areaColor = areaColor;
        mShader = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                new int[]{areaColor, areaColor}, null, Shader.TileMode.CLAMP);
    }

    /**
     * 设置六边形文字背景颜色
     */
    public void setTextBgColor(int textBgColor) {
        this.textBgColor = textBgColor;
        mTextBgShader = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                new int[]{textBgColor, textBgColor}, null, Shader.TileMode.CLAMP);
    }

    /**
     * 是否显示六边形长度文字
     */
    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    /**
     * 设置地图缩放级别
     *
     * @param mapZoom 地图缩放
     */
    public void setMapZoom(float mapZoom) {
        if (this.mapZoom == mapZoom) {
            return;
        }
        this.mapZoom = mapZoom;
        circleRadius = circleSetRadius / mapZoom;
        updateDotArray();
        invalidate();
    }

    /**
     * 设置圆形的 四个点坐标
     * -     1
     * -  0     2
     * -     3
     */
    public void setArrayProximity(float[][] arrays) {
        if (arrayProximity == null || arrays.length != tempArrayProximity.length) {
            throw new IndexOutOfBoundsException("arrayProximity length should be 4");
        }
        tempArrayPolygon = arrays;
        for (int i = 0; i < arrays.length; i++) {
            arrayProximity[i][0] = getMeasuredWidth() / 2 + mapZoom * (arrays[i][0] - getMeasuredWidth() / 2);
            arrayProximity[i][1] = getMeasuredHeight() / 2 + mapZoom * (arrays[i][1] - getMeasuredHeight() / 2);
        }
        invalidate();
    }

    /**
     * 设置多边形的 六个点坐标
     * -     1    2
     * -
     * -  0          3
     * -
     * -     5    4
     */
    public void setArrayPolygon(float[][] arrays) {
        if (arrayPolygon == null || arrayPolygon.length != arrays.length) {
            throw new IndexOutOfBoundsException("arrayProximity length should be 6");
        }
        tempArrayPolygon = arrays;
        for (int i = 0; i < arrays.length; i++) {
            arrayPolygon[i][0] = getMeasuredWidth() / 2 + mapZoom * (arrays[i][0] - getMeasuredWidth() / 2);
            arrayPolygon[i][1] = getMeasuredHeight() / 2 + mapZoom * (arrays[i][1] - getMeasuredHeight() / 2);
        }
        invalidate();
    }

    /**
     * 获取当圆的的四个点坐标
     */
    public float[][] getTempArrayProximity() {
        return tempArrayProximity;
    }

    /**
     * 获取六边形的六个点坐标
     */
    public float[][] getTempArrayPolygon() {
        return tempArrayPolygon;
    }

    /**
     * 更新坐标点 x y 值
     */
    private void updateDotArray() {
        if (tempArrayProximity != null) {
            tempArrayProximity[0][0] = getMeasuredWidth() / 2 + (arrayProximity[0][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayProximity[0][1] = getMeasuredHeight() / 2 + (arrayProximity[0][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayProximity[1][0] = getMeasuredWidth() / 2 + (arrayProximity[1][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayProximity[1][1] = getMeasuredHeight() / 2 + (arrayProximity[1][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayProximity[2][0] = getMeasuredWidth() / 2 + (arrayProximity[2][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayProximity[2][1] = getMeasuredHeight() / 2 + (arrayProximity[2][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayProximity[3][0] = getMeasuredWidth() / 2 + (arrayProximity[3][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayProximity[3][1] = getMeasuredHeight() / 2 + (arrayProximity[3][1] - getMeasuredHeight() / 2) / mapZoom;
        }
        if (tempArrayPolygon != null) {
            tempArrayPolygon[0][0] = getMeasuredWidth() / 2 + (arrayPolygon[0][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayPolygon[0][1] = getMeasuredHeight() / 2 + (arrayPolygon[0][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayPolygon[1][0] = getMeasuredWidth() / 2 + (arrayPolygon[1][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayPolygon[1][1] = getMeasuredHeight() / 2 + (arrayPolygon[1][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayPolygon[2][0] = getMeasuredWidth() / 2 + (arrayPolygon[2][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayPolygon[2][1] = getMeasuredHeight() / 2 + (arrayPolygon[2][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayPolygon[3][0] = getMeasuredWidth() / 2 + (arrayPolygon[3][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayPolygon[3][1] = getMeasuredHeight() / 2 + (arrayPolygon[3][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayPolygon[4][0] = getMeasuredWidth() / 2 + (arrayPolygon[4][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayPolygon[4][1] = getMeasuredHeight() / 2 + (arrayPolygon[4][1] - getMeasuredHeight() / 2) / mapZoom;
            tempArrayPolygon[5][0] = getMeasuredWidth() / 2 + (arrayPolygon[5][0] - getMeasuredWidth() / 2) / mapZoom;
            tempArrayPolygon[5][1] = getMeasuredHeight() / 2 + (arrayPolygon[5][1] - getMeasuredHeight() / 2) / mapZoom;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mType == TYPE_PROXIMITY) {
            drawProximity(canvas);
        } else {
            if (dotRadius * 2 > circleRadius) {
                return;
            }
            drawPolygonLine(canvas);
            drawPolygonDot(canvas);

            if (showText) {
                drawPolygonText(canvas);
            }
        }
    }

    public void setDisTextFormat(String disTextFormat) {
        this.disTextFormat = disTextFormat;
    }

    /**
     * 修改 六边形的 距离文字的大小
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
        updateTextAttrs();
    }

    /**
     * 设置圆形的半径
     */
    public void setCircleRadius(float circleRadius) {
        if (circleRadius == this.circleSetRadius) {
            return;
        }
        circleSetRadius = circleRadius;
        this.circleRadius = circleSetRadius / mapZoom;
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
        polygonLinePath.moveTo(tempArrayPolygon[0][0], tempArrayPolygon[0][1]);
        polygonLinePath.lineTo(tempArrayPolygon[1][0], tempArrayPolygon[1][1]);
        polygonLinePath.lineTo(tempArrayPolygon[2][0], tempArrayPolygon[2][1]);
        polygonLinePath.lineTo(tempArrayPolygon[3][0], tempArrayPolygon[3][1]);
        polygonLinePath.lineTo(tempArrayPolygon[4][0], tempArrayPolygon[4][1]);
        polygonLinePath.lineTo(tempArrayPolygon[5][0], tempArrayPolygon[5][1]);
        polygonLinePath.close();
        linePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(polygonLinePath, linePaint);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setShader(mShader);
        canvas.drawPath(polygonLinePath, linePaint);
        linePaint.setShader(null);
        polygonLinePath.reset();
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
        canvas.drawCircle(tempArrayPolygon[0][0], tempArrayPolygon[0][1], dotRadius, dotPaint);
        canvas.drawCircle(tempArrayPolygon[1][0], tempArrayPolygon[1][1], dotRadius, dotPaint);
        canvas.drawCircle(tempArrayPolygon[2][0], tempArrayPolygon[2][1], dotRadius, dotPaint);
        canvas.drawCircle(tempArrayPolygon[3][0], tempArrayPolygon[3][1], dotRadius, dotPaint);
        canvas.drawCircle(tempArrayPolygon[4][0], tempArrayPolygon[4][1], dotRadius, dotPaint);
        canvas.drawCircle(tempArrayPolygon[5][0], tempArrayPolygon[5][1], dotRadius, dotPaint);
    }

    /**
     * 画六边形文字
     */
    private void drawPolygonText(Canvas canvas) {
        int length = tempArrayPolygon.length;
        for (int i = 0; i < length; i++) {
            polygonTextParams.distance = Math.sqrt(
                    Math.pow(tempArrayPolygon[(i + 1) % length][0] - tempArrayPolygon[i % length][0], 2)
                            + Math.pow(tempArrayPolygon[(i + 1) % length][1] - tempArrayPolygon[i % length][1], 2));

            int dis = (int) Math.ceil(polygonTextParams.distance * mapZoom);
            dis = dis - dis % 10;
            disText = String.format(disTextFormat, dis);
            //增加间隙
            float textW = textPaint.measureText("_" + disText);
            polygonTextParams.textW = textW;
            polygonTextParams.textH = textHeight;
            polygonTextParams.getValues(tempArrayPolygon[i % length][0], tempArrayPolygon[i % length][1],
                    tempArrayPolygon[(i + 1) % length][0], tempArrayPolygon[(i + 1) % length][1]);

            canvas.save();

            canvas.rotate(polygonTextParams.angelDegree, polygonTextParams.x1, polygonTextParams.y1);


            textBgPaint.setStyle(Paint.Style.FILL);
            textBgPaint.setShader(mTextBgShader);
            textBgPaint.setColor(textBgColor);
            textBgRect.set((int) polygonTextParams.leftTopX, (int) polygonTextParams.leftTopY,
                    (int) (polygonTextParams.leftTopX + textW), (int) (polygonTextParams.leftTopY + textHeight));
            canvas.drawRect(textBgRect, textBgPaint);


            polygonTextBgPath.moveTo(polygonTextParams.leftTopX, polygonTextParams.leftTopY);
            polygonTextBgPath.lineTo(polygonTextParams.leftTopX + textW, polygonTextParams.leftTopY);
            polygonTextBgPath.lineTo(polygonTextParams.leftTopX + textW, polygonTextParams.leftTopY + textHeight);
            polygonTextBgPath.lineTo(polygonTextParams.leftTopX, polygonTextParams.leftTopY + textHeight);
            polygonTextBgPath.close();

            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setColor(textStrokeColor);
            textPaint.setStrokeWidth(textBgLineWidth);
            canvas.drawPath(polygonTextBgPath, textPaint);
            polygonTextBgPath.reset();

            textPaint.setColor(textColor);
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(" " + disText, polygonTextParams.leftTopX, polygonTextParams.leftTopY + baseline, textPaint);
            canvas.restore();
        }
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
        for (int i = 0; i < tempArrayPolygon.length; i++) {
            if (Math.abs(x - tempArrayPolygon[i][0]) < dotRadius && Math.abs(y - tempArrayPolygon[i][1]) < dotRadius) {
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
        if (x < dotRadius || y < dotRadius
                || x + dotRadius > getMeasuredWidth() || y + dotRadius > getMeasuredHeight()) {
            return;
        }
        if (isPolygon() && pointLegal(x, y)) {
            tempArrayPolygon[moveDotIndex][0] = x;
            tempArrayPolygon[moveDotIndex][1] = y;
            arrayPolygon[moveDotIndex][0] = getMeasuredWidth() / 2 + mapZoom * (x - getMeasuredWidth() / 2);
            arrayPolygon[moveDotIndex][1] = getMeasuredHeight() / 2 + mapZoom * (y - getMeasuredHeight() / 2);
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
        for (int i = 0; i < tempArrayPolygon.length; i++) {
            //除去当前点 然后当前点和其他点中心距离 差距要大于两个半径
            if (i != moveDotIndex
                    && Math.abs(x - tempArrayPolygon[i][0]) <= dotRadius * 2
                    && Math.abs(y - tempArrayPolygon[i][1]) <= dotRadius * 2) {
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
        int s = tempArrayPolygon.length;
        if (checkLineIntersect(x, y,
                tempArrayPolygon[(moveDotIndex + s + 1) % s][0], tempArrayPolygon[(moveDotIndex + s + 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 2) % s][0], tempArrayPolygon[(moveDotIndex + s + 2) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 3) % s][0], tempArrayPolygon[(moveDotIndex + s + 3) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                tempArrayPolygon[(moveDotIndex + s + 1) % s][0], tempArrayPolygon[(moveDotIndex + s + 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 3) % s][0], tempArrayPolygon[(moveDotIndex + s + 3) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 4) % s][0], tempArrayPolygon[(moveDotIndex + s + 4) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                tempArrayPolygon[(moveDotIndex + s + 1) % s][0], tempArrayPolygon[(moveDotIndex + s + 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 4) % s][0], tempArrayPolygon[(moveDotIndex + s + 4) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 5) % s][0], tempArrayPolygon[(moveDotIndex + s + 5) % s][1])) {
            return true;
        }

        if (checkLineIntersect(x, y,
                tempArrayPolygon[(moveDotIndex + s - 1) % s][0], tempArrayPolygon[(moveDotIndex + s - 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 1) % s][0], tempArrayPolygon[(moveDotIndex + s + 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 2) % s][0], tempArrayPolygon[(moveDotIndex + s + 2) % s][1])) {
            return true;
        }
        if (checkLineIntersect(x, y,
                tempArrayPolygon[(moveDotIndex + s - 1) % s][0], tempArrayPolygon[(moveDotIndex + s - 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 2) % s][0], tempArrayPolygon[(moveDotIndex + s + 2) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 3) % s][0], tempArrayPolygon[(moveDotIndex + s + 3) % s][1])) {
            return true;
        }
        return checkLineIntersect(x, y,
                tempArrayPolygon[(moveDotIndex + s - 1) % s][0], tempArrayPolygon[(moveDotIndex + s - 1) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 3) % s][0], tempArrayPolygon[(moveDotIndex + s + 3) % s][1],
                tempArrayPolygon[(moveDotIndex + s + 4) % s][0], tempArrayPolygon[(moveDotIndex + s + 4) % s][1]);
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
