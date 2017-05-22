package com.coolwin.XYT;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

public class VideoPlayMainActivity extends Activity {
	private final String TAG = "main";
	private SurfaceView sv;
	private MediaPlayer mediaPlayer;
	private String path;
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoplay_main);
		sv = (SurfaceView) findViewById(R.id.sv);
		imageView= (ImageView) findViewById(R.id.play_video);
		// 为SurfaceHolder添加回调
		sv.getHolder().addCallback(callback);
		// 4.0版本之下需要设置的属性
		// 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
		// sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		path = getIntent().getStringExtra("vidoepath");
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
				imageView.setVisibility(View.GONE);
			}
		});
	}
	private Callback callback = new Callback() {
		// SurfaceHolder被修改的时候回调
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG, "SurfaceHolder 被销毁");
			// 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i(TAG, "SurfaceHolder 被创建");
				// 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
				play();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
			Log.i(TAG, "SurfaceHolder 大小被改变");
		}

	};


	/*
	 * 停止播放
	 */
	protected void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * 开始播放
	 *
	 */
	protected void play() {
		// 获取视频文件地址
		try {
			imageView.setVisibility(View.GONE);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置播放的视频源
			mediaPlayer.setDataSource(path);
			// 设置显示视频的SurfaceHolder
			mediaPlayer.setDisplay(sv.getHolder());
			Log.i(TAG, "开始装载");
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.i(TAG, "装载完成");
					mediaPlayer.start();
					// 按照初始位置播放
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
