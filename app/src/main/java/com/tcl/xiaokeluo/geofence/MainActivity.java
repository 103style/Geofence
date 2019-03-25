package com.tcl.xiaokeluo.geofence;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tcl.xiaokeluo.GeofenceView;

public class MainActivity extends AppCompatActivity {

    private float[][] tempArrayProximity, tempArrayPolygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GeofenceView geofenceView = findViewById(R.id.gv_test);


        final TextView tvShow = findViewById(R.id.tv_show);

        geofenceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geofenceView.setType(System.currentTimeMillis() % 2 == 0 ? GeofenceView.TYPE_POLYGON : GeofenceView.TYPE_PROXIMITY);
            }
        });

        geofenceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String temp = "";
                tempArrayPolygon = new float[6][2];
                if (geofenceView.getType() == GeofenceView.TYPE_POLYGON) {
                    float[][] tempArray = geofenceView.getTempArrayPolygon();
                    for (int i = 0; i < tempArray.length; i++) {
                        tempArrayPolygon[i][0] = tempArray[i][0];
                        tempArrayPolygon[i][1] = tempArray[i][1];
                        temp += "   x = " + tempArray[i][0] + ", y = " + tempArray[i][1];
                    }
                } else {
                    tempArrayProximity = geofenceView.getTempArrayProximity();
                    for (int i = 0; i < tempArrayProximity.length; i++) {
                        temp += "   x = " + tempArrayProximity[i][0] + ", y = " + tempArrayProximity[i][1];
                    }
                }
                tvShow.setText(temp);
                return true;
            }
        });

        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = "";
                if (geofenceView.getType() == GeofenceView.TYPE_POLYGON) {
                    for (int i = 0; i < tempArrayPolygon.length; i++) {
                        temp += "   x = " + tempArrayPolygon[i][0] + ", y = " + tempArrayPolygon[i][1];
                    }
                    tvShow.setText(temp);
                }
            }
        });

        tvShow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                geofenceView.setArrayPolygon(tempArrayPolygon);
                return true;
            }
        });

        SeekBar seekBar = findViewById(R.id.sb_test);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                geofenceView.setMapZoom(1 + progress);
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
