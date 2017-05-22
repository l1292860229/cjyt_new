package com.coolwin.XYT;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.coolwin.XYT.Entity.Bimp;
import com.coolwin.XYT.Entity.ImagePublicWay;
import com.coolwin.XYT.adapter.ImageFolderAdapter;


/**
 * 这个类主要是用来进行显示包含图片的文件夹
 * @version 2014年10月18日
 */
public class ImageFileAcitvity extends Activity {

	private ImageFolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_image_file);
		mContext = this;
		ImagePublicWay.activityList.add(this);
		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText(R.string.photo);
		folderAdapter = new ImageFolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			//清空选择的图片
			Bimp.tempSelectBitmap.clear();
			ImageFileAcitvity.this.finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ImageFileAcitvity.this.finish();
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
		return true;
	}

}
