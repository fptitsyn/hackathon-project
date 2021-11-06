package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class GetMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting);

        Button button = (Button) findViewById(R.id.map_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent = new Intent(GetMeetingActivity.this, MapsActivity.class);
                LatLng address = new LatLng(55.753884949680305, 37.691154336772904);
                mapsIntent.putExtra("ADDRESS", address);
                startActivity(mapsIntent);
            }
        });
    }
}