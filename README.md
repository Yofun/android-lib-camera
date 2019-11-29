# Android-Library-Camera

使用自定义相机进行拍照、录像的library。

**说明**

- 拍摄的照片/视频默认存储在`内部存储/DCIM/Camera/`下面，文件命名格式为`yyyyMMdd_HHmmss`。
拍摄视频最大的时间限制为`10分钟`，超过该时间可能会出现异常。

- 已解决预览画面拉伸问题，已解决不同的方向拍摄照片/视频的问题。

- 拍照默认使用`1920 * 1080`的分辨率，没有则会选择其他`16:9`的分辨率。

- 拍摄视频默认使用`16:9`的分辨率，帧数为`30`帧，如果没有，则选择一个小于30帧的帧数。码率使用`3M+`的码率，以保证视频不会太模糊，也保证视频大小不会太大。

- 图片压缩：library中引用鲁班压缩，拍摄的原图存储在`内部存储/DCIM/Camera/`下，并会刷新图库。压缩图片会存储在`内部存储/Android/data/应用包名/DCIM`下面，不会刷新图库。



| 作者 | 联系方式 |
| -- | -- |
| HyFun | QQ：775183940 |

[demo.apk下载地址](app/debug/app-debug.apk)

## 一、依赖

**Project build.gradle中**

```
allprojects {
    repositories {
        jcenter()
        maven {
            url "https://jitpack.io"
        }
    }
}
```

Module build.gradle中

```
dependencies {
    implementation 'com.github.HyFun:Android-Library-Camera:{last-version}'
 }
```


## 二、注意事项

**Android 6.0 运行时权限处理**

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
```

## 三、使用方法

### 启动

- 拍照
    ```java
    new RxPermissions(this)
            .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        FunCamera.capturePhoto(MainActivity.this, 10);
                    } else {
                        Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    ```


- 录像
    ```java
    new RxPermissions(this)
            .request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            )
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        FunCamera.captureRecord(MainActivity.this, 20, 10000);
                    } else {
                        Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    ```

- 拍照+录像

    ```java
    new RxPermissions(this)
            .request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            )
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        FunCamera.capturePhoto2Record(MainActivity.this, 30, 10000);
                    } else {
                        Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    ```

### 回调

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
        String path = data.getStringExtra(FunCamera.DATA);
        String pathOrigin = data.getStringExtra(FunCamera.DATA_ORIGIN);
        StringBuilder sb = new StringBuilder();
        sb.append("压缩后地址：" + path + "\n");
        sb.append("原图的地址：" + pathOrigin);
        textView.setText(sb.toString());
    }
}
```


## 四、TODO

- 拍完照后跳转到预览的时候会闪一下黑屏（看着难受，微信都不带动的~）

