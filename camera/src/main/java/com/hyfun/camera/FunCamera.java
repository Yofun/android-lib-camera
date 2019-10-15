package com.hyfun.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

/**
 * Created by HyFun on 2019/10/15.
 * Email: 775183940@qq.com
 * Description:
 */
public class FunCamera {

    /**
     *
     * @param activity
     * @param requestCode
     */
    public static final void capturePhoto(Activity activity, int requestCode) {
        capturePhoto2Record(activity, requestCode, Const.MODE_拍照, 10);
    }


    /**
     * @param activity
     * @param mode     模式
     * @param duration 拍摄时长 单位秒
     */
    public static final void capturePhoto2Record(Activity activity, int requestCode, int mode, int duration) {
        Intent intent = new Intent(activity, CameraCaptureActivity.class);
        intent.putExtra(CameraCaptureActivity.MODE, mode);
        intent.putExtra(CameraCaptureActivity.DURATION, duration);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.activity_up_in, R.anim.activity_up_out);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, compat.toBundle());
    }
}
