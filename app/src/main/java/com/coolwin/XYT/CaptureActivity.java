package com.coolwin.XYT;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.camera.CameraManager;
import com.coolwin.XYT.decoding.CaptureActivityHandler;
import com.coolwin.XYT.decoding.InactivityTimer;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.widget.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

/**
 * 扫一扫
 * @author 
 */
public class CaptureActivity extends BaseActivity implements Callback {
	public final static int SHOWMESSAGE = 700;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
		mContext = this;
		setTitleContent(R.drawable.back_btn,0, R.string.scan_qr_code);
		mLeftBtn.setOnClickListener(this);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * ����ɨ����
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String codeUrl = result.getText();
		if (codeUrl.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else if(codeUrl.contains("http://pre.im/jmac") ) {
			if(codeUrl.contains("bid")){
				final String bid = codeUrl.substring(codeUrl.indexOf("bid=")+"bid=".length());
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							BbsList bl= IMCommon.getIMInfo().getBbs(bid);
							if(bl.mBbsList.size()!=0){
								Intent intent = new Intent(mContext, JoinBBSActivity.class);
								intent.putExtra("data", bl.mBbsList.get(0));
								startActivity(intent);
							}
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}else if(codeUrl.contains("sessionid")){
				final String sessionid = codeUrl.substring(codeUrl.indexOf("sessionid=")+"sessionid=".length());
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Room room = IMCommon.getIMInfo().getRommInfoById(sessionid);
							if(room!=null){
								boolean isjoin=false;
								for (Login login : room.mUserList) {
									if (login.uid.equals(IMCommon.getUserId(mContext))) {
										isjoin = true;
									}
								}
								if(!isjoin){
									IMCommon.getIMInfo().inviteUsers(sessionid, IMCommon.getUserPhone(mContext));
								}
								Login user = new Login();
								user.uid = room.groupId;
								user.phone = room.groupId;
								user.nickname = room.groupName;
								user.mIsRoom = 300;
								Intent intent = new Intent(mContext, ChatMainActivity.class);
								intent.putExtra("data", user);
								startActivity(intent);
							}
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}else if(codeUrl.startsWith("http://") || codeUrl.startsWith("https://")){
			Intent intent = new Intent();
			intent.putExtra("url",codeUrl);
			intent.setClass(mContext, WebViewActivity.class);
			startActivity(intent);
		}else{
			Toast.makeText(CaptureActivity.this, "未识别的二维码", Toast.LENGTH_SHORT).show();
		}
		CaptureActivity.this.finish();
	}

   /*
    *
    */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			CaptureActivity.this.finish();
			break;

		default:
			break;
		}
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_LOAD_ERROR:
				hideProgressDialog();
				String error_Detail = (String)msg.obj;
				if(error_Detail != null && !error_Detail.equals("")){
					Toast.makeText(mContext,error_Detail, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mContext, R.string.load_error, Toast.LENGTH_LONG).show();
				}
				break;
			case SHOWMESSAGE:
				Toast.makeText(mContext,"数据不存在", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};


}