package com.example.scrollwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.snap.ReboundScrollView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSlideMenuClick(View view) {
        Intent intent = new Intent(this, SlideMenuActivity.class);
        startActivity(intent);
    }

    public void onReboundScrollViewClick(View view) {
        Intent intent = new Intent(this, ReboundScrollViewActivity.class);
        startActivity(intent);
    }
}
