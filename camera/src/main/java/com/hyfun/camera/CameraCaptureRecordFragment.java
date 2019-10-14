package com.hyfun.camera;

import android.annotation.SuppressLint;

/**
 * Created by HyFun on 2019/10/14.
 * Email: 775183940@qq.com
 * Description: 用于拍摄照片和视频的fragment,默认保存至DCMI文件夹中
 */
@SuppressLint("ValidFragment")
public class CameraCaptureRecordFragment extends BaseFragment {
    private int mode; // 拍摄模式
    private long duration; // 拍摄时长

    public CameraCaptureRecordFragment(int mode, long duration) {
        this.mode = mode;
        this.duration = duration;
    }
}
