package com.hyfun.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyfun.camera.FunCamera;
import com.hyfun.camera.audio.FunAudioRecordListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
    }

    public void capture(View view) {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            FunCamera.capturePhoto(MainActivity.this, 10);
                        } else {
                            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void record(View view) {
        new RxPermissions(this)
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            FunCamera.captureRecord(MainActivity.this, 20, 10000);
                        } else {
                            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void captureRecord(View view) {
        new RxPermissions(this)
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            FunCamera.capturePhoto2Record(MainActivity.this, 30, 10000);
                        } else {
                            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void captureAudio(View view) {
        new RxPermissions(this)
                .request(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            FunCamera.captureAudioRecord(MainActivity.this, new FunAudioRecordListener() {
                                @Override
                                public void onAudioRecordResult(String filePath) {
                                    textView.setText("录音地址：" + filePath);
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String path = data.getStringExtra(FunCamera.DATA);
            String pathOrigin = data.getStringExtra(FunCamera.DATA_ORIGIN);
            StringBuilder sb = new StringBuilder();
            sb.append("压缩后地址：" + path + "\n");
            sb.append("原图的地址：" + pathOrigin);
            textView.setText(sb.toString());
        }
    }
}
