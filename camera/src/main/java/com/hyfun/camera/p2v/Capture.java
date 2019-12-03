package com.hyfun.camera.p2v;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceHolder;

import com.hyfun.camera.widget.FunSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HyFun on 2019/10/20.
 * Email: 775183940@qq.com
 * Description: 拍摄照片  录制视频的
 */
class Capture {
    private FunSurfaceView surfaceView;

    public Capture(FunSurfaceView surfaceView) {
        this.surfaceView = surfaceView;

        // 初始化
        if (Camera.getNumberOfCameras() > 1) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;    // 后置摄像头
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;   // 前置摄像头
        }
        //  surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
    private String currentFlashMode = Camera.Parameters.FLASH_MODE_OFF; // 默认闪光灯关闭
    // 对焦模式
    private String currentFocusMode;

    // 当前视频的地址
    private String fileVideo;
    // 录制视频器
    private MediaRecorder mediaRecorder;

    // 是否正在预览
    private boolean isPreviewing = false;
    // 是否正在录像
    private boolean isRecording = false;


    // —————————————————————————————————公有方法—————————————————————————————————————————

    /**
     * 开始预览画面
     * 每次预览需要重新计算预览尺寸
     */
    private void startPreview() {
        // 先destory
        destroy();
        // 打开
        camera = Camera.open(cameraId);

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
        surfaceView.setUserSize(true);
        surfaceView.setVideoDimension(previewInfo.getPreviewHeight(), previewInfo.getPreviewWidth());
        surfaceView.requestLayout();
    }

    /**
     * @param x
     * @param y
     */
    public void focus(final float x, final float y) {
        if (!isPreviewing) {
            return;
        }
        Rect focusRect = calculateTapArea(x, y);
        //一定要首先取消，否则无法再次开启
        camera.cancelAutoFocus();
        Camera.Parameters params = camera.getParameters();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            //focus areas not supported
        }
        //首先保存原来的对焦模式，然后设置为macro，对焦回调后设置为保存的对焦模式
        final String currentFocusMode = params.getFocusMode();
        List<String> supportedFocusModes = params.getSupportedFocusModes();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        try {
            camera.setParameters(params);
        } catch (Exception e) {
            Util.e(e);
        }
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                //回调后 还原模式
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
                if (success) {
                    Util.log("focus success");
                    if (onCameraCaptureListener != null) {
                        onCameraCaptureListener.onFocusSuccess(x, y);
                    }
                }
            }
        });

    }


    /**
     * 切换闪光灯状态
     * <p>
     * 切换闪光灯的时候检查闪光灯是否可用
     */
    public void enableFlashLight() {
        if (!isPreviewing) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("预览的时候才能操作闪光灯"));
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

        // 再切换闪光灯
        if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            currentFlashMode = Camera.Parameters.FLASH_MODE_ON;
        } else if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            currentFlashMode = Camera.Parameters.FLASH_MODE_TORCH;
        } else if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            currentFlashMode = Camera.Parameters.FLASH_MODE_OFF;
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(currentFlashMode);
        camera.setParameters(parameters);
        if (onCameraCaptureListener != null) {
            onCameraCaptureListener.onToggleSplash(currentFlashMode);
        }
    }


    /**
     * 切换镜头
     */
    public void switchCamera() {

        int cameraNum = Camera.getNumberOfCameras();
        if (cameraNum < 2) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("您的手机不支持前置拍摄"));
            }
            return;
        }
        if (!isPreviewing) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("非预览状态无法切换摄像头"));
            }
            return;
        }

        if (isRecording) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("正在录像，无法切换摄像头"));
            }
            return;
        }
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        // 重新预览
        startPreview();
    }

    /**
     * 拍照
     */
    public void capturePhoto(final int orientation) {
        if (!isPreviewing) {
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onError(new Exception("非预览状态，无法拍照"));
            }
            return;
        }
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    // int orientation = ((TakePhotoVideoActivity) mActivity).getOrientation();
                    // 将图片保存在 DIRECTORY_DCIM 内存卡中
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Matrix matrix = new Matrix();
                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        matrix.setRotate(-90 - orientation);
                        matrix.postScale(-1, 1);
                    } else {
                        matrix.setRotate(90 + orientation);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    // 创建文件
                    String parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";
                    File mediaStorageDir = new File(parentPath);
                    if (!mediaStorageDir.exists()) {
                        mediaStorageDir.mkdirs();
                    }
                    File mediaFile = new File(mediaStorageDir.getPath() + File.separator + Util.randomName() + ".jpg");
                    FileOutputStream stream = new FileOutputStream(mediaFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                    // 回调
                    if (onCameraCaptureListener != null) {
                        onCameraCaptureListener.onCapturePhoto(mediaFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    if (onCameraCaptureListener != null) {
                        onCameraCaptureListener.onError(e);
                    }
                }
            }
        });
    }


    /**
     * 开始录制
     */
    public void captureRecordStart(int orientation) {
        // 生成视频文件
        String parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + File.separator + "Camera";
        File parentFile = new File(parentPath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        fileVideo = parentPath + File.separator + Util.randomName() + ".mp4";
//        File file = new File(fileVideo);
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        captureRecordEnd();
        camera.stopPreview();
        camera.unlock();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // 前置
            mediaRecorder.setOrientationHint(270 - orientation);
        } else {
            mediaRecorder.setOrientationHint((90 + orientation) % 360);
        }

        // 自定义的设置mediaeecorder 这里设置视频质量最低  录制出来的视频体积很小 对质量不是要求不高的可以使用
        try {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            //设置分辨率，应设置在格式和编码器设置之后
            mediaRecorder.setVideoSize(previewInfo.getVideoWidth(), previewInfo.getVideoHeight());
//            mediaRecorder.setVideoEncodingBitRate(52 * defaultVideoFrameRate * 1024);
//            mediaRecorder.setVideoEncodingBitRate(1400 * 1024);
            mediaRecorder.setVideoEncodingBitRate(3000 * 1024);
            mediaRecorder.setAudioEncodingBitRate(65536);
            mediaRecorder.setAudioSamplingRate(44100);
        } catch (Exception e) {
            Util.e(e);
            // 如果出错，则使用
            mediaRecorder.setProfile(Util.getBestCamcorderProfile(cameraId));
        }


        mediaRecorder.setVideoFrameRate(previewInfo.getVideoRate());
        // 设置最大录制时间
        mediaRecorder.setMaxFileSize(30 * 1024 * 1024);
        mediaRecorder.setMaxDuration(10 * 60 * 60 * 1000);
        mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        mediaRecorder.setOutputFile(fileVideo);

        // 一切就绪
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        isRecording = true;
    }


    /**
     * 录制完成
     */
    public void captureRecordEnd() {
        if (!isRecording) {
            return;
        }
        if (mediaRecorder == null) {
            return;
        }
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onCaptureRecord(fileVideo);
            }
        } catch (Exception e) {
            Util.e(e);
            isRecording = false;
            // 删除已经创建的视频文件
            File file = new File(fileVideo);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 录制视频失败
     */
    public void captureRecordFailed() {
        if (!isRecording) {
            return;
        }
        if (mediaRecorder == null) {
            return;
        }
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (Exception e) {
            Util.e(e);
        }
        isRecording = false;
        // 删除已经创建的视频文件
        File file = new File(fileVideo);
        if (file.exists()) {
            file.delete();
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
        // 设置预览的尺寸
        parameters.setPreviewSize(previewInfo.getPreviewWidth(), previewInfo.getPreviewHeight());
        parameters.setPictureSize(previewInfo.getPictureWidth(), previewInfo.getPictureHeight());
        parameters.setJpegQuality(100);
        // 获取支持的对焦模式
        List<String> supportedFocus = parameters.getSupportedFocusModes();
        if (supportedFocus != null && supportedFocus.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        // 设置闪光灯模式之前检查该模式是否可用
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null) {
            flashModes = new ArrayList<>();
        }
        if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            // 设置闪光灯模式
            parameters.setFlashMode(currentFlashMode);
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onToggleSplash(currentFlashMode);
            }
        } else {
            // 该设备不支持闪光灯
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            if (onCameraCaptureListener != null) {
                onCameraCaptureListener.onToggleSplash(null);
            }
        }

        camera.setParameters(parameters);
    }


    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private Rect calculateTapArea(float x, float y) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * 1.0f).intValue();
        int centerY = 0;
        int centerX = 0;
        centerY = (int) (x / surfaceView.getMeasuredWidth() * 2000 - 1000);
        centerX = (int) (y / surfaceView.getMeasuredHeight() * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    /**
     * @param x
     * @param min
     * @param max
     * @return
     */
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    /**
     * surface callback
     */
    private SurfaceHolder.Callback surfaceCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Util.log("surfaceCallBack>>>>>>surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Util.log("surfaceCallBack>>>>>>surfaceChanged");
            if (holder.getSurface() == null) {
                return;
            }
            // 开始预览
            startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Util.log("surfaceCallBack>>>>>>surfaceDestroyed");
            destroy();
        }
    };
}
