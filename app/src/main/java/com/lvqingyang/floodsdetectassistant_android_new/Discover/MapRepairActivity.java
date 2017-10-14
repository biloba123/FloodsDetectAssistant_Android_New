package com.lvqingyang.floodsdetectassistant_android_new.Discover;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.Work.CheckDetailsActivity;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestHelper;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import frame.tool.MyToast;


public class MapRepairActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, OnCheckedChangeListener, View.OnClickListener,
        AMap.OnMarkerClickListener {
    //高德地图
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    //控件
    private Spinner mSpinner;
    private RadioGroup mRadioGroup;
    private Button btnFloodedPlace;
    private TextView tvName;
    private TextView tvAddress;
    private Button btnDetails;
    private Button btnDrive;
    private Button btnBus;
    private Button btnFoot;
    //公众上报信息
    private List<UploadInfo> mUploadInfoList=new ArrayList<>();
    //记录当前所选的渍水点
    private int nowPoint = 0;
    private CardView mCvInfoTips;
    private CardView mCvAcc;


    public static void start(Context context) {
        Intent starter = new Intent(context, MapRepairActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_map);

        mapView = (MapView) findViewById(R.id.map_main);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        //初始化地图
        initMap();

        //从服务器获取点的信息并用sharedpreference存储到本地
        receivePointInfo();


    }


    private void initView() {
        addMarkersToMap();// 往地图上添加marker

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_repair_map_model);
        mCvAcc = (CardView) findViewById(R.id.cv_accuracy);
        mRadioGroup.setOnCheckedChangeListener(this);

//        mCvInfoTips = (CardView) findViewById(R.id.cv_info_tips);
//        mCvInfoTips.setVisibility(View.GONE);//底部tip设置不可见
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//设置置底
//        lp.setMargins(10, 0, 0, 10);//设置margin
//        mCvAcc.setLayoutParams(lp);//动态改变布局

        mSpinner = (Spinner) findViewById(R.id.sp_repair_map_point_choose);
        btnFloodedPlace = (Button) findViewById(R.id.btn_repair_map_flooded_places);
        tvName = (TextView) findViewById(R.id.tv_repair_map_name);
        tvAddress = (TextView) findViewById(R.id.tv_repair_map_address);
        btnDetails = (Button) findViewById(R.id.btn_repair_map_details);
        btnDrive = (Button) findViewById(R.id.btn_repair_map_drive);
        btnBus = (Button) findViewById(R.id.btn_repair_map_bus);
        btnFoot = (Button) findViewById(R.id.btn_repair_map_foot);

        btnFloodedPlace.setOnClickListener(this);
        btnDetails.setOnClickListener(this);
        btnDrive.setOnClickListener(this);
        btnBus.setOnClickListener(this);
        btnFoot.setOnClickListener(this);
        setMySpinner();

    }

    private void initMap() {
        //获取地图并且设置一些属性
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

            aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        }
    }

    private void setMySpinner() {
        ArrayList<String> nameList = new ArrayList<>();
        for (UploadInfo i : mUploadInfoList) {
            nameList.add(i.getUpload_name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, nameList);
        mSpinner.setAdapter(adapter);

        //设置spinner点击事件
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nowPoint = position;
                //使用arrayMarkers的定位功能
                ArrayList<MarkerOptions> arrayMarkers = new ArrayList<MarkerOptions>();
                ArrayList<Marker> markers = new ArrayList<Marker>();

                LatLng mLatLng = new LatLng(
                        mUploadInfoList.get(position).getLatitude(),
                        mUploadInfoList.get(position).getLongitude());
                arrayMarkers.add(new MarkerOptions()
                        .title(mUploadInfoList.get(position).getUpload_name())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(mLatLng));
                markers = aMap.addMarkers(arrayMarkers, true);
                markers.get(0).showInfoWindow();

                //设置底部栏可见并且更新信息
//                setInfoTipVisibile();
                refreshTipInfo(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

    //设置RadioGroup的点击变换效果
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbtn_repair_map_locate:
                // 设置定位的类型为定位模式
                aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                break;
            case R.id.rbtn_repair_map_rotate:
                // 设置定位的类型为根据地图面向方向旋转
                aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                //将定位经纬度信息缓存到本地
                SharedPreferences.Editor editor = getSharedPreferences("now_data", MODE_PRIVATE).edit();
                editor.putString("my_latitude", amapLocation.getLatitude() + "");
                editor.putString("my_longitude", amapLocation.getLongitude() + "");
                editor.commit();
            } else {
                String errText = getString(R.string.location_wrong) + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                MyToast.error(this, errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
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
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_repair_map_flooded_places:
                WetPointActivity.start(this);
                break;
            case R.id.btn_repair_map_details:
                CheckDetailsActivity.start(this, mUploadInfoList.get(nowPoint));
                break;
            case R.id.btn_repair_map_foot:
                intent = new Intent(MapRepairActivity.this, MapRouteActivity.class);
                intent.putExtra("route_type", 0);
                intent.putExtra("info", mUploadInfoList.get(nowPoint));
                startActivity(intent);
                break;
            case R.id.btn_repair_map_bus:
                intent = new Intent(MapRepairActivity.this, MapRouteActivity.class);
                intent.putExtra("route_type", 1);
                intent.putExtra("info", mUploadInfoList.get(nowPoint));
                startActivity(intent);
                break;
            case R.id.btn_repair_map_drive:
                intent = new Intent(MapRepairActivity.this, MapRouteActivity.class);
                intent.putExtra("route_type", 2);
                intent.putExtra("info", mUploadInfoList.get(nowPoint));
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //底部tip设置可见
//        setInfoTipVisibile();

        //在mPointInfo查找选中的受灾点的信息，并显示在textview中
        for (int i = 0; i < mUploadInfoList.size(); i++) {
            if (marker.getTitle().equals(mUploadInfoList.get(i).getUpload_name())) {
                nowPoint = i;
                mSpinner.setSelection(i);
                refreshTipInfo(i);
            }
        }

        return false;
    }

    private void refreshTipInfo(int i) {
        tvName.setText(mUploadInfoList.get(i).getUpload_name());
        tvAddress.setText(mUploadInfoList.get(i).getUpload_address());
    }

//    private void setInfoTipVisibile() {
//        if (mCvInfoTips.getVisibility() == View.GONE) {
//            mCvInfoTips.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(10, 0, 0, 10);//设置margin
//            lp.addRule(RelativeLayout.ABOVE, R.id.rl_repair_map_info_tip);
//            mCvAcc.setLayoutParams(lp);//动态改变布局
//        }
//    }


    //从数据库获取点的信息
    private void receivePointInfo() {
        RequestHelper.getUploadInfos(new RequestListener() {
            @Override
            public void onResponce(String responce) {
                List<UploadInfo> uploadInfoList=RequestHelper.stringToArray(responce, UploadInfo[].class);
                for (UploadInfo uploadInfo : uploadInfoList) {
                    if (uploadInfo.getApproval_status()==1) {
                        mUploadInfoList.add(uploadInfo);
                    }
                }
                //初始化控件
                initView();
            }

            @Override
            public void onError(Throwable throwable) {
                MyToast.error(MapRepairActivity.this, R.string.load_error);
            }
        });

    }


}
