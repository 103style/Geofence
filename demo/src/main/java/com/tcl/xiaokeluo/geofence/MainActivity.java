package com.tcl.xiaokeluo.geofence;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.tcl.xiaokeluo.GeofenceView;

public class MainActivity extends AppCompatActivity {


    private GeofenceView geofenceView;

    private SeekBar sbRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofenceView = findViewById(R.id.gv_test);

        RadioGroup radioGroup = findViewById(R.id.rg_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                geofenceView.setType(checkedId == R.id.rb_proximity ? GeofenceView.TYPE_PROXIMITY : GeofenceView.TYPE_POLYGON);
                sbRadius.setVisibility(checkedId == R.id.rb_proximity ? View.VISIBLE : View.GONE);
                geofenceView.setCircleRadius(sbRadius.getProgress());
            }
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

    }
}
