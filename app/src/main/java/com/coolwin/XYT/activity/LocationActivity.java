package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.coolwin.XYT.Entity.MapInfo;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.LocationBinding;

import java.util.ArrayList;
import java.util.List;

/*
 * 定位页面
 */
public class LocationActivity extends BaseActivity {
	// 定位相关
	LocationClient mLocClient;
	MapView mMapView;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true; // 是否首次定位
	LocationBinding binding;
	private Double mCurrentLantitude, mCurrentLongitude;
	private String mCurrentAddress,mCurrentCity;
	private List<String> nearList = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding =  DataBindingUtil.setContentView(this,R.layout.location);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		// 地图初始化
		mMapView = binding.bmapView;
		init();
	}
	private void init(){
		//设置标题栏
		binding.titleLayout.title.setText("位置");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
		binding.titleLayout.rightTextBtn.setText("确定");
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		// mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(new MyLocationListenner());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		option.setIsNeedAddress(true);
		option.setAddrType("all");
		option.disableCache(true);//禁止启用缓存定位
		mLocClient.setLocOption(option);
		mLocClient.start();
		showLoading("定位中");
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public void right_text(View view) {
		Intent intent =new Intent();
		MapInfo mapInfo  = new MapInfo(mCurrentCity,mCurrentAddress,mCurrentLantitude+"",mCurrentLongitude+"");
		intent.putExtra("mapInfo",mapInfo);
		setResult(RESULT_OK,intent);
		finish();
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			hideLoading();
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			//这是已经获得定位后的结果
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude= location.getLongitude();
			mCurrentAddress = location.getAddrStr();
			mCurrentCity = location.getCity();
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				binding.loctionAddress.setText(location.getAddrStr());
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(18.0f);
				mBaiduMap.setOnMapStatusChangeListener(new MyOnMapStatusChangeListener(ll));
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
			}
		}
	}
	private class MyOnMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {
		private LatLng latLng;
		public MyOnMapStatusChangeListener(LatLng latLng){
			this.latLng = latLng;
		}
		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus) {}
		@Override
		public void onMapStatusChange(MapStatus mapStatus) {}
		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {
			//下面这个是调整位置时获得调整后的位置记录
			OverlayOptions option = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
					.fromResource(R.drawable.locationbg)).zIndex(9)  //设置marker所在层级
					.draggable(true);  //设置手势拖拽
			Marker marker = (Marker)(mBaiduMap.addOverlay(option));
			marker.setPosition(mapStatus.target);
			GeoCoder geoCoder = GeoCoder.newInstance();
			// 设置反地理经纬度坐标,请求位置时,需要一个经纬度
			geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
			//设置地址或经纬度反编译后的监听,这里有两个回调方法,
			geoCoder.setOnGetGeoCodeResultListener(new MyOnGetGeoCoderResultListener());
		}
	}
	private  class MyOnGetGeoCoderResultListener implements OnGetGeoCoderResultListener {
		//double格式化,保留6位小数
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.000000");
		//经纬度转换成地址
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null ||  result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			if (!df.format(result.getLocation().latitude).equals(df.format(mCurrentLantitude))
					|| !df.format(result.getLocation().longitude).equals(df.format(mCurrentLongitude))) {
				binding.loctionAddress.setText(result.getAddress());
			}
			mCurrentAddress = result.getAddress();
			//这里附近的地址
			nearList.clear();
			for (PoiInfo poiInfo : result.getPoiList()) {
				nearList.add(poiInfo.name);
			}
			final ReverseGeoCodeResult reverseGeoCodeResult=result;
			binding.addressList.setAdapter(new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,nearList));
			binding.addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getPoiList().get(position).location);
					mBaiduMap.animateMapStatus(u);
					binding.loctionAddress.setText(reverseGeoCodeResult.getPoiList().get(position).name);
					mCurrentLantitude = reverseGeoCodeResult.getPoiList().get(position).location.latitude;
					mCurrentLongitude = reverseGeoCodeResult.getPoiList().get(position).location.longitude;
				}
			});
		}
		//把地址转换成经纬度
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			//TODO 详细地址转换在经纬度
		}
	}

}
