package com.hyfun.camera;

import android.util.Log;

class Util {
    private static final boolean DEBUG = true;

    public static final void log(String message) {
        if (DEBUG) {
            Log.d("Camera", message);
        }
    }
}
