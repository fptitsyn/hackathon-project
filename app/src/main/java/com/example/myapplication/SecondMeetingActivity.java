package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class SecondMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_meeting);

        Button button = (Button) findViewById(R.id.map_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent = new Intent(SecondMeetingActivity.this, MapsActivity.class);
                LatLng address = new LatLng(55.75208968204676, 37.58545511591237);
                mapsIntent.putExtra("ADDRESS", address);
                startActivity(mapsIntent);
            }
        });
    }
}