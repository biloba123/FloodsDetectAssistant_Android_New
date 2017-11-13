package com.lvqingyang.floodsdetectassistant_android_new.Discover;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.lvqingyang.floodsdetectassistant_android_new.R;

import java.util.ArrayList;

import static com.amap.api.location.AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.4.9 9:31
 * 修改备注：
 */
public class ReportAddressActivity extends AppCompatActivity implements LocationSource, AMapLocationListener
        , GeocodeSearch.OnGeocodeSearchListener ,AMap.OnPOIClickListener {

    private Button btn;

    private MapView mMapView;
    private AMap mAMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;

    private double mLatitude;
    private double mLongitude;
    private String mAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_address);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map_report_address);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        initView();
        initMap();
    }

    private void initView() {
        btn = (Button) findViewById(R.id.btn_report_address);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("latitude",mLatitude);
                intent.putExtra("longitude",mLongitude);
                intent.putExtra("address",mAddress);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


    private void initMap() {
        //初始化地图控制器对象
        if (mAMap == null) {
            mAMap = mMapView.getMap();

            mAMap.setLocationSource(this);// 设置定位监听
            mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mAMap.setOnPOIClickListener(this);

    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(double latitude, double longitude) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkerToMap() {
        ArrayList<MarkerOptions> listMarkerOptions = null;
        if (listMarkerOptions == null)
            listMarkerOptions = new ArrayList<>();
        else
            listMarkerOptions.clear();

        LatLng latlng = new LatLng(mLatitude, mLongitude);
        MarkerOptions markerOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.ic_location)))
                .position(latlng)
                .draggable(true);

        listMarkerOptions.add(markerOption);

        mAMap.clear();
        mAMap.addMarkers(listMarkerOptions, true);
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
            //获取一次定位结果：
            mLocationOption.setOnceLocation(true);
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(Hight_Accuracy);
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
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

                mLatitude = amapLocation.getLatitude();
                mLongitude = amapLocation.getLongitude();
                getAddress(mLatitude, mLongitude);
                addMarkerToMap();

            } else {
                String errText = getString(R.string.location_wrong) + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Toast.makeText(ReportAddressActivity.this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPOIClick(Poi poi) {
        mLatitude=poi.getCoordinate().latitude;
        mLongitude=poi.getCoordinate().longitude;
        addMarkerToMap();
        getAddress(mLatitude,mLongitude);
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
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        mAddress = result.getRegeocodeAddress().getFormatAddress();
        btn.setText(mAddress);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

}
