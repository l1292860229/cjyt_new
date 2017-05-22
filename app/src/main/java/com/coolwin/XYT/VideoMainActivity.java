package com.coolwin.XYT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.coolwin.XYT.global.Utils;
import com.coolwin.XYT.widget.MovieRecorderView;

import java.io.IOException;

public class VideoMainActivity extends BaseActivity {
    private MovieRecorderView mRecorderView;
    private Button mShootBtn,cancel,sure;
    private boolean isFinish = true;
    private String path;
    private LinearLayout luzhi;
    private RelativeLayout showVideo;
    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_main);
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);
        luzhi = (LinearLayout) findViewById(R.id.luzhi);
        showVideo = (RelativeLayout) findViewById(R.id.showVideo);
        cancel = (Button) findViewById(R.id.cancel);
        sure = (Button) findViewById(R.id.sure);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mShootBtn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mRecorderView.getTimeCount() > 1)
                        handler.sendEmptyMessage(1);
                    else {
                        if (mRecorderView.getmRecordFile() != null)
                            mRecorderView.getmRecordFile().delete();
                        mRecorderView.stop();
                        Toast.makeText(VideoMainActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorderView.setVisibility(View.VISIBLE);
                // 显示播放页面
                luzhi.setVisibility(View.VISIBLE);
                showVideo.setVisibility(View.GONE);
                try {
                    mRecorderView.initCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("path",path);
                try{
                    Double time = Double.valueOf(Utils.getRingDuring(path));
                    if((time/1000)>9){
                        intent.putExtra("time", "00:"+(int)(time/1000));
                    }else if((time/1000)<1){
                        intent.putExtra("time", "00:01");
                    }else{
                        intent.putExtra("time", "00:0"+(int)(time/1000));
                    }
                }catch(NumberFormatException e){}
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };

    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            // 返回到播放页面
//            Intent intent = new Intent();
//            Log.d("TAG",mRecorderView.getmRecordFile().getAbsolutePath());
//            intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath());
//            setResult(RESULT_OK,intent);
            path = mRecorderView.getmRecordFile().getAbsolutePath();
            mRecorderView.setVisibility(View.GONE);
            // 显示播放页面
            luzhi.setVisibility(View.GONE);
            showVideo.setVisibility(View.VISIBLE);
            // 播放相应的视频
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.setVideoURI(Uri.parse(path));
            mVideoView.start();
        }
        // isFinish = false;
    }

}
