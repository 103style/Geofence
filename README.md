# Geofence


GeofenceView  ： 电子围栏视图（圆形 和 六边形）


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
