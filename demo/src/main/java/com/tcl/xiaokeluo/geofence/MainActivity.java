package com.tcl.xiaokeluo.geofence;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.tcl.xiaokeluo.GeofenceView;

public class MainActivity extends AppCompatActivity {


    private GeofenceView geofenceView;

    private SeekBar sbRadius;

    private EditText etTxtFormat, etEnlargeTime;
    private Switch divisibleBy10, showDisTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofenceView = findViewById(R.id.gv_test);

        RadioGroup radioGroup = findViewById(R.id.rg_type);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            geofenceView.setType(checkedId == R.id.rb_proximity ? GeofenceView.TYPE_PROXIMITY : GeofenceView.TYPE_POLYGON);
            sbRadius.setVisibility(checkedId == R.id.rb_proximity ? View.VISIBLE : View.GONE);
            geofenceView.setCircleRadius(sbRadius.getProgress());
        });

        sbRadius = findViewById(R.id.sb_radius);
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                geofenceView.setCircleRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        etTxtFormat = findViewById(R.id.ed_txt_format);
        etEnlargeTime = findViewById(R.id.ed_enlarge_times);
        findViewById(R.id.bt_format).setOnClickListener(v -> {
            String format = etTxtFormat.getText().toString();
            if (format.contains("%d") || format.contains("%s") || format.contains("%f")) {
                geofenceView.setDisTextFormat(format);
                geofenceView.invalidate();
            } else {
                Toast.makeText(MainActivity.this, "need contains %d or %s or %f", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.bt_enlarge_time).setOnClickListener(v -> {
            String times = etEnlargeTime.getText().toString();
            geofenceView.setPolygonDotTouchAreaEnlargeTimes(Integer.parseInt(times));
        });
        divisibleBy10 = findViewById(R.id.switch_divisible_by_10);
        divisibleBy10.setSelected(true);
        divisibleBy10.setOnClickListener(v -> {
            boolean state = !divisibleBy10.isSelected();
            divisibleBy10.setSelected(state);
            geofenceView.setDisTextDivisibleBy10(state);
            geofenceView.invalidate();
        });

        showDisTxt = findViewById(R.id.switch_show_dis_txt);
        showDisTxt.setSelected(true);
        showDisTxt.setOnClickListener(v -> {
            boolean state = !showDisTxt.isSelected();
            showDisTxt.setSelected(state);
            geofenceView.setShowText(state);
            geofenceView.invalidate();
        });

    }
}
