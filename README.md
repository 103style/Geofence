# Geofence


GeofenceView  ： 电子围栏视图（圆形 和 六边形）

#### ScreenShot

<img src="https://github.com/103style/Geofence/blob/master/screenshot/Screenshot1.png"  height="432" width="216">  <img src="https://github.com/103style/Geofence/blob/master/screenshot/Screenshot2.png"  height="432" width="216">  <img src="https://github.com/103style/Geofence/blob/master/screenshot/Screenshot3.png"  height="432" width="216">

#### Install
add to the project `build.gradle`
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
add to the app  `build.gradle`
```
dependencies {
        implementation 'com.github.103style:Geofence:0.0.2'
}

```


#### Usage
```
<com.tcl.xiaokeluo.GeofenceView
    android:id="@+id/gv_test"
    android:layout_width="300dp"
    android:layout_height="300dp"
    android:layout_gravity="center"
    android:background="@drawable/test_bg"
    app:gv_area_color="#1A398EFF"
    app:gv_dot_line_color="@color/colorPrimary"
    app:gv_dot_radius="10dp"
    app:gv_line_color="@color/colorAccent"
    app:gv_line_width="2dp"
    app:gv_radius="100dp"
    app:gv_text_bg_color="#FFFFFF"
    app:gv_show_text="true"
    app:gv_text_bg_line_width="1dp"
    app:gv_text_bg_stroke_color="#ffff00"
    app:gv_text_color="#ff00ff"
    app:gv_text_size="14dp"
    app:gv_type="TYPE_POLYGON" />
```

#### Attrs
```
<declare-styleable name="GeofenceView">
    <!-- 圆形 或者 六边形 -->
    <attr name="gv_type">
        <flag name="TYPE_PROXIMITY" value="1" />
        <flag name="TYPE_POLYGON" value="2" />
    </attr>
    <!-- 圆形时 圆的半径 -->
    <attr name="gv_radius" format="dimension" />
    <!--构成的圆或者六边形 区域的填充色-->
    <attr name="gv_area_color" format="color" />
    <!-- 六边形 点的半径 -->
    <attr name="gv_dot_radius" format="dimension" />
    <!-- 六边形点的线颜色 -->
    <attr name="gv_dot_line_color" format="color" />
    <!-- 六边形 点与点的连线颜色 -->
    <attr name="gv_line_color" format="color" />
    <!-- 六边形 点与点的连线宽度 -->
    <attr name="gv_line_width" format="dimension" />
    <!-- 是否显示 六边形 边长度 文字-->
    <attr name="gv_show_text" format="boolean" />
    <!-- 六边形 边长度 文字的大小 -->
    <attr name="gv_text_size" format="dimension" />
    <!-- 六边形 边长度 文字的颜色 -->
    <attr name="gv_text_color" format="color" />
    <!-- 六边形 边长度 文字的背景颜色 -->
    <attr name="gv_text_bg_color" format="color" />
    <!-- 六边形 边长度 文字的背景描边颜色 -->
    <attr name="gv_text_bg_stroke_color" format="color" />
    <!-- 六边形 边长度 文字的背景描边宽度 -->
    <attr name="gv_text_bg_line_width" format="dimension" />
</declare-styleable>
```


```
    /**
     * 设置展示形状
     */
    public void setType(@GEOFENCETYPE int mType) {}

    /**
     * 设置电子围栏的 区域颜色
     */
    public void setAreaColor(int areaColor) {}

    /**
     * 设置六边形文字背景颜色
     */
    public void setTextBgColor(int textBgColor) {}

    /**
     * 是否显示六边形长度文字
     */
    public void setShowText(boolean showText) {}

    /**
     * 设置地图缩放级别
     *
     * @param mapZoom 地图缩放
     */
    public void setMapZoom(float mapZoom) {}
    
    /**
     * 设置六边形显示问题的格式(需带 %d)
     */
    public void setDisTextFormat(String disTextFormat) {}

    /**
     * 修改 六边形的 距离文字的大小
     */
    public void setTextSize(int textSize) {}

    /**
     * 设置圆形的半径
     */
    public void setCircleRadius(float circleRadius) {}
```
