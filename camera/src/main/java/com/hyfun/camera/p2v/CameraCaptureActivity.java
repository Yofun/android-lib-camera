package com.hyfun.camera.p2v;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.hyfun.camera.FunCamera;
import com.hyfun.camera.R;

import java.util.List;

public class CameraCaptureActivity extends AppCompatActivity implements CameraCaptureInterface {
    public static final String MODE = "MODE";
    public static final String DURATION = "DURATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        setContentView(R.layout.activity_camera_capture);
        /**
         * 传过来的配置
         */
        int mode = getIntent().getIntExtra(MODE, 0);
        long duration = getIntent().getLongExtra(DURATION, 0);
        if (mode == 0 || duration == 0) {
            throw new RuntimeException("mode or duration can`t be zero!");
        }

        /**
         * 进入默认的fragment 进行预览
         */
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.camera_capture_main_framelayout, new CameraCaptureRecordFragment(mode, duration))
                    .commit();
        }
    }


    @Override
    public void onBackPressed() {
        try {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            ((BaseFragment) fragmentList.get(fragmentList.size() - 1)).finish();
        } catch (Exception e) {
            finish();
        }
    }

    // ——————————————————————————————————————————————————————————————————
    @Override
    public void returnPath(String path) {
        Intent intent = new Intent();
        intent.putExtra(FunCamera.DATA, path);
        setResult(RESULT_OK, intent);
        finish();
    }


    // ——————————————————————————————————————————————————————————————————


}
