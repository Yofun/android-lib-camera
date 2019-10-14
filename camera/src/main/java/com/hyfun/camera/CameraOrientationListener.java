package com.hyfun.camera;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

/**
 * Created by HyFun on 2019/10/14.
 * Email: 775183940@qq.com
 * Description:
 */
class CameraOrientationListener extends OrientationEventListener {


    private int mCurrentNormalizedOrientation;
    private int mRememberedNormalOrientation = ORIENTATION_UNKNOWN;

    public CameraOrientationListener(Context context) {
        super(context, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onOrientationChanged(final int orientation) {
        if (orientation != ORIENTATION_UNKNOWN) {
            mCurrentNormalizedOrientation = normalize(orientation);
        }
        // 避免重复
        if (mRememberedNormalOrientation != mCurrentNormalizedOrientation) {
            mRememberedNormalOrientation = mCurrentNormalizedOrientation;
        }
    }

    private int normalize(int degrees) {
        if (degrees > 315 || degrees <= 45) {
            return 0;
        }

        if (degrees > 45 && degrees <= 135) {
            return 90;
        }

        if (degrees > 135 && degrees <= 225) {
            return 180;
        }

        if (degrees > 225 && degrees <= 315) {
            return 270;
        }
        throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
    }

    public final int getOrientation() {
        return mRememberedNormalOrientation;
    }
}
