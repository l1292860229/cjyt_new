package com.coolwin.XYT;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coolwin.XYT.Entity.Bimp;
import com.coolwin.XYT.Entity.ImageBucket;
import com.coolwin.XYT.Entity.ImageHelper;
import com.coolwin.XYT.Entity.ImageItem;
import com.coolwin.XYT.Entity.ImagePublicWay;
import com.coolwin.XYT.adapter.ImageGridViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 这个是进入相册显示所有图片的界面
 * @version 2014年10月18日  下午11:47:15
 */
public class ImageActivity extends Activity {
	//显示手机里的所有图片的列表控件
	private GridView gridView;
	//当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;
	//gridView的adapter
	private ImageGridViewAdapter gridImageAdapter;
	//完成按钮
	private Button preview;
	private Button okButton;
	// 返回按钮
	private Button back;
	// 取消按钮
	private Button cancel;
	private Context mContext;
	public static ArrayList<ImageItem> dataList;
	private ImageHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	// 标题
	private TextView headTitle;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		mContext = this;
		//注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);  
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
        init();
		initListener();
		//这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		  
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				ImagePublicWay.activityList.add(ImageActivity.this);
				Intent intent = new Intent();
				intent.putExtra("position", "1");
				intent.setClass(ImageActivity.this, ImageGalleryActivity.class);
				startActivity(intent);
			}
		}

	}
	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			ImageActivity.this.finish();
			if (ImagePublicWay.activityList.size()>0) {
				for (Activity activity : ImagePublicWay.activityList) {
					if (!activity.isFinishing()) {
						activity.finish();
					}
				}
				setResult(RESULT_OK);
				ImagePublicWay.activityList.clear();
			}
		}

	}

	// 返回按钮监听
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			Intent intent = new Intent();
			ImageActivity.this.finish();
			intent.setClass(ImageActivity.this, ImageFileAcitvity.class);
			startActivity(intent);
		}
	}

	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			ImageActivity.this.finish();
		}
	}

	

	// 初始化，给一些对象赋值
	private void init() {
		helper = ImageHelper.getHelper();
		helper.init(getApplicationContext());
		
		contentList = helper.getImagesBucketList(false);
		Intent intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if(folderName==null || folderName.equals("")){
			dataList = helper.getImageList();
		}
		headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
		headTitle.setText(folderName);
		Log.e("init()","folderName="+folderName);
		back = (Button) findViewById(R.id.back);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener());
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new ImageGridViewAdapter(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		preview = (Button) findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText(getResources().getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
				+ "/"+ ImagePublicWay.num+")");
	}

	private void initListener() {
		gridImageAdapter
				.setOnItemClickListener(new ImageGridViewAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,Button chooseBt) {
						if (Bimp.tempSelectBitmap.size() >= ImagePublicWay.num) {
							toggleButton.setChecked(false);
							chooseBt.setVisibility(View.GONE);
							if (!removeOneData(dataList.get(position))) {
								Toast.makeText(ImageActivity.this, R.string.only_choose_num,
										Toast.LENGTH_LONG).show();
							}
							return;
						}
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText(getResources().getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+ ImagePublicWay.num+")");
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText(getResources().getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ImagePublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
			if (Bimp.tempSelectBitmap.contains(imageItem)) {
				Bimp.tempSelectBitmap.remove(imageItem);
				okButton.setText(getResources().getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ImagePublicWay.num+")");
				return true;
			}
		return false;
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(getResources().getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ImagePublicWay.num+")");
			preview.setPressed(true);
			preview.setClickable(true);
			preview.setTextColor(Color.WHITE);
			okButton.setPressed(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
		} else {
			okButton.setText(getResources().getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ImagePublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			preview.setTextColor(Color.parseColor("#E1E0DE"));
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ImageActivity.this.finish();
		}
		return false;
	}
@Override
protected void onRestart() {
	isShowOkBt();
	super.onRestart();
}
}
