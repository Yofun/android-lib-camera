package com.hyfun.camera.listener;

/**
 * Created by HyFun on 2019/11/22.
 * Email: 775183940@qq.com
 * Description:
 */
public interface OnCameraCaptureListener {
    public void onCameraSwitch(int mode);

    public void onToggleSplash(String flashMode);

    void onCapturePhoto(String photoPath);

    void onCaptureRecord(String filePath);

    public void onError(Throwable throwable);
}
