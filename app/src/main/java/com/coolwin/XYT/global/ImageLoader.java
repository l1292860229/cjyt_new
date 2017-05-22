package com.coolwin.XYT.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.Login;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;


public class ImageLoader {
	public final static String TAG = "ImageLoader";
	HashMap<String, Bitmap> mImageBuffer = null;
	HashMap<String, Bitmap> mHeaderBuffer = null;
	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	final int cacheSize = maxMemory / 8;
	private LruCache<String, Bitmap> mMemoryCache;
	public final static String SDCARD_PICTURE_CACHE_PATH = "/IM/pic_cache/";
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
	private List<Login> list;
	private Bitmap defaultbmp;
	public ImageLoader(final List<Login> loginlist){
		mImageBuffer  = new HashMap<String, Bitmap>();
		mHeaderBuffer = new HashMap<String, Bitmap>();
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
		this.list= loginlist;
		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				for(Login l:list){
					String imageURL = l.headsmall;
					String fileName =  MD5.getInstantiation().getMD5ofStr(imageURL);
					File file=null;
					if (FeatureFunction.checkSDCard()&& FeatureFunction.newFolder(Environment.getExternalStorageDirectory()
							+ SDCARD_PICTURE_CACHE_PATH)) {
						file = new File(Environment.getExternalStorageDirectory() + SDCARD_PICTURE_CACHE_PATH, fileName);
					}
					if (file != null && file.exists()) {
						Bitmap bitmap = FeatureFunction.tryToDecodeImageFile(file.getPath(),1, true);
						if(bitmap !=null){
							addBitmapToMemoryCache(imageURL,bitmap);
						}
					}
				}
			}
		});
	}
	public ImageLoader(final List<BbsReplyInfo> bbsReplyInfolist, int i){
		mImageBuffer  = new HashMap<String, Bitmap>();
		mHeaderBuffer = new HashMap<String, Bitmap>();
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				for(BbsReplyInfo l:bbsReplyInfolist){
					String imageURL = l.headsmall;
					String fileName =  MD5.getInstantiation().getMD5ofStr(imageURL);
					File file=null;
					if (FeatureFunction.checkSDCard()&& FeatureFunction.newFolder(Environment.getExternalStorageDirectory()
							+ SDCARD_PICTURE_CACHE_PATH)) {
						file = new File(Environment.getExternalStorageDirectory() + SDCARD_PICTURE_CACHE_PATH, fileName);
					}
					if (file != null && file.exists()) {
						Bitmap bitmap = FeatureFunction.tryToDecodeImageFile(file.getPath(),1, true);
						if(bitmap !=null){
							addBitmapToMemoryCache(imageURL,bitmap);
						}
					}
				}
			}
		});
	}
	public ImageLoader(){
		mImageBuffer  = new HashMap<String, Bitmap>();
		mHeaderBuffer = new HashMap<String, Bitmap>();
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
	}
	public void clearBitmapToMemoryCache(){
		mMemoryCache.evictAll();
	}
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageBuffer;
	}
	public Bitmap getBitmap(final Object view, ProgressBar progressBar, final String imageURL, int resID, Bitmap defaultbmp) {
		this.defaultbmp = defaultbmp;
		if (imageURL == null || !imageURL.startsWith("http://api.map.baidu.com")) {
			if (imageURL != null) {
//				Log.e(TAG, "imageURL = " + imageURL);
				//如果是外部地址图片！调用下载
			}
		}
		Bitmap bitmap =null;
		if(getBitmapFromMemCache(imageURL)!=null){
			bitmap = getBitmapFromMemCache(imageURL);
		}
		if (bitmap != null) {
			if (view instanceof ImageView) {
				ImageView imageView = (ImageView) view;
				imageView.setImageBitmap(bitmap);
				if(progressBar != null){
					progressBar.setVisibility(View.GONE);
				}
			}
			else if (view instanceof RemoteViews) {
				RemoteViews imageView = (RemoteViews) view;
				imageView.setImageViewBitmap(resID, bitmap);
			}else if(view instanceof RelativeLayout){
				RelativeLayout layout=(RelativeLayout)view;
				BitmapDrawable bd=new BitmapDrawable(bitmap);
				layout.setBackgroundDrawable(bd);
			}else {
				Log.d(TAG, "Unkown view get bitmap!");
			}
			return bitmap;
		} else {
			Log.d(TAG, "Image buffer exist empty bitmap object!");
		}
		String fileName =  MD5.getInstantiation().getMD5ofStr(imageURL);
		try {
			final Object str[] = new Object[8];
			str[0] = imageURL;
			str[1] = view;
			str[2] = progressBar;
			str[3] = fileName;
			CanvasImageTask2 task = new CanvasImageTask2();
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				task.execute(str);
			} else {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
			}
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}
	public Bitmap getBitmap(Context context, final Object view,
                            ProgressBar progressBar, final String imageURL, int resID, final boolean isCache , boolean isHead) {

		if (imageURL == null || !imageURL.startsWith("http://api.map.baidu.com")) {
			if (imageURL != null) {
	            Log.e(TAG, "imageURL = " + imageURL);
				//如果是外部地址图片！调用下载
            }
		}
		if (mImageBuffer.containsKey(imageURL)) {
			Bitmap bitmap = mImageBuffer.get(imageURL);
			if (bitmap != null) {
				if (view instanceof ImageView) {
					ImageView imageView = (ImageView) view;
					imageView.setImageBitmap(bitmap);
					if(progressBar != null){
						progressBar.setVisibility(View.GONE);
					}
				} 
				else if (view instanceof RemoteViews) {
					// Reference been Widget user profile
					RemoteViews imageView = (RemoteViews) view;
					imageView.setImageViewBitmap(resID, bitmap);
				}else if(view instanceof RelativeLayout){
					RelativeLayout layout=(RelativeLayout)view;
					BitmapDrawable bd=new BitmapDrawable(bitmap);
					layout.setBackgroundDrawable(bd);
				}else {
					Log.d(TAG, "Unkown view get bitmap!");
				}
				return bitmap;
			} else {
				Log.d(TAG, "Image buffer exist empty bitmap object!");
			}
		}

		try {
			final Object str[] = new Object[8];
			str[0] = imageURL;
			str[1] = view;
			str[2] = context;
			str[3] = resID;
			str[4] = isHead;
			str[5] = true;
			str[6] = progressBar;
			//new CanvasImageTask(isCache).execute(str);
			CanvasImageTask task = new CanvasImageTask(isCache);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				task.execute(str);
			} else {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
			}
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

    //获得圆角图片的方法  
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx){
		Bitmap output;
		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
					.getHeight(), Config.ARGB_8888);
		}catch (OutOfMemoryError error){
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
					.getHeight(), Config.ALPHA_8);
		}
        Canvas canvas = new Canvas(output);
   
        final int color = 0xff424242;  
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
   
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
   
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);  
        if(bitmap != null && !bitmap.isRecycled() && bitmap != output){
        	bitmap.recycle();
        }
        bitmap = output;
        return bitmap;  
    }
	/**
	 * Down load bitmap from net and save it to cache
	 * @param urlString
	 * 		The address of bitmap
	 * @param file
	 * 		Bitmap will been saved path
	 * @return
	 * 		null indicator down load bitmap is fail
	 */
	private Bitmap loadImageFromUrl(String urlString, File file) {
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream is = null;

		if(urlString == null || urlString.equals("")){
			return bitmap;
		}
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					bos.write(temp, 0, readLen);
					destPos += readLen;
				}

				byte[] imgData = new byte[destPos];
				System.arraycopy(bos.toByteArray(), 0, imgData, 0, destPos);
				try {
					bitmap = BitmapFactory.decodeByteArray(imgData, 0,
							imgData.length);
				}catch (OutOfMemoryError error){
					BitmapFactory.Options newOpts = new BitmapFactory.Options();
					newOpts.inJustDecodeBounds = false;
					newOpts.inSampleSize = 10;//
					bitmap = BitmapFactory.decodeByteArray(imgData, 0,
							imgData.length, newOpts);
				}
				if (file != null){
					writeBitmapToCache(imgData, file);
				}
			}


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
			if(bitmap == null){
				bitmap = defaultbmp;
			}
		}

		return bitmap;
	}

	/**
	 * Write the special data to file
	 * 
	 * @param imgData
	 * @param file
	 *            Save data, maybe is SDCard or cache
	 */
	private void writeBitmapToCache(byte[] imgData, File file) {
		FileOutputStream fos = null;
		BufferedOutputStream outPutBuffer = null;

		if (file != null) {
			try {
				fos = new FileOutputStream(file);

				outPutBuffer = new BufferedOutputStream(fos);
				outPutBuffer.write(imgData);
				outPutBuffer.flush();
				fos.flush();

			} catch (IOException e) {
				Log.d(TAG, e.toString());
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
					imgData = null;
					System.gc();

					if (outPutBuffer != null) {
						outPutBuffer.close();
					}
				} catch (IOException e) {
					Log.d(TAG, e.toString());
				}
			}
		}
	}


	class CanvasImageTask extends AsyncTask<Object, Void, Bitmap> {
		/** After load picture, will set it to this view */
		private Object gView;

		/** According to this URL to down load picture */
		private String url;
		private Context mContext;

		/** Down load and generate bitmap object */
		Bitmap bmp = null;

		/** Whether need write picture to cache */
		private boolean mIsCache = true;



		private int mResId;
		private boolean mIsHead;
		private boolean mIsNeedWriteFile;
		private ProgressBar mProgressBar;
		public CanvasImageTask(boolean isCache) {
			mIsCache = isCache;
		}

		/**
		 * If special URL file had not been cached, it will down load from net,
		 * or load it from cache
		 *
		 * @param str
		 *            Include view object, URL and context
		 */
		@Override
		protected Bitmap doInBackground(Object... str) {
			// Decode parameters
			url = str[0].toString();
			gView = str[1];
			mContext = (Context) str[2];
			mResId = (Integer)str[3];
			mIsHead=(Boolean)str[4];
			mIsNeedWriteFile = (Boolean)str[5];
			mProgressBar = (ProgressBar) str[6];
			String fileName = MD5.getInstantiation().getMD5ofStr(url);
			File file = null;
			if (mIsNeedWriteFile) {
				if (!mImageBuffer.containsKey(url)){
						if (mIsCache) {
							if (str.length > 2) {
								file = new File(mContext.getCacheDir(), fileName);
							}
						} else {
							if (FeatureFunction.checkSDCard() && str.length > 2) {
								if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory()
										+ SDCARD_PICTURE_CACHE_PATH)) {
									file = new File(Environment.getExternalStorageDirectory() + SDCARD_PICTURE_CACHE_PATH, fileName);
								}
							}
						}
						if (file != null && file.exists()) {
							bmp = FeatureFunction.tryToDecodeImageFile(file.getPath(),1, true);
						}else {
							bmp = loadImageFromUrl(url, file);
						}
					}
					else {
						bmp = loadImageFromUrl(url, file);
					}
			}
			else {
				if (!mHeaderBuffer.containsKey(url)){
					bmp = loadImageFromUrl(url, file);
				}
			}
			return bmp;
		}

		/**
		 * After load picture is success, system will call this function to
		 * update UI
		 *
		 */
		@Override
		protected void onPostExecute(Bitmap bm) {

			if (bm != null) {
				Bitmap bitmap = bm;
				if (gView instanceof ImageView) {
					ImageView imageView = (ImageView) gView;
					if (mIsHead) {
						bitmap = getRoundedCornerBitmap(bm, 5);
					}else {
						bitmap = bm;
					}
					if(mProgressBar != null){
						mProgressBar.setVisibility(View.GONE);
					}
					imageView.setImageBitmap(bitmap);
				}else if(gView instanceof RelativeLayout){
					bitmap=bm.copy(Config.ARGB_8888, true);
					RelativeLayout relayout=(RelativeLayout)gView;
					BitmapDrawable bd=new BitmapDrawable(bm);
					relayout.setBackgroundDrawable(bd);
				}

				if (mIsNeedWriteFile) {
					mImageBuffer.put(url, bitmap);
				}
				else {
					mHeaderBuffer.put(url, bitmap);
				}
			}else{
				Log.e("ImageLoader_", "bitmap is null "+url);
			}
		}
	}
	class CanvasImageTask2 extends AsyncTask<Object, Void, Bitmap> {
		/** After load picture, will set it to this view */
		private Object gView;
		/** According to this URL to down load picture */
		private String url;
		/** Down load and generate bitmap object */
		Bitmap bmp = null;
		String fileName;
		/** Whether need write picture to cache */
		private ProgressBar mProgressBar;
		/**
		 * If special URL file had not been cached, it will down load from net,
		 * or load it from cache
		 *
		 * @param str
		 *            Include view object, URL and context
		 */
		@Override
		protected Bitmap doInBackground(Object... str) {
			// Decode parameters
			url = str[0].toString();
			gView = str[1];
			mProgressBar = (ProgressBar) str[2];
			fileName = (String)str[3];
			File file = new File(Environment.getExternalStorageDirectory() + SDCARD_PICTURE_CACHE_PATH, fileName);
			bmp = loadImageFromUrl(url, file);
			addBitmapToMemoryCache(url,bmp);
			return bmp;
		}

		/**
		 * After load picture is success, system will call this function to
		 * update UI
		 *
		 */
		@Override
		protected void onPostExecute(Bitmap bm) {
			if (bm != null) {
				Bitmap bitmap = bm;
				if (gView instanceof ImageView) {
					ImageView imageView = (ImageView) gView;
					if(mProgressBar != null){
						mProgressBar.setVisibility(View.GONE);
					}
					imageView.setImageBitmap(bitmap);
				}
			}else{
				Log.e("ImageLoader_", "bitmap is null "+url);
			}
		}
	}
}
