package com.hyfun.camera;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

public class CameraCaptureActivity extends BaseActivity implements CameraCaptureInterface {
    private static final String MODE = "MODE";
    private static final String DURATION = "DURATION";


    private CameraOrientationListener cameraOrientationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        /**
         * 传过来的配置
         */
        int mode = getIntent().getIntExtra(MODE, 0);
        int duration = getIntent().getIntExtra(MODE, 0);
        if (mode == 0 || duration == 0) {
            throw new RuntimeException("mode or duration can`t be zero!");
        }

        /**
         *  开启方向监听
         */
        cameraOrientationListener = new CameraOrientationListener(this);
        cameraOrientationListener.enable();

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

    @Override
    protected void onDestroy() {
        cameraOrientationListener.disable();
        cameraOrientationListener = null;
        super.onDestroy();
    }

    // ——————————————————————————————————————————————————————————————————
    @Override
    public void returnPath(String path) {

    }

    @Override
    public int getOrientation() {
        return cameraOrientationListener.getOrientation();
    }


    // ——————————————————————————————————————————————————————————————————


}
