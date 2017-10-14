package com.lvqingyang.floodsdetectassistant_android_new.Warn.Chart;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.bean.DeviceDataInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import frame.tool.MyToast;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.3.13 8:45
 * 修改备注：
 */
//思路是每几秒获取一次数据，然后“历史数据”就是根据已经获得的数据更新界面
// 而如果选择的是“实时刷新”，那么每次获取到新的数据后都会更新界面
public class ChartActivity extends AppCompatActivity {

    //控件
    private TabLayout tlType;
    private Spinner spLevel;
    private final String[] level = {"历史数据", "实时更新"};
    private final String[] stringTag = {"总况", "雨量", "雨水等级", "下水流速", "下水位", "风速"};
    private final String[] yUnit = {"总况", "毫米", "无", "米每秒", "厘米", "米每秒"};
    //volley
    private RequestQueue mRequestQueue;
    private JsonArrayRequest jsonArrayRequest;
    //实时刷新使用HandlerThread
    private HandlerThread mHandlerThread;
    //子线程中的handler
    private Handler mThreadHandler;
    //以防退出界面后Handler还在执行
    private Boolean isUpdateInfo = new Boolean(false);
    //用以表示该handler的常数
    private static final int MSG_UPDATE_INFO = 0x110;
    //服务器数据
    ArrayList<DeviceDataInfo> listDeviceDataInfo = null;
    //此时所在界面
    private int nowTip = 0;
    //第一次进入界面，需要获取到数据才能显示历史数据
    private boolean isFirst = true;

    public static void start(Context context) {
        Intent starter = new Intent(context, ChartActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initVolley();
        initView();
        initeActionbar();
    }

    private void initeActionbar()
    {
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null) {
            localActionBar.setTitle(R.string.monitoring_data);
            localActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initVolley() {
        //初始化RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);
        jsonArrayRequest = new JsonArrayRequest(
                "http://47.92.48.100:8080/Getthirtydata",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (listDeviceDataInfo == null) {
                            listDeviceDataInfo = new ArrayList<>();
                        } else {
                            listDeviceDataInfo.clear();
                        }
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                DeviceDataInfo deviceDataInfo = new DeviceDataInfo();
                                deviceDataInfo.setRainFall(jsonObject.getDouble("rain_fall"));
                                deviceDataInfo.setRainLevel(jsonObject.getDouble("rain_level"));
                                deviceDataInfo.setWaterSpeed(jsonObject.getDouble("water_speed"));
                                deviceDataInfo.setWaterLevel(jsonObject.getDouble("water_level"));
                                deviceDataInfo.setWindSpeed(jsonObject.getDouble("wind_speed"));
                                deviceDataInfo.setGeneralLevel(jsonObject.getDouble("general_level"));
                                listDeviceDataInfo.add(deviceDataInfo);
                            }
                            //如果是选择的历史数据，那么就刷新
                            if (spLevel.getSelectedItemPosition() == 1)
                                chooseInfoToChart();
                            //如果是第一次进入界面，也需要刷新
                            if (isFirst) {
                                chooseInfoToChart();
                                isFirst = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MyToast.error(ChartActivity.this, R.string.load_error);
            }
        });
    }

    private void chooseInfoToChart() {
        //选择数据，前后设为0，好看
        double data[] = new double[32];
        data[0] = 0;
        data[31] = 0;
        switch (nowTip) {
            case 0:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getGeneralLevel();
                }
                break;
            case 1:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getRainFall();
                }
                break;
            case 2:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getRainLevel();
                }
                break;
            case 3:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getWaterSpeed();
                }
                break;
            case 4:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getWaterLevel();
                }
                break;
            case 5:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getWindSpeed();
                }
                break;
            default:
                for (int i = 0; i < 30; i++) {
                    data[i + 1] = listDeviceDataInfo.get(i).getGeneralLevel();
                }
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("x_unit", "条目");
        bundle.putString("y_unit", yUnit[nowTip]);
        bundle.putDoubleArray("data", data);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LineChartFragment fraLineChart = new LineChartFragment();
        fraLineChart.setArguments(bundle);

        ft.replace(R.id.fl_chart, fraLineChart);
        ft.commit();
    }

    private void initView() {

        spLevel = (Spinner) findViewById(R.id.sp_chart_level);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, level);
        spLevel.setAdapter(adapter);
        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //如果不是第一次进入界面，那么就根据获取到的数据刷新View
                if (!isFirst)
                    chooseInfoToChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tlType = (TabLayout) findViewById(R.id.tl_chart_type);
        tlType.setTabMode(TabLayout.MODE_SCROLLABLE);
        tlType.setTabGravity(TabLayout.GRAVITY_FILL);
        for (int i = 0; i < stringTag.length; i++) {
            tlType.addTab(tlType.newTab().setText(stringTag[i]), i);
        }


        //初始化mHandlerThread并开启
        mHandlerThread = new HandlerThread("receive_line_chart_data");
        mHandlerThread.start();
        //初始化实时刷新需要的Handler
        mThreadHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                realTimeUpdate();//模拟数据更新

                if (isUpdateInfo)
                    mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);
            }
        };

        tlType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                //记录当前tip的位置
                nowTip = tab.getPosition();

                //刷新折线图
                if (!isFirst)
                    chooseInfoToChart();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tlType.getTabAt(4).select();


    }

    private void realTimeUpdate() {
        if (isUpdateInfo)
            mRequestQueue.add(jsonArrayRequest);
        //每4秒更新一次
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isUpdateInfo = true;
        mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止查询
        //以防退出界面后Handler还在执行
        isUpdateInfo = false;
        mThreadHandler.removeMessages(MSG_UPDATE_INFO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mHandlerThread.quit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
