package com.lvqingyang.floodsdetectassistant_android_new.Discover;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestHelper;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestListener;
import com.lvqingyang.floodsdetectassistant_android_new.overlay.BusRouteOverlay;
import com.lvqingyang.floodsdetectassistant_android_new.overlay.DrivingRouteOverlay;
import com.lvqingyang.floodsdetectassistant_android_new.overlay.WalkRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import frame.tool.MyToast;

/**
 * 项目名称：SaveFloodedPlace
 * 类描述：
 * 创建人：佳佳
 * 创建时间：2016/3/17 9:36
 * 修改人：佳佳
 * 修改时间：2016/3/17 9:36
 * 修改备注：
 */
public class MapRouteActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        RouteSearch.OnRouteSearchListener, LocationSource, AMapLocationListener, AMap.OnMarkerClickListener {

    //控件
    private RadioGroup rgRouteModel;
    private Spinner mSpinner;
    private RadioButton rbtnFoot;
    private RadioButton rbtnBus;
    private RadioButton rbtnCar;
    private Button btnSearch;
    private ProgressDialog progDialog = null;// 搜索时进度条
    //高德地图
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private int busMode = RouteSearch.BusDefault;// 公交默认模式
    private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
    private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
    private BusRouteResult busRouteResult;// 公交模式查询结果
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private RouteSearch routeSearch;
    private LatLonPoint startLatLon;
    private LatLonPoint endLatLon;
    //路线方式
    private int route_type = 0;
    //渍水点数据
    private List<UploadInfo> mUploadInfoList;
    private UploadInfo nowUploadInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_map_route);

        mapView = (MapView) findViewById(R.id.map_route);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        //初始化地图
        initMap();

        //初始化视图
        initView();
        //获取信息
        receivePointInfo();
        //跳转界面后直接查找路线
        intentSearchRoute();
    }

    //获取点的信息
    private void receivePointInfo() {
        nowUploadInfo = (UploadInfo) getIntent().getSerializableExtra("info");
        //测试信息
        startLatLon = new LatLonPoint(30.44044159, 114.26472187);

        RequestHelper.getUploadInfos(new RequestListener() {
            @Override
            public void onResponce(String responce) {
                mUploadInfoList = RequestHelper.stringToArray(responce, UploadInfo[].class);

                //设置spinner的内容，并且初始化当前选项的位置
                initMySpinner();
            }

            @Override
            public void onError(Throwable throwable) {
                MyToast.error(MapRouteActivity.this, R.string.load_error);
            }
        });

    }

    //跳转界面后直接查找路线
    private void intentSearchRoute() {
        //从本地缓存获取到当前选定的受灾点

        endLatLon = new LatLonPoint(nowUploadInfo.getLatitude(), nowUploadInfo.getLongitude());
        route_type = getIntent().getIntExtra("route_type", 0);
        initRadioGroup();
        //搜索线路
        searchRouteResult(startLatLon, endLatLon);
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        //aMap.setLocationSource(this);// 设置定位监听
        //aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        //aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
    }

    private void initView() {
        rgRouteModel = (RadioGroup) findViewById(R.id.rg_route_model);
        mSpinner = (Spinner) findViewById(R.id.sp_route_point);
        rbtnFoot = (RadioButton) findViewById(R.id.rbtn_route_foot);
        rbtnBus = (RadioButton) findViewById(R.id.rbtn_route_bus);
        rbtnCar = (RadioButton) findViewById(R.id.rbtn_route_car);
        btnSearch = (Button) findViewById(R.id.btn_route_search);

        rgRouteModel.setOnCheckedChangeListener(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();//清除地图上的覆盖物
                //搜索线路
                searchRouteResult(startLatLon, endLatLon);
            }
        });
    }

    private void initRadioGroup() {
        switch (route_type) {
            case 0://步行模式
                rbtnFoot.setChecked(true);
                break;
            case 1://公交模式
                rbtnBus.setChecked(true);
                break;
            case 2://驾车模式
                rbtnCar.setChecked(true);
                break;
        }
    }

    private void initMySpinner() {
        ArrayList<String> nameList = new ArrayList<>();
        int index = 0;
        for (int i=0; i < mUploadInfoList.size(); i++) {
            nameList.add(mUploadInfoList.get(i).getUpload_name());
            if (nowUploadInfo.getId() == mUploadInfoList.get(i).getId())
                index = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, nameList);
        mSpinner.setAdapter(adapter);

        //此处需要设置mSpinner位置
        mSpinner.setSelection(index);

        //设置spinner点击事件
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取要去的受灾点的经纬度
                endLatLon = new LatLonPoint(
                        mUploadInfoList.get(position).getLatitude(),
                        mUploadInfoList.get(position).getLongitude());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbtn_route_foot:
                route_type = 0;
                break;
            case R.id.rbtn_route_bus:
                route_type = 1;
                break;
            case R.id.rbtn_route_car:
                route_type = 2;
                break;
        }
    }


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    /**
     * 开始搜索路径规划方案
     */
    private void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        if (route_type == 0) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (route_type == 1) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, busMode, "武汉", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (route_type == 2) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, drivingMode,
                    null, null, "");
// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    /**
     * 公交路线查询回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                busRouteResult = result;
                BusPath busPath = busRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
                        busPath, busRouteResult.getStartPos(),
                        busRouteResult.getTargetPos());
                routeOverlay.removeFromMap();
                routeOverlay.addToMap();
                routeOverlay.zoomToSpan();
                addMarkersToMap();
            } else {
                MyToast.error(this, R.string.search_route_wrong);
            }
        }
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                driveRouteResult = result;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        this, aMap, drivePath, driveRouteResult.getStartPos(),
                        driveRouteResult.getTargetPos(), null);
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
                addMarkersToMap();
            } else {
                MyToast.error(this, R.string.search_route_wrong);
            }
        }
    }

    /**
     * 步行路线结果回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
                        aMap, walkPath, walkRouteResult.getStartPos(),
                        walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
                addMarkersToMap();
            } else {
                MyToast.error(this, R.string.search_route_wrong);
            }
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                MyToast.error(this, errText);
            }
        }
    }

    // 往地图上添加marker
    private void addMarkersToMap() {
        for (int i = 0; i < mUploadInfoList.size(); i++) {
            LatLng mLatLng = new LatLng(
                    mUploadInfoList.get(i).getLatitude(),
                    mUploadInfoList.get(i).getLongitude());

            aMap.addMarker(new MarkerOptions()
                    .title(mUploadInfoList.get(i).getUpload_name())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(mLatLng)
            );
        }

    }

    //之前做了一个规避渍水点的路径规划功能
    //将所有渍水点的位置放入一个List
    private List<List<LatLonPoint>> wetPointList() {
        List<List<LatLonPoint>> mList = new ArrayList<List<LatLonPoint>>();
        List<LatLonPoint> mListPoint = new ArrayList<LatLonPoint>();
        double mLatitude;
        double mLongitude;
        for (int i = 0; i < mUploadInfoList.size(); i++) {
            mLatitude = mUploadInfoList.get(i).getLatitude();
            mLongitude = mUploadInfoList.get(i).getLongitude();
            mListPoint.add(new LatLonPoint(mLatitude + 0.001, mLongitude + 0.001));
            mListPoint.add(new LatLonPoint(mLatitude + 0.001, mLongitude - 0.001));
            mListPoint.add(new LatLonPoint(mLatitude - 0.001, mLongitude - 0.001));
            mListPoint.add(new LatLonPoint(mLatitude - 0.001, mLongitude + 0.001));
            mList.add(mListPoint);
        }
        return mList;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}