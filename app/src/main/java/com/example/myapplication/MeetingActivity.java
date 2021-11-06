package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMeetingBinding;

public class MeetingActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMeetingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMeetingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View rect1 = (View) findViewById(R.id.rectangle_1);
        rect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingActivity.this, GetMeetingActivity.class);
                startActivity(intent);
            }
        });

        View rect2 = (View) findViewById(R.id.rectangle_2);
        rect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingActivity.this, SecondMeetingActivity.class);
                startActivity(intent);
            }
        });

        Button button = (Button) findViewById(R.id.button_stats);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        });

        Button button_route = (Button) findViewById(R.id.button_route);
        button_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingActivity.this, MapsActivity.class);
                intent.putExtra("ROUTE", 1);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_meeting2);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}