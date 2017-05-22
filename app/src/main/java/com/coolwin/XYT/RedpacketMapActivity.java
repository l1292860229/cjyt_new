package com.coolwin.XYT;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.coolwin.XYT.Entity.BranchSearchRests;
import com.coolwin.XYT.Entity.RedpacketDetails;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.map.BMapApiApp;
import com.coolwin.XYT.net.IMException;

import java.util.ArrayList;


/**
 * 关于我们页面
 * @author dongli
 *
 */
public class RedpacketMapActivity extends BaseActivity implements OnClickListener {
	public static final int GETREDPACKETSHOWMAP=0;
	public static final int OPEN_REDPACKET_INWMAP=1;
	private static final int REFU_REDPACKET_DATA=1;
	/**
	 * 导入控件
	 */
	private ImageLoader mImageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.redpacket_map);
		mContext = this;
		mImageLoader = new ImageLoader();
		initComponent();
	}
	/**
	 * 实例化控件
	 */
	MapView mMapView;
	// 定位相关
	LocationClient mLocClient;
	ImageView redpackethead,redpacketopen,redpacketbottom,redpackettop;
	TextView redpacketname,redpacketAddress,closeredpacket;
	RelativeLayout redpacketLayout;
	private BMapApiApp mApp;
	private MKSearch mSearch;
	private void initComponent(){
		setTitleContent(R.drawable.back_btn, 0, "地图红包");
		redpackethead = (ImageView) findViewById(R.id.redpackethead);
		redpacketopen= (ImageView) findViewById(R.id.redpacketopen);
		redpacketbottom= (ImageView) findViewById(R.id.redpacketbottom);
		redpackettop= (ImageView) findViewById(R.id.redpackettop1);
		redpacketname = (TextView) findViewById(R.id.redpacketname);
		redpacketAddress = (TextView) findViewById(R.id.redpacket_address);
		closeredpacket= (TextView) findViewById(R.id.closeredpacket);
		redpacketLayout = (RelativeLayout) findViewById(R.id.redpacket_layout);
		mLeftBtn.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapController.setZoom(20);
		// 定位初始化
		mLocClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setAddrType("all");
		option.setOpenGps(true);//打开gps
		option.setCoorType("bd09ll");     //设置坐标类型
		option.setScanSpan(5000);
		option.setPoiExtraInfo(true);
		option.disableCache(true);
		option.setPoiNumber(5);
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.requestLocation();
		mLocClient.registerLocationListener(new MyLocationListenner());
		mApp = (BMapApiApp)this.getApplication();
		mSearch = new MKSearch();
		closeredpacket.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				redpacketLayout.setVisibility(View.GONE);
			}
		});
		mRightBtn.setImageResource(R.drawable.add_btn);
		mRightBtn.setOnClickListener(this);
		mRightBtn.setVisibility(View.VISIBLE);
		mSearch.init(mApp.mBMapManager, new MKSearchListener() {
			@Override
			public void onGetPoiResult(MKPoiResult mkPoiResult, int i, int i1) {}
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult mkTransitRouteResult, int i) {}
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult mkDrivingRouteResult, int i) {}
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult mkWalkingRouteResult, int i) {}
			@Override
			public void onGetBusDetailResult(MKBusLineResult mkBusLineResult, int i) {}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult mkSuggestionResult, int i) {}
			@Override
			public void onGetPoiDetailSearchResult(int i, int i1) {}
			@Override
			public void onGetShareUrlResult(MKShareUrlResult mkShareUrlResult, int i, int i1) {}
			@Override
			public void onGetAddrResult(MKAddrInfo mkAddrInfo, int i) {
				if(mkAddrInfo != null){
					if(mkAddrInfo.strAddr != null && !mkAddrInfo.strAddr.equals("")){
						addrStr = mkAddrInfo.strAddr;
					}
				}
			}

		});
	}
	/**
	 * 定位SDK监听函数
	 */
	private String addrStr;
	private MapController mMapController;
	double Lat;
	double Lng;
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null){
				if (mLocClient != null) {
					mLocClient.stop();
				}
				Lat = location.getLatitude();
				Lng = location.getLongitude();
				GeoPoint point = new GeoPoint((int)(Lat* 1e6), (int)(Lng* 1e6));
				mSearch.reverseGeocode(point);
				mMapController.setCenter(point);
				MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
				LocationData locData = new LocationData();
				locData.latitude = Lat;
				locData.longitude = Lng;
				locData.direction = 2.0f;
				myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.icon_markf_h));
				myLocationOverlay.setData(locData);
				mMapView.getOverlays().add(myLocationOverlay);
				mMapView.refresh();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							BranchSearchRests branchSearchRests = IMCommon.getIMInfo().getRedpacketcount(Lat+","+Lng);
							Message message = new Message();
							message.what = GETREDPACKETSHOWMAP;
							message.obj = branchSearchRests;
							mHandler.sendMessage(message);
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}else {
				if (mLocClient != null) {
					mLocClient.stop();
				}
			}
		}
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		redpacketLayout.setVisibility(View.GONE);
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case GETREDPACKETSHOWMAP:
					BranchSearchRests branchSearchRests = (BranchSearchRests) msg.obj;
					myOverlay = new MyOverlay(getResources().getDrawable(R.drawable.redpacketmap), mMapView);
					for (BranchSearchRests.RedpacketInfo branchSearchRest : branchSearchRests.BranchSearchRests) {
						GeoPoint p1 = new GeoPoint((int) (branchSearchRest.Latitude * 1E6), (int) (branchSearchRest.Longitude * 1E6));
						OverlayItem item1 = new OverlayItem(p1, branchSearchRest.FullAddress, branchSearchRest.id);
						myOverlay.addItem(item1);
					}
					mMapView.getOverlays().clear();
					MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
					LocationData locData = new LocationData();
					locData.latitude = Lat;
					locData.longitude = Lng;
					locData.direction = 2.0f;
					myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.icon_markf_h));
					myLocationOverlay.setData(locData);
					mMapView.getOverlays().add(myLocationOverlay);
					mMapView.getOverlays().add(myOverlay);
					mMapView.refresh();
					break;
				case OPEN_REDPACKET_INWMAP:
					final RedpacketDetails redpacketDetails = (RedpacketDetails) msg.obj;
					if (redpacketDetails.ishb.equals("1")) {
						Intent intent = new Intent(mContext, RedPacketListActivity.class);
						intent.putExtra("data",  redpacketDetails.list);
						intent.putExtra("money",redpacketDetails.mymoney);
						startActivity(intent);
						return;
					}
					redpacketname.setText(redpacketDetails.hname);
					redpacketAddress.setText("在["+openredpacketaddress+"]发了一个红包");
					mImageLoader.getBitmap(mContext, redpackethead,null, redpacketDetails.hpic,0,false,false);
					redpacketLayout.setVisibility(View.VISIBLE);
					redpacketopen.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							 ScaleAnimation animation =new ScaleAnimation(1, 1.5f, 1, 1.5f,
									Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
							animation.setDuration(500);
							animation.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												Thread.sleep(200);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											Intent intent = new Intent(mContext, OpenRedPacketActivity.class);
											intent.putExtra("data",redpacketDetails);
											startActivity(intent);
											overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_left);
										}
									}).start();
								}
								@Override
								public void onAnimationEnd(Animation animation) {
								}
								@Override
								public void onAnimationRepeat(Animation animation) {}
							});
							redpacketLayout.startAnimation(animation);
							Animation translateAnimation=new TranslateAnimation(0,0,0,-100);
							translateAnimation.setDuration(500);               //设置动画持续时间
							redpackettop.startAnimation(translateAnimation);
							Animation translateAnimation2=new TranslateAnimation(0,0,0,100);
							translateAnimation2.setDuration(500);               //设置动画持续时间
							redpacketbottom.startAnimation(translateAnimation2);
						}
					});
					break;
				default:
					break;
			}
		}
	};
	public void onC(View v){
	}
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			mLocClient.stop();
			this.finish();
			break;
		case R.id.right_btn:
			Intent intent = new Intent(this,RedPacketActivity.class);
			intent.putExtra("data",addrStr);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
			switch (requestCode){
				case REFU_REDPACKET_DATA:
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BranchSearchRests branchSearchRests = IMCommon.getIMInfo().getRedpacketcount(Lat+","+Lng);
								Message message = new Message();
								message.what = GETREDPACKETSHOWMAP;
								message.obj = branchSearchRests;
								mHandler.sendMessage(message);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
					break;
			}

		}
	}

	private MyOverlay myOverlay;
	private String openredpacketaddress;
	public class MyOverlay extends ItemizedOverlay<OverlayItem> {
		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}
		@Override
		public boolean onTap(int index) {
//			final OverlayItem item = getItem(index);
//			openredpacketaddress = item.getTitle();
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						RedpacketDetails redpacketDetails = IMCommon.getIMInfo().getOpenRedpacket(item.getSnippet());
//						Message message = new Message();
//						message.what = OPEN_REDPACKET_INWMAP;
//						message.obj = redpacketDetails;
//						mHandler.sendMessage(message);
//					} catch (IMException e) {
//						e.printStackTrace();
//					}
//				}
//			}).start();
			return super.onTap(index);
		}
		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			ArrayList<OverlayItem> olist = myOverlay.getAllItem();
			double min=50;
			OverlayItem minoverlayItem=null;
			for (OverlayItem overlayItem : olist) {
				double d = DistanceUtil.getDistance(pt, overlayItem.getPoint());
				if(d<50){
					if(d<min){
						min = d;
						minoverlayItem = overlayItem;
					}
				}
			}
			if(minoverlayItem!=null){
				openredpacketaddress = minoverlayItem.getTitle();
				final OverlayItem item = minoverlayItem;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							RedpacketDetails redpacketDetails = IMCommon.getIMInfo().getOpenRedpacket(item.getSnippet());
							Message message = new Message();
							message.what = OPEN_REDPACKET_INWMAP;
							message.obj = redpacketDetails;
							mHandler.sendMessage(message);
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			return super.onTap(pt,mMapView);
		}

	}
}
