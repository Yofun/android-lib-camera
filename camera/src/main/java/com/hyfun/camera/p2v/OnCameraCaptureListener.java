package com.hyfun.camera.p2v;

/**
 * Created by HyFun on 2019/11/22.
 * Email: 775183940@qq.com
 * Description:
 */
interface OnCameraCaptureListener {
    void onCameraSwitch(int mode);

    void onToggleSplash(String flashMode);

    void onCapturePhoto(String photoPath);

    void onCaptureRecord(String filePath);

    void onError(Throwable throwable);
}
