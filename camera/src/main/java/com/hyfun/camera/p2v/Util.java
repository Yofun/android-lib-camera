package com.hyfun.camera.p2v;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class Util {

    private static final String TAG = "FunCamera::";

    public static final void log(String message) {
        Log.d(TAG, message);
    }

    public static final void e(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
    }


    interface Const {
        int 类型_照片 = 0;
        int 类型_视频 = 1;
    }


    /**
     * 设置内容全屏,即内容延伸至状态栏底部,状态栏文字还在
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            // 适配刘海屏幕
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(lp);
            }

            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //Activity全屏显示，但导航栏不会被隐藏覆盖，导航栏依然可见，Activity底部布局部分会被导航栏遮住。
            uiFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            // 隐藏状态栏
            uiFlags |= View.INVISIBLE;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


            window.getDecorView().setSystemUiVisibility(uiFlags);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置透明状态栏,这样才能让 ContentView 向上
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 生成文件名称
     *
     * @return
     */
    public static String randomName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(new Date());
    }

    /**
     * 通知系统相册更新了
     */
    public static final void notifyAlbumDataChanged(Context context, File file) {
        //通知相册更新
        Uri uri = Uri.fromFile(file);
        // 通知图库更新
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scannerIntent);
    }


    /**
     * 解决录像时清晰度问题
     * <p>
     * 视频清晰度顺序 High 1080 720 480 cif qvga gcif 详情请查看 CamcorderProfile.java
     * 在12秒mp4格式视频大小维持在1M左右时,以下四个选择效果最佳
     * <p>
     * 不同的CamcorderProfile.QUALITY_ 代表每帧画面的清晰度,
     * 变换 profile.videoBitRate 可减少每秒钟帧数
     *
     * @param cameraID 前摄 Camera.CameraInfo.CAMERA_FACING_FRONT /后摄 Camera.CameraInfo.CAMERA_FACING_BACK
     * @return
     */
    public static CamcorderProfile getBestCamcorderProfile(int cameraID) {
        CamcorderProfile profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_LOW);
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_720P)) {
            //对比上面480 这个选择 动作大时马赛克!!
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_720P);
            profile.videoBitRate = profile.videoBitRate / 10;
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_480P)) {
            //对比下面720 这个选择 每帧不是很清晰
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_480P);
            profile.videoBitRate = profile.videoBitRate / 2;
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_CIF)) {
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_CIF);
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_QVGA)) {
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_QVGA);
            return profile;
        }
        return profile;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * focus view的缩放动画
     */
    public static void scale(final View view) {
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);

        AnimationSet set = new AnimationSet(false);


        ScaleAnimation scale = new ScaleAnimation(1.6f, 1.0f, 1.6f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(400);
        set.addAnimation(scale);

        AlphaAnimation alpha = new AlphaAnimation(0f, 1.0f);
        alpha.setDuration(400);
        set.addAnimation(alpha);


        view.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


}
