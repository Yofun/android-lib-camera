package com.hyfun.camera.audio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyfun.camera.R;
import com.hyfun.camera.widget.FunWaveView;

import java.io.File;

import tech.oom.idealrecorder.IdealRecorder;
import tech.oom.idealrecorder.StatusListener;

/**
 * Created by HyFun on 2019/12/5.
 * Email: 775183940@qq.com
 * Description:
 */
public class FunAudioDialogFragment extends DialogFragment {

    private Context context;
    private boolean isRecording = false;
    private RecordHandler handler;
    private long startTime;
    private int recordTime;
    private IdealRecorder idealRecorder;

    private MediaPlayer mediaPlayer;
    private String currentFilePath = null;

    private FunAudioRecordListener audioRecordListener;

    // 视图
    // 录制界面
    private View viewRecordView;
    private TextView viewRecordTextTitle;
    private FunWaveView viewRecordWaveView;
    private View viewRecordBtnStart;
    private View viewRecordNegative;
    // 播放界面
    private View viewPreviewView;
    private TextView viewPreviewTextTitle;
    private SeekBar viewPreviewSeekbar;
    private ImageView viewPreviewPlayStop;
    private View viewPreviewNegative;
    private View viewPreviewPositive;


    public static FunAudioDialogFragment newInstance() {
        return new FunAudioDialogFragment();
    }

    public void setAudioRecordListener(FunAudioRecordListener audioRecordListener) {
        this.audioRecordListener = audioRecordListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化
        // 录音
        idealRecorder = IdealRecorder.getInstance();
        idealRecorder.init(getContext());
        handler = new RecordHandler();
        setCancelable(false);
        // 设置样式等等
        // 播放
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fun_camera_audio_record_dialog_fragment, container, false);
        // 录制视图
        viewRecordView = view.findViewById(R.id.fun_camera_audio_record_view);
        viewRecordTextTitle = view.findViewById(R.id.fun_camera_audio_record_tv_title);
        viewRecordWaveView = view.findViewById(R.id.fun_camera_audio_record_wave_view);
        viewRecordBtnStart = view.findViewById(R.id.fun_camera_audio_record_view_start);
        viewRecordNegative = view.findViewById(R.id.fun_camera_audio_record_tv_negative);


        // 播放视图
        viewPreviewView = view.findViewById(R.id.fun_camera_audio_preview_view);
        viewPreviewTextTitle = view.findViewById(R.id.fun_camera_audio_preview_tv_title);
        viewPreviewSeekbar = view.findViewById(R.id.fun_camera_audio_preview_seekbar);
        viewPreviewPlayStop = view.findViewById(R.id.fun_camera_audio_preview_iv_play);
        viewPreviewNegative = view.findViewById(R.id.fun_camera_audio_preview_tv_negative);
        viewPreviewPositive = view.findViewById(R.id.fun_camera_audio_preview_tv_positive);

        // 初始化视图
        viewRecordView.setVisibility(View.VISIBLE);
        viewPreviewView.setVisibility(View.GONE);


        // ——————————————————————————————————————点击事件——————————————————————————————————————————
        /**
         * 点击开始录音
         */
        viewRecordBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isRecording) {
                        // 再次点击就停止
                        if (Math.abs(startTime - System.currentTimeMillis()) < 2000) {
                            Toast.makeText(context, "录制时间太短", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        stopRecord();
                    } else {
                        startTime = System.currentTimeMillis();
                        startRecord();
                    }
                } catch (Exception e) {
                }
            }
        });

        // 点击录音界面取消
        viewRecordNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    Toast.makeText(context, "正在录音", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
            }
        });


        // 点击播放 停止
        viewPreviewPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    viewPreviewTextTitle.setText("00:00/00:00");
                    viewPreviewSeekbar.setProgress(0);
                    viewPreviewPlayStop.setImageResource(R.drawable.func_camera_ic_audio_record_play_black_24dp);
                } else {
                    // 开始播放
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(currentFilePath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        viewPreviewSeekbar.setMax(mediaPlayer.getDuration());
                        handler.sendEmptyMessage(handler.AUDIO_PREVIEW_PLAYING);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.start();
                            }
                        });
                        viewPreviewPlayStop.setImageResource(R.drawable.func_camera_ic_audio_record_stop_black_24dp);
                    } catch (Exception e) {
                    }
                }
            }
        });


        viewPreviewSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(progress);
                } else {
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 点击播放界面取消
        viewPreviewNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 释放录音资源
                mediaPlayer.stop();
                mediaPlayer.reset();
                File file = new File(currentFilePath);
                if (file.exists()) {
                    file.delete();
                }
                // 重置数据
                viewPreviewTextTitle.setText("播放录音");
                viewPreviewSeekbar.setProgress(0);
                viewPreviewPlayStop.setImageResource(R.drawable.func_camera_ic_audio_record_play_black_24dp);
                // 切换
                viewRecordView.setVisibility(View.VISIBLE);
                viewPreviewView.setVisibility(View.GONE);
            }
        });

        // 点击播放界面使用
        viewPreviewPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                if (audioRecordListener != null) {
                    audioRecordListener.onAudioRecordResult(currentFilePath);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(false);
    }


    // ——————————————————————————————————————私有——————————————————————————————————————————

    /**
     * 开始录制
     */
    private void startRecord() {
        IdealRecorder.RecordConfig recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC,
                IdealRecorder.RecordConfig.SAMPLE_RATE_22K_HZ, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        idealRecorder
                .setRecordConfig(recordConfig)
                .setRecordFilePath(getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + File.separator + Util.randomName() + ".wav")
                .setMaxRecordTime(Integer.MAX_VALUE)
                .setVolumeInterval(200)
                .setStatusListener(statusListener);
        idealRecorder.start();
    }


    /**
     * 停止录制
     */
    private void stopRecord() {
        idealRecorder.stop();
    }


    private class RecordHandler extends Handler {
        // 录制开始
        public static final int AUDIO_RECORD_START = 6;
        // 正在录制
        public static final int AUDIO_RECORD_ING = 7;
        // 正在播放
        public static final int AUDIO_PREVIEW_PLAYING = 8;


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case AUDIO_RECORD_START:
                    viewRecordTextTitle.setText(Util.formatTime(recordTime));
                    handler.sendEmptyMessageDelayed(AUDIO_RECORD_ING, 1000);
                    break;
                case AUDIO_RECORD_ING:
                    if (isRecording) {
                        recordTime++;
                        viewRecordTextTitle.setText(Util.formatTime(recordTime));
                        handler.sendEmptyMessageDelayed(AUDIO_RECORD_ING, 1000);
                    } else {
                        // 录制结束
                        recordTime = 0;
                        viewRecordTextTitle.setText("点击录音");
                    }
                    break;
                case AUDIO_PREVIEW_PLAYING:
                    if (mediaPlayer.isPlaying()) {
                        int duration = mediaPlayer.getDuration();
                        int current = mediaPlayer.getCurrentPosition();
                        viewPreviewSeekbar.setProgress(current);
                        viewPreviewTextTitle.setText(Util.formatTime(current / 1000) + "/" + Util.formatTime(duration / 1000));
                        handler.sendEmptyMessageDelayed(AUDIO_PREVIEW_PLAYING, 100);
                    }
                    break;
            }
        }
    }


    /**
     * 录音监听
     */
    private StatusListener statusListener = new StatusListener() {
        @Override
        public void onStartRecording() {
            super.onStartRecording();
            viewRecordWaveView.setVisibility(View.VISIBLE);
            isRecording = true;
            handler.sendEmptyMessage(handler.AUDIO_RECORD_START);
        }

        @Override
        public void onRecordData(short[] data, int length) {
            for (int i = 0; i < length; i += 60) {
                viewRecordWaveView.addData(data[i]);
            }
        }

        @Override
        public void onFileSaveSuccess(String fileUri) {
            super.onFileSaveSuccess(fileUri);
            currentFilePath = fileUri;
            // 跳转到播放界面
            viewRecordView.setVisibility(View.GONE);
            viewPreviewView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStopRecording() {
            isRecording = false;
            recordTime = 0;
            viewRecordTextTitle.setText("点击录音");
        }
    };

}
