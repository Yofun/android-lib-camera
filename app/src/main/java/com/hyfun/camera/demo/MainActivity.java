package com.hyfun.camera.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyfun.camera.FunCamera;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void capture(View view) {
        FunCamera.capturePhoto(this, 10);
    }

    public void record(View view) {
        FunCamera.captureRecord(this, 20, 10000);
    }

    public void captureRecord(View view) {
        FunCamera.capturePhoto2Record(this, 30, 15000);
    }
}
