package com.hyfun.camera.audio;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HyFun on 2019/12/6.
 * Email: 775183940@qq.com
 * Description:
 */
class Util {
    private Util() {
    }

    /**
     * 处理时间
     *
     * @param time 单位:秒
     * @return 00:53
     */
    public static String formatTime(int time) {
        int min = time / 60;
        int sec = time % 60;
        return String.format("%02d", min) + ":" + String.format("%02d", sec);
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
}
