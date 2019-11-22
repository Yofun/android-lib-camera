package com.hyfun.camera.p2v;

import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hyfun.camera.listener.OnCameraCaptureListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by HyFun on 2019/10/20.
 * Email: 775183940@qq.com
 * Description: 拍摄照片  录制视频的
 */
class Capture {
    private SurfaceView surfaceView;

    public Capture(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;

        // 初始化
        if (Camera.getNumberOfCameras() > 1) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;    // 后置摄像头
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;   // 前置摄像头
        }
        surfaceView.getHolder().addCallback(surfaceCallBack);
    }

    private OnCameraCaptureListener onCameraCaptureListener;

    public void setOnCameraCaptureListener(OnCameraCaptureListener onCameraCaptureListener) {
        this.onCameraCaptureListener = onCameraCaptureListener;
    }

    // 摄像头id
    private int cameraId;
    // 摄像机对象
    private Camera camera;
    // 预览信息
    private PreviewInfo previewInfo;
    // 闪光灯模式
    public static final int FLASH_MODE_OFF = 0;
    public static final int FLASH_MODE_ON = 1;
    public int flashType = FLASH_MODE_OFF;

    // 是否正在预览
    private boolean isPreviewing = false;
    // 是否正在录像
    private boolean isRecording = false;


    // —————————————————————————————————公有方法—————————————————————————————————————————

    public void startPreview() {
        isPreviewing = false;
        // 开始计算尺寸
        previewInfo = new PreviewInfo(surfaceView.getContext(), camera);
        previewInfo.notifyDataChanged();
        setCameraParameter();
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
        } catch (IOException e) {
            destroy();
            return;
        }
        camera.startPreview();
        isPreviewing = true;
//        mSizeSurfaceView.setVideoDimension(previewHeight, previewWidth);
//        mSizeSurfaceView.requestLayout();
    }

    public void enableFlashLight() {
        if (!isPreviewing) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("预览的时候才能操作闪光灯"));
            }
            return;
        }
        // 先判断是否是后置摄像头
        if (cameraId != Camera.CameraInfo.CAMERA_FACING_BACK) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("只有后置摄像头才能开启闪光灯"));
            }
            return;
        }
        // 判断是否正在录像
        if (isRecording) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("正在录像，无法操作"));
            }
            return;
        }




    }

    /**
     * 销毁一切
     */
    public void destroy() {
        if (camera != null) {
            if (isPreviewing) {
                camera.stopPreview();
                isPreviewing = false;
                camera.setPreviewCallback(null);
                camera.setPreviewCallbackWithBuffer(null);
            }
            camera.release();
            camera = null;
        }
    }


    // —————————————————————————————————私有方法—————————————————————————————————————————

    /**
     * 设置camera 的 Parameters
     */
    private void setCameraParameter() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewInfo.getPreviewWidth(), previewInfo.getPreviewHeight());
        parameters.setPictureSize(previewInfo.getPictureWidth(), previewInfo.getPictureHeight());
        parameters.setJpegQuality(100);
        if (Build.VERSION.SDK_INT < 9) {
            return;
        }
        List<String> supportedFocus = parameters.getSupportedFocusModes();
        boolean isHave = supportedFocus == null ? false :
                supportedFocus.indexOf(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) >= 0;
        if (isHave) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        parameters.setFlashMode(flashType == FLASH_MODE_ON ?
                Camera.Parameters.FLASH_MODE_TORCH :
                Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }


    /**
     * surface callback
     */
    private SurfaceHolder.Callback surfaceCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (holder.getSurface() == null) {
                return;
            }

            if (camera == null) {
                camera = Camera.open(cameraId);
            }

            camera.stopPreview();
            isPreviewing = false;
            // 开启预览
            startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            destroy();
        }
    };
}
