package com.coolwin.XYT;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.ImageLoader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 行业圈二维码
 * @author dongli
 *
 */
public class GrouperweimaActivity extends BaseActivity implements OnClickListener {
	
	private ImageView headsmallView,eWeiMaView;
	private TextView titleView,contentView;
	private ImageLoader mImageLoader;
	private LinearLayout groupHeader;
//	private Bbs bbs;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_eweima_page);
		mContext = this;
		mImageLoader =new ImageLoader();
		initCompent();
	}
	
	/*
	 * 实例化控件
	 */
	private void initCompent() {
		//bbs = (Bbs)getIntent().getSerializableExtra("data");
		setTitleContent(R.drawable.back_btn, 0, 0);
		String titleTV  = getIntent().getStringExtra("title");
		String headsmallTV  = getIntent().getStringExtra("headsmall");
		String titlevalue  = getIntent().getStringExtra("titlevalue");
		String content  = getIntent().getStringExtra("content");
		String url  = getIntent().getStringExtra("url");
		Login[] logins  = (Login[]) getIntent().getSerializableExtra("usersheadsmall");
		titileTextView.setText(titleTV);
		mLeftBtn.setOnClickListener(this);
		headsmallView = (ImageView) findViewById(R.id.headsmall);
		eWeiMaView = (ImageView) findViewById(R.id.bbs_eweima);
		titleView = (TextView) findViewById(R.id.bbs_title);
		contentView = (TextView) findViewById(R.id.bbs_content);
		groupHeader = (LinearLayout) findViewById(R.id.group_header);
		if(headsmallTV!=null &&!headsmallTV.equals("")){
			mImageLoader.getBitmap(mContext,headsmallView,null,headsmallTV,0,false,false);
		}else if(logins!=null && logins.length>0){
			if(groupHeader.getChildCount() != 0){
				groupHeader.removeAllViews();
			}
			int count = logins.length;
			if(count == 1){
				groupHeader.setVisibility(View.GONE);
				headsmallView.setVisibility(View.VISIBLE);
				mImageLoader.getBitmap(mContext,headsmallView, null, logins[0].headsmall, 0, false, true);
			}else {
				groupHeader.setVisibility(View.VISIBLE);
				headsmallView.setVisibility(View.GONE);

				boolean single = count % 2 == 0 ? false : true;
				int row = !single ? count / 2 : count / 2 + 1;
				for (int i = 0; i < row; i++) {
					LinearLayout outLayout = new LinearLayout(mContext);
					outLayout.setOrientation(LinearLayout.HORIZONTAL);
					int width = FeatureFunction.dip2px(mContext, 40);
					outLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, width));
					int padding = FeatureFunction.dip2px(mContext, 1);
					if(single && i == 0){
						LinearLayout layout = new LinearLayout(mContext);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
						layout.setPadding(padding, padding, padding, padding);
						layout.setLayoutParams(params);
						ImageView imageView = new ImageView(mContext);
						imageView.setImageResource(R.drawable.contact_default_header);
						mImageLoader.getBitmap(mContext, imageView, null, logins[0].headsmall, 0, false, true);
						imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
						layout.addView(imageView);
						outLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						outLayout.addView(layout);
					}else {
						for (int j = 0; j < 2; j++) {
							LinearLayout layout = new LinearLayout(mContext);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
							layout.setPadding(padding, padding, padding, padding);
							layout.setLayoutParams(params);
							ImageView imageView = new ImageView(mContext);
							imageView.setImageResource(R.drawable.contact_default_header);
							if(single){
								mImageLoader.getBitmap(mContext, imageView, null,logins[2 * i + j - 1].headsmall, 0, false, true);
							}else {
								mImageLoader.getBitmap(mContext, imageView, null,logins[2 * i + j].headsmall, 0, false, true);
							}
							imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
							layout.addView(imageView);
							outLayout.addView(layout);
						}
					}
					groupHeader.addView(outLayout);
				}
			}
		}
		titleView.setText(titlevalue);
		contentView.setText(content);
		try {
			if(headsmallTV!=null &&!headsmallTV.equals("")){
				eWeiMaView.setImageBitmap(addLogo(Create2DCode(url), getBitmap(headsmallTV)));
			}else{
				eWeiMaView.setImageBitmap(Create2DCode(url));
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将url转为getBitmap;
	 * @param url
	 * @return
     */
	public Bitmap getBitmap(String url) {
		Bitmap bm = null;
		try {
			URL iconUrl = new URL(url);
			URLConnection conn = iconUrl.openConnection();
			HttpURLConnection http = (HttpURLConnection) conn;

			int length = http.getContentLength();

			conn.connect();
			// 获得图像的字符流
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is, length);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();// 关闭流
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
	/**
	 * 用字符串生成二维码
	 * @param str
	 * @return
	 * @throws WriterException
	 */
	public Bitmap Create2DCode(String str) throws WriterException {
		//生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 600, 600);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		//二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(matrix.get(x, y)){
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}

			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	/**
	 * 在二维码中间添加Logo图案
	 */
	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}
		if (logo == null) {
			return src;
		}
		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();
		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}
		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}
		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth, srcHeight);
			canvas.drawBitmap(logo, (srcWidth - logoWidth), (srcHeight - logoHeight), null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}
	
	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			GrouperweimaActivity.this.finish();
			break;
		default:
			break;
		}
	}

}
