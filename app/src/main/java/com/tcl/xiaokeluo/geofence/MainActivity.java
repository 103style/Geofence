package com.tcl.xiaokeluo.geofence;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.tcl.xiaokeluo.GeofenceView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GeofenceView geofenceView = findViewById(R.id.gv_test);


        geofenceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geofenceView.setType(System.currentTimeMillis() % 2 == 0 ? GeofenceView.TYPE_POLYGON : GeofenceView.TYPE_PROXIMITY);
            }
        });

        geofenceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                geofenceView.setCircleRadius(100 + (int) (100 * Math.random()));
                return true;
            }
        });


        SeekBar seekBar = findViewById(R.id.sb_test);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
