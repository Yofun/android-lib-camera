package com.hyfun.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.hyfun.camera.audio.FunAudioDialogFragment;
import com.hyfun.camera.audio.FunAudioRecordListener;
import com.hyfun.camera.p2v.CameraCaptureActivity;
import com.hyfun.camera.widget.CaptureButton;

/**
 * Created by HyFun on 2019/10/15.
 * Email: 775183940@qq.com
 * Description:
 */
public class FunCamera {

    // 压缩后的文件路径
    public static final String DATA = "DATA";
    // 原文件路径
    public static final String DATA_ORIGIN = "DATA_ORIGIN";

    /**
     * 拍照
     *
     * @param activity
     * @param requestCode
     */
    public static final void capturePhoto(Activity activity, int requestCode) {
        Intent intent = capturePhoto2Record(activity, requestCode, CaptureButton.Mode.MODE_CAPTURE, 15000);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 录像
     *
     * @param activity
     * @param requestCode
     * @param duration
     */
    public static final void captureRecord(Activity activity, int requestCode, long duration) {
        Intent intent = capturePhoto2Record(activity, requestCode, CaptureButton.Mode.MODE_RECORD, duration);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 拍照+录像
     *
     * @param activity
     * @param requestCode
     * @param duration
     */
    public static final void capturePhoto2Record(Activity activity, int requestCode, long duration) {
        Intent intent = capturePhoto2Record(activity, requestCode, CaptureButton.Mode.MODE_CAPTURE_RECORD, duration);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 录音
     *
     * @param activity
     * @param funAudioRecordListener
     */
    public static final void captureAudioRecord(AppCompatActivity activity, FunAudioRecordListener funAudioRecordListener) {
        FunAudioDialogFragment funAudioDialogFragment = FunAudioDialogFragment.newInstance();
        funAudioDialogFragment.setAudioRecordListener(funAudioRecordListener);
        funAudioDialogFragment.show(activity.getSupportFragmentManager(), "FunAudioDialogFragment");
    }


    // —————————————————————————————————————————————————————————————————————————————————————————————

    /**
     * @param activity
     * @param mode     模式
     * @param duration 拍摄时长 单位毫秒
     */
    private static final Intent capturePhoto2Record(Activity activity, int requestCode, int mode, long duration) {
        Intent intent = new Intent(activity, CameraCaptureActivity.class);
        intent.putExtra(CameraCaptureActivity.MODE, mode);
        intent.putExtra(CameraCaptureActivity.DURATION, duration);
        return intent;
    }
}
