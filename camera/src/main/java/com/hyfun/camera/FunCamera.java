package com.hyfun.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.hyfun.camera.widget.CaptureButton;

/**
 * Created by HyFun on 2019/10/15.
 * Email: 775183940@qq.com
 * Description:
 */
public class FunCamera {

    /**
     * 拍照
     *
     * @param activity
     * @param requestCode
     */
    public static final void capturePhoto(Activity activity, int requestCode) {
        capturePhoto2Record(activity, requestCode, CaptureButton.Mode.MODE_CAPTURE, 15000);
    }

    /**
     * 录像
     *
     * @param activity
     * @param requestCode
     * @param duration
     */
    public static final void captureRecord(Activity activity, int requestCode, long duration) {
        capturePhoto2Record(activity, requestCode, CaptureButton.Mode.MODE_RECORD, duration);
    }

    /**
     * 拍照+录像
     *
     * @param activity
     * @param requestCode
     * @param duration
     */
    public static final void capturePhoto2Record(Activity activity, int requestCode, long duration) {
        capturePhoto2Record(activity, requestCode, CaptureButton.Mode.MODE_CAPTURE_RECORD, duration);
    }

    /**
     * @param activity
     * @param mode     模式
     * @param duration 拍摄时长 单位毫秒
     */
    private static final void capturePhoto2Record(Activity activity, int requestCode, int mode, long duration) {
        Intent intent = new Intent(activity, CameraCaptureActivity.class);
        intent.putExtra(CameraCaptureActivity.MODE, mode);
        intent.putExtra(CameraCaptureActivity.DURATION, duration);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.activity_up_in, R.anim.activity_up_out);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, compat.toBundle());
    }
}
