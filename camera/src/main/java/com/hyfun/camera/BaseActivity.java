package com.hyfun.camera;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by HyFun on 2019/10/14.
 * Email: 775183940@qq.com
 * Description:
 */
class BaseActivity extends AppCompatActivity {

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_down_out);
    }
}
