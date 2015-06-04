package com.honghe.navdemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.honghe.application.MainApplication;
import com.honghe.application.TTSController;
import com.honghe.navdemo.R;
import com.honghe.navdemo.util.ToastUtil;
import com.honghe.navdemo.util.Utils;

public class MainActivity extends FragmentActivity implements OnClickListener,
		AMapNaviListener {
	// 起点、终点坐标显示
	private TextView mStartPointTextView;
	private TextView mEndPointTextView;
	// 驾车线路：路线规划、模拟导航、实时导航按钮
	private Button mDriveRouteButton;
	private Button mDriveEmulatorButton;
	private Button mDriveNaviButton;
	// 步行线路：路线规划、模拟导航、实时导航按钮
	private Button mFootRouteButton;
	private Button mFootEmulatorButton;
	private Button mFootNaviButton;

	private Button mHUDNavButton;
	// 地图和导航资源
	private MapView mMapView;
	private AMap mAMap;
	private AMapNavi mAMapNavi;

	// 起点终点坐标
	private NaviLatLng mNaviStart = new NaviLatLng(39.989614, 116.481763);
	private NaviLatLng mNaviEnd = new NaviLatLng(39.983456, 116.3154950);
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	// 规划线路
	private RouteOverLay mRouteOverLay;
	// 是否驾车和是否计算成功的标志
	private boolean mIsDriveMode = true;
	private boolean mIsCalculateRouteSuccess = false;
	private List<NaviLatLng> navilatlngArray;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AMapNaviPath naviPath = (AMapNaviPath) msg.obj;
			navilatlngArray= naviPath.getCoordList();
			/*List<NaviLatLng> navilatArray = naviPath.getCoordList();
			if (null != navilatArray) {
				int size = naviPath.getCoordList().size();
				Log.e("test", size + "");
				for (NaviLatLng naviLatLng : navilatArray) {
					Log.e("test", "lat:" + naviLatLng.getLatitude() + " lng:"
							+ naviLatLng.getLongitude());
				}
			}*/

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView(savedInstanceState);
		MainApplication.getInstance().addActivity(this);
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
	}

	// 初始化View
	private void initView(Bundle savedInstanceState) {
		mAMapNavi = AMapNavi.getInstance(this);
		mAMapNavi.setAMapNaviListener(this);
		mStartPoints.add(mNaviStart);
		mEndPoints.add(mNaviEnd);
		mStartPointTextView = (TextView) findViewById(R.id.start_position_textview);
		mEndPointTextView = (TextView) findViewById(R.id.end_position_textview);

		mStartPointTextView.setText(mNaviStart.getLatitude() + ","
				+ mNaviStart.getLongitude());
		mEndPointTextView.setText(mNaviEnd.getLatitude() + ","
				+ mNaviEnd.getLongitude());

		mDriveNaviButton = (Button) findViewById(R.id.car_navi_navi);
		mDriveEmulatorButton = (Button) findViewById(R.id.car_navi_emulator);
		mDriveRouteButton = (Button) findViewById(R.id.car_navi_route);

		mFootRouteButton = (Button) findViewById(R.id.foot_navi_route);
		mFootEmulatorButton = (Button) findViewById(R.id.foot_navi_emulator);
		mFootNaviButton = (Button) findViewById(R.id.foot_navi_navi);
		mHUDNavButton = (Button) findViewById(R.id.car_navi_hud_navi);

		mDriveNaviButton.setOnClickListener(this);
		mDriveEmulatorButton.setOnClickListener(this);
		mDriveRouteButton.setOnClickListener(this);

		mFootRouteButton.setOnClickListener(this);
		mFootEmulatorButton.setOnClickListener(this);
		mFootNaviButton.setOnClickListener(this);
		mHUDNavButton.setOnClickListener(this);

		mMapView = (MapView) findViewById(R.id.simple_route_map);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();
		mRouteOverLay = new RouteOverLay(mAMap, null);
	}

	// 计算驾车路线
	private void calculateDriveRoute() {
		boolean isSuccess = mAMapNavi.calculateDriveRoute(mStartPoints,
				mEndPoints, null, AMapNavi.DrivingDefault);
		if (!isSuccess) {
			ToastUtil.showShort(this, "路线计算失败,检查参数情况");
		}

	}

	// 计算步行路线
	private void calculateFootRoute() {
		boolean isSuccess = mAMapNavi.calculateWalkRoute(mNaviStart, mNaviEnd);
		if (!isSuccess) {
			ToastUtil.showShort(this, "路线计算失败,检查参数情况");
		}
	}

	private void startEmulatorNavi(boolean isDrive) {
		if ((isDrive && mIsDriveMode && mIsCalculateRouteSuccess)
				|| (!isDrive && !mIsDriveMode && mIsCalculateRouteSuccess)) {
			/*
			 * Intent emulatorIntent = new Intent(MainActivity.this,
			 * SimpleNaviActivity.class); Bundle bundle = new Bundle();
			 * bundle.putBoolean(Utils.ISEMULATOR, true);
			 * bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEROUTENAVI);
			 * emulatorIntent.putExtras(bundle);
			 * emulatorIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			 * startActivity(emulatorIntent);
			 */

		} else {
			ToastUtil.showShort(this, "请先进行相对应的路径规划，再进行导航");
		}
	}

	private void startGPSNavi(boolean isDrive) {
		if ((isDrive && mIsDriveMode && mIsCalculateRouteSuccess)
				|| (!isDrive && !mIsDriveMode && mIsCalculateRouteSuccess)) {
			/*
			 * Intent gpsIntent = new Intent(MainActivity.this,
			 * SimpleNaviActivity.class); Bundle bundle = new Bundle();
			 * bundle.putBoolean(Utils.ISEMULATOR, false);
			 * bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEROUTENAVI);
			 * gpsIntent.putExtras(bundle);
			 * gpsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			 * startActivity(gpsIntent);
			 */
		} else {
			ToastUtil.showShort(this, "请先进行相对应的路径规划，再进行导航");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			MainApplication.getInstance().deleteActivity(this);

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		ToastUtil.showShort(this, "路径规划出错" + arg0);
		mIsCalculateRouteSuccess = false;

	}

	@Override
	public void onCalculateRouteSuccess() {
		AMapNaviPath naviPath = mAMapNavi.getNaviPath();
		if (naviPath == null) {
			return;
		}
		// 获取路径规划线路，显示到地图上
		mRouteOverLay.setRouteInfo(naviPath);
		mRouteOverLay.addToMap();
		mIsCalculateRouteSuccess = true;
		if (null != naviPath) {
			Message msg = new Message();
			msg.obj = naviPath;
			handler.sendMessage(msg);
		}

	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car_navi_route:
			mIsCalculateRouteSuccess = false;
			mIsDriveMode = true;
			calculateDriveRoute();
			break;
		case R.id.car_navi_emulator:
			startEmulatorNavi(true);
			break;
		case R.id.car_navi_navi:
			startGPSNavi(true);
			break;
		case R.id.foot_navi_route:
			mIsCalculateRouteSuccess = false;
			mIsDriveMode = false;
			calculateFootRoute();
			break;
		case R.id.foot_navi_emulator:
			startEmulatorNavi(false);
			break;
		case R.id.foot_navi_navi:
			startGPSNavi(false);
			break;
		case R.id.car_navi_hud_navi:
			startHUDNavi();
			break;

		}
	}

	/**
	 * HUD 导航
	 * 
	 * @param isDrive
	 */
	private void startHUDNavi() {
		if ((mIsDriveMode && mIsCalculateRouteSuccess)
				|| (!mIsDriveMode && mIsCalculateRouteSuccess)) {
			Intent intent = new Intent(MainActivity.this,
					SimpleHudActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			Bundle bundle = new Bundle();
			bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEHUDNAVIE);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			ToastUtil.showShort(this, "请先进行相对应的路径规划，再进行导航");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		// 删除监听
		AMapNavi.getInstance(this).removeAMapNaviListener(this);

	}

}
