package com.hyfun.camera.p2v;

import android.content.Context;
import android.hardware.Camera;
import android.view.WindowManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by HyFun on 2019/11/22.
 * Email: 775183940@qq.com
 * Description:
 */
public class PreviewInfo {
    private int previewWidth; // 预览宽度
    private int previewHeight; // 预览高度

    private int pictureWidth; // 拍照宽度
    private int pictureHeight; // 拍照高度

    private int videoWidth; // 拍摄视频宽度
    private int videoHeight; // 拍摄视频高度

    private int videoRate; // 视频帧率


    private Context context;
    private Camera camera;

    public PreviewInfo(Context context, Camera camera) {
        this.context = context;
        this.camera = camera;
    }


    /**
     * 计算
     */
    public void notifyDataChanged() {
        Util.log("===============================================");
        boolean hasSupportRate = false;
        List<Integer> supportedPreviewFrameRates = getSupportedPreviewFrameRates(camera);
        Util.log("supportRate::" + supportedPreviewFrameRates.toString());
        if (supportedPreviewFrameRates != null
                && supportedPreviewFrameRates.size() > 0) {
            for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                int supportRate = supportedPreviewFrameRates.get(i);
                if (supportRate == 30) {
                    videoRate = 30;
                    hasSupportRate = true;
                    break;
                }
            }
            if (!hasSupportRate) {
                for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                    int supportRate = supportedPreviewFrameRates.get(i);
                    if (supportRate <= 30) {
                        videoRate = supportRate;
                        hasSupportRate = true;
                        break;
                    }
                }
            }

            if (!hasSupportRate) {
                videoRate = supportedPreviewFrameRates.get(supportedPreviewFrameRates.size() - 1);
                hasSupportRate = true;
            }
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 设置预览时的宽高
        {
            List<Camera.Size> resolutionList = getSupportedPreviewSizes(camera);
            if (resolutionList != null && resolutionList.size() > 0) {
                Camera.Size previewSize = null;
                boolean hasSize = false;
                // 手机支持的分辨率 列表
                Util.log("--------support preview list-----------");
                for (int i = 0; i < resolutionList.size(); i++) {
                    Camera.Size size = resolutionList.get(i);
                    Util.log("width:" + size.width + "   height:" + size.height);
                }

                if (!hasSize)
                    for (int i = 0; i < resolutionList.size(); i++) {
                        Camera.Size size = resolutionList.get(i);
                        if (size != null && size.width == 1920 && size.height == 1080) {
                            previewSize = size;
                            previewWidth = previewSize.width;
                            previewHeight = previewSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < resolutionList.size(); i++) {
                        Camera.Size size = resolutionList.get(i);
                        if (size != null && size.width == height && size.height == width) {
                            previewSize = size;
                            previewWidth = previewSize.width;
                            previewHeight = previewSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < resolutionList.size(); i++) {
                        Camera.Size size = resolutionList.get(i);
                        if (size != null && size.height == width) {
                            previewSize = size;
                            previewWidth = previewSize.width;
                            previewHeight = previewSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                //如果相机不支持上述分辨率，使用中分辨率
                if (!hasSize) {
                    int mediumResolution = resolutionList.size() / 2;
                    if (mediumResolution >= resolutionList.size())
                        mediumResolution = resolutionList.size() - 1;
                    previewSize = resolutionList.get(mediumResolution);
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                }
            }
        }

        // 设置拍照照片的宽高
        {
            List<Camera.Size> pictureSizeList = getSupportedPictureSizes(camera);
            if (pictureSizeList != null && !pictureSizeList.isEmpty()) {
                Camera.Size pictureSize = null;
                boolean hasSize = false;
                // 手机支持的分辨率 列表
                Util.log("---------support picture list----------");
                for (int i = 0; i < pictureSizeList.size(); i++) {
                    Camera.Size size = pictureSizeList.get(i);
                    Util.log("width:" + size.width + "    height:" + size.height);
                }

                if (!hasSize)
                    for (int i = 0; i < pictureSizeList.size(); i++) {
                        Camera.Size size = pictureSizeList.get(i);
                        if (size != null && size.width == 1920 && size.height == 1080) {
                            pictureSize = size;
                            pictureWidth = pictureSize.width;
                            pictureHeight = pictureSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < pictureSizeList.size(); i++) {
                        Camera.Size size = pictureSizeList.get(i);
                        float scale = (float) size.height / (float) size.width;
                        if (size != null && scale == 0.5625f) {
                            pictureSize = size;
                            pictureWidth = pictureSize.width;
                            pictureHeight = pictureSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < pictureSizeList.size(); i++) {
                        Camera.Size size = pictureSizeList.get(i);
                        if (size != null && size.width == height && size.height == width) {
                            pictureSize = size;
                            pictureWidth = pictureSize.width;
                            pictureHeight = pictureSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < pictureSizeList.size(); i++) {
                        Camera.Size size = pictureSizeList.get(i);
                        if (size != null && size.height == width) {
                            pictureSize = size;
                            pictureWidth = pictureSize.width;
                            pictureHeight = pictureSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                //如果相机不支持上述分辨率，使用中分辨率
                if (!hasSize) {
                    int mediumResolution = pictureSizeList.size() / 2;
                    if (mediumResolution >= pictureSizeList.size())
                        mediumResolution = pictureSizeList.size() - 1;
                    pictureSize = pictureSizeList.get(mediumResolution);
                    pictureWidth = pictureSize.width;
                    pictureHeight = pictureSize.height;
                }
            }
        }
        // 设置拍摄视频时的宽高
        {
            List<Camera.Size> videoSizeList = getSupportedVideoSizes(camera);
            if (videoSizeList != null && !videoSizeList.isEmpty()) {
                Camera.Size videoSize = null;
                boolean hasSize = false;
                Util.log("---------support video list----------");
                for (int i = 0; i < videoSizeList.size(); i++) {
                    Camera.Size size = videoSizeList.get(i);
                    Util.log("width:" + size.width + "   height:" + size.height + "   scale:" + ((float) size.height / (float) size.width));
                }

                if (!hasSize)
                    for (int i = 0; i < videoSizeList.size(); i++) {
                        Camera.Size size = videoSizeList.get(i);
                        float scale = (float) size.height / (float) size.width;
                        if (size != null && size.width >= 960 && scale == 0.5625f) {
                            videoSize = size;
                            videoWidth = videoSize.width;
                            videoHeight = videoSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < videoSizeList.size(); i++) {
                        Camera.Size size = videoSizeList.get(i);
                        if (size != null && size.width == 1280 && size.height == 720) {
                            videoSize = size;
                            videoWidth = videoSize.width;
                            videoHeight = videoSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < videoSizeList.size(); i++) {
                        Camera.Size size = videoSizeList.get(i);
                        if (size != null && size.width == height && size.height == width) {
                            videoSize = size;
                            videoWidth = videoSize.width;
                            videoHeight = videoSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < videoSizeList.size(); i++) {
                        Camera.Size size = videoSizeList.get(i);
                        if (size != null && size.height == width) {
                            videoSize = size;
                            videoWidth = videoSize.width;
                            videoHeight = videoSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                if (!hasSize)
                    for (int i = 0; i < videoSizeList.size(); i++) {
                        Camera.Size size = videoSizeList.get(i);
                        float scale = (float) size.height / (float) size.width;
                        if (size != null && scale == 0.5625f) {
                            videoSize = size;
                            videoWidth = videoSize.width;
                            videoHeight = videoSize.height;
                            hasSize = true;
                            break;
                        }
                    }

                //如果相机不支持上述分辨率，使用中分辨率
                if (!hasSize) {
                    int mediumResolution = videoSizeList.size() / 2;
                    if (mediumResolution >= videoSizeList.size())
                        mediumResolution = videoSizeList.size() - 1;
                    videoSize = videoSizeList.get(mediumResolution);
                    videoWidth = videoSize.width;
                    videoHeight = videoSize.height;
                }
            }
        }

        Util.log("preview wh:" + previewWidth + "," + previewHeight + "    picture wh:" + pictureWidth + "," + pictureHeight + "    video wh:" + videoWidth + "," + videoHeight + "    videoRate:" + videoRate);
        Util.log("===============================================");
    }


    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public int getPictureWidth() {
        return pictureWidth;
    }

    public int getPictureHeight() {
        return pictureHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public int getVideoRate() {
        return videoRate;
    }


    // —————————————————————————————————————————————————私有方法———————————————————————————————————————————————————————————

    /**
     * 获取camera支持的预览尺寸集合
     *
     * @param camera
     * @return
     */
    private List<Integer> getSupportedPreviewFrameRates(Camera camera) {
        List<Integer> supportedPreviewFrameRates = camera.getParameters().getSupportedPreviewFrameRates();
        Collections.sort(supportedPreviewFrameRates, new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return t1 - integer;
            }
        });
        return supportedPreviewFrameRates;
    }


    /**
     * 获取手机摄像头支持预览的尺寸集合
     *
     * @param camera
     * @return
     */
    private List<Camera.Size> getSupportedPreviewSizes(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.height != rhs.height) {
                    return lhs.height - rhs.height;
                } else {
                    return lhs.width - rhs.width;
                }
            }
        });
        return previewSizes;
    }

    /**
     * 获取手机摄像头支持拍摄照片像素的集合
     *
     * @param camera
     * @return
     */
    private List<Camera.Size> getSupportedPictureSizes(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPictureSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.height != rhs.height)
                    return rhs.height - lhs.height;
                else
                    return rhs.width - lhs.width;
            }
        });
        return previewSizes;
    }


    /**
     * 获取摄像头支持拍摄视频的像素集合
     *
     * @param camera
     * @return
     */
    private List<Camera.Size> getSupportedVideoSizes(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedVideoSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.height != rhs.height)
                    return lhs.height - rhs.height;
                else
                    return lhs.width - rhs.width;
            }
        });
        return previewSizes;
    }
}
