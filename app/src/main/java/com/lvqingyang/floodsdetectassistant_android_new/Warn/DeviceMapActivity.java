package com.lvqingyang.floodsdetectassistant_android_new.Warn;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.Warn.Chart.ChartActivity;
import com.lvqingyang.floodsdetectassistant_android_new.bean.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import frame.tool.MyToast;

public class DeviceMapActivity extends AppCompatActivity implements AMap.OnMarkerClickListener {

    //高德地图组件
    private MapView mMapView;
    private AMap mAMap;
    private ArrayList<MarkerOptions> listMarkerOptions;
    //Volley
    private RequestQueue mRequestQueue;
    private JsonArrayRequest jsonArrayRequest;
    //使用HandlerThread解决定时刷新问题
    private HandlerThread mHandlerThread;
    //子线程中的handler
    private Handler mThreadHandler;
    //以防退出界面后Handler还在执行
    private boolean isUpdateInfo;
    //用以表示该handler的常熟
    private static final int MSG_UPDATE_INFO = 0x110;
    //服务器数据
    ArrayList<DeviceInfo> listDeviceInfo = new ArrayList<>();
    //四种危险级别图标
    private final int[] iconWarn = {
            R.drawable.ic_warn_0, R.drawable.ic_warn_1,
            R.drawable.ic_warn_2, R.drawable.ic_warn_3};

    public static void start(Context context) {
        Intent starter = new Intent(context, DeviceMapActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        initeActionbar();
        initMap();
        initThread();
    }

    private void initeActionbar()
    {
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null) {
            localActionBar.setTitle(R.string.device_map);
            localActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initMap() {
        //初始化地图控制器对象
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            //addMarkersToMap();
        }
        mAMap.setOnMarkerClickListener(this);
    }

    private void initThread() {
        mHandlerThread = new HandlerThread("update");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                update();

                if (isUpdateInfo)
                    mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);
            }
        };

        //初始化RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);
        jsonArrayRequest = new JsonArrayRequest(
                "http://47.92.48.100:8080/GetMachineInfo",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                DeviceInfo deviceInfo = new DeviceInfo();
                                deviceInfo.setName(jsonObject.getString("name"));
                                deviceInfo.setDangerLevel(jsonObject.getInt("danger_level"));
                                deviceInfo.setPosition(jsonObject.getString("position"));
                                deviceInfo.setDescription(jsonObject.getString("description"));
                                deviceInfo.setLatitude(jsonObject.getDouble("latitude"));
                                deviceInfo.setLongitude(jsonObject.getDouble("longitude"));
                                listDeviceInfo.add(deviceInfo);
                            }
                            addMarkersToMap();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MyToast.error(DeviceMapActivity.this, R.string.load_error);
            }
        });
    }

    private void update() {
        mRequestQueue.add(jsonArrayRequest);

        //每隔4秒更新一次
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        if (listMarkerOptions == null)
            listMarkerOptions = new ArrayList<>();
        else
            listMarkerOptions.clear();
        for (int i = 0; i < listDeviceInfo.size(); i++) {
            LatLng latlng = new LatLng(listDeviceInfo.get(i).getLatitude(),
                    listDeviceInfo.get(i).getLongitude());
            MarkerOptions markerOption = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(getResources(),
                                    iconWarn[listDeviceInfo.get(i).getDangerLevel()])))
                    .title(listDeviceInfo.get(i).getName())
                    .position(latlng);
            listMarkerOptions.add(markerOption);
        }
        mAMap.clear();
        mAMap.addMarkers(listMarkerOptions, true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        //开始查询
        isUpdateInfo = true;
        mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        //停止查询
        //以防退出界面后Handler还在执行
        isUpdateInfo = false;
        mThreadHandler.removeMessages(MSG_UPDATE_INFO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        //释放资源
        mHandlerThread.quit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
