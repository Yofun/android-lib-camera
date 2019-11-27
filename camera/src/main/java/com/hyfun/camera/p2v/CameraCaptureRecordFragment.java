package com.hyfun.camera.p2v;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyfun.camera.R;
import com.hyfun.camera.widget.CaptureButton;
import com.hyfun.camera.widget.FunSurfaceView;

/**
 * Created by HyFun on 2019/10/14.
 * Email: 775183940@qq.com
 * Description: 用于拍摄照片和视频的fragment,默认保存至DCMI文件夹中
 */
@SuppressLint("ValidFragment")
public class CameraCaptureRecordFragment extends BaseFragment implements OnCameraCaptureListener {


    private int mode; // 拍摄模式
    private long duration; // 拍摄时长

    private CameraOrientationListener cameraOrientationListener;


    //视图
    private FunSurfaceView surfaceView;
    private View viewBack;
    private CaptureButton captureButton;
    private ImageView viewSplashMode, viewSwitch, viewFocusView;


    public CameraCaptureRecordFragment(int mode, long duration) {
        this.mode = mode;
        this.duration = duration;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraOrientationListener = new CameraOrientationListener(getContext());
        cameraOrientationListener.enable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.camera_fragment_capture_record, container, false);
        surfaceView = view.findViewById(R.id.camera_capture_record_surface_view);
        viewBack = view.findViewById(R.id.camera_capture_record_btn_back);
        captureButton = view.findViewById(R.id.camera_capture_record_capture_button);
        viewSplashMode = view.findViewById(R.id.camera_capture_record_iv_splash);
        viewSwitch = view.findViewById(R.id.camera_capture_record_iv_switch);
        viewFocusView = view.findViewById(R.id.camera_capture_record_focus_view);

        // 初始化
        final Capture capture = new Capture(surfaceView);
        capture.setOnCameraCaptureListener(this);

        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        captureButton.setMode(mode);
        captureButton.setDuration(duration);

        // ——————————————————————————————————————点击事件——————————————————————————————————————————

        // 切换闪光灯
        viewSplashMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture.enableFlashLight();
            }
        });

        // 切换摄像头
        viewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture.switchCamera();
            }
        });

        // 点击拍摄
        captureButton.setOnProgressTouchListener(new CaptureButton.OnProgressTouchListener() {
            @Override
            public void onCapture() {
                capture.capturePhoto(cameraOrientationListener.getOrientation());
            }

            @Override
            public void onCaptureRecordStart() {
                capture.captureRecordStart(cameraOrientationListener.getOrientation());
            }

            @Override
            public void onCaptureRecordEnd() {
                capture.captureRecordEnd();
            }

            @Override
            public void onCaptureError(String message) {
                capture.captureRecordFailed();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

        });

        // 对焦
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        float X = event.getX();
                        float Y = event.getY();
                        Util.log("click    X：" + X + "    Y：" + Y);
                        capture.focus(X, Y);
                        break;
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        cameraOrientationListener.disable();
        cameraOrientationListener = null;
        super.onDestroy();
    }

    // —————————————————————————————————VIEW———————————————————————————————————————

    @Override
    public void onCameraSwitch(int mode) {
        if (mode == Camera.CameraInfo.CAMERA_FACING_BACK) {
            viewSplashMode.setVisibility(View.VISIBLE);
        } else if (mode == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            viewSplashMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onToggleSplash(String flashMode) {
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_capture_flash_off_24dp);
        }

        if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_camera_flash_auto_24dp);
        }

        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_capture_flash_on_24dp);
        }
    }

    @Override
    public void onFocusSuccess(float x, float y) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewFocusView.getLayoutParams();
        layoutParams.leftMargin = (int) x - Util.dip2px(getContext(), 35);
        layoutParams.topMargin = (int) y - Util.dip2px(getContext(), 35);
        viewFocusView.setLayoutParams(layoutParams);
        Util.scale(viewFocusView);
    }

    @Override
    public void onCapturePhoto(String photoPath) {
        // 拍照成功
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.camera_capture_main_framelayout,
                        new CameraCapturePreviewFragment(Util.Const.类型_照片, photoPath),
                        CameraCapturePreviewFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCaptureRecord(String filePath) {
        // 录像成功
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.camera_capture_main_framelayout,
                        new CameraCapturePreviewFragment(Util.Const.类型_视频, filePath),
                        CameraCapturePreviewFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
