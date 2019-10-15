package com.hyfun.camera;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyfun.camera.widget.CaptureButton;

/**
 * Created by HyFun on 2019/10/14.
 * Email: 775183940@qq.com
 * Description: 用于拍摄照片和视频的fragment,默认保存至DCMI文件夹中
 */
@SuppressLint("ValidFragment")
public class CameraCaptureRecordFragment extends BaseFragment {


    private int mode; // 拍摄模式
    private long duration; // 拍摄时长


    //
    private CaptureButton captureButton;


    public CameraCaptureRecordFragment(int mode, long duration) {
        this.mode = mode;
        this.duration = duration;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.camera_fragment_capture_record, container, false);
        captureButton = view.findViewById(R.id.camera_capture_record_capture_button);
        captureButton.setOnProgressTouchListener(new CaptureButton.OnProgressTouchListener() {
            @Override
            public void onClick(CaptureButton photoButton) {
                Util.log("点击了");
            }

            @Override
            public void onLongClick(CaptureButton photoButton) {
                Util.log("长按按下");
            }

            @Override
            public void onLongClickUp(CaptureButton photoButton) {
                Util.log("长按抬起");
            }

            @Override
            public void onFinish() {
                Util.log("录制结束了");
            }
        });

        return view;
    }
}
