package com.lvqingyang.floodsdetectassistant_android_new.Discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.lvqingyang.floodsdetectassistant_android_new.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import frame.tool.MyToast;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.3.29 8:36
 * 修改备注：
 */
public class WeatherActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private TextView tvCity;
    private TextView tvDate;
    private TextView tvTemperature;
    private TextView tvType;
    private TextView tvDescribe;
    private TextView tvTime;
    private TextView tvHigh;
    private TextView tvLow;
    private TextView tvWindDirection;
    private TextView tvWindPower;
    private ImageView mImageView;

    public static void start(Context context) {
        Intent starter = new Intent(context, WeatherActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();
        receiveInfo();
    }

    private void initView() {
        tvCity=(TextView)findViewById(R.id.tv_weather_city);
        tvDate =(TextView)findViewById(R.id.tv_weather_date);
        tvTemperature=(TextView)findViewById(R.id.tv_weather_temperature);
        tvType=(TextView)findViewById(R.id.tv_weather_type);
        tvDescribe=(TextView)findViewById(R.id.tv_weather_describe);
        tvTime=(TextView)findViewById(R.id.tv_weather_time);
        tvHigh=(TextView)findViewById(R.id.tv_weather_high);
        tvLow=(TextView)findViewById(R.id.tv_weather_low);
        tvWindDirection=(TextView)findViewById(R.id.tv_weather_wind_direction);
        mImageView = (ImageView) findViewById(R.id.iv_bg);
        Glide.with(this)
                .load(R.mipmap.img_weather)
                .into(mImageView);

        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(date);
        tvTime.setText(time);
    }

    private void receiveInfo() {
        mRequestQueue= Volley.newRequestQueue(this);

        JsonObjectRequest mJsonObjectRequest=new JsonObjectRequest(
                "http://wthrcdn.etouch.cn/weather_mini?citykey=101200101",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data=new JSONObject(response.getString("data"));
                            JSONArray forecast=data.getJSONArray("forecast");
                            JSONObject todayWeather=forecast.getJSONObject(0);

                            tvCity.setText(data.getString("city"));
                            tvTemperature.setText(data.getString("wendu")+"°");
                            tvDescribe.setText(data.getString("ganmao"));
                            tvDate.setText(todayWeather.getString("date"));
                            tvType.setText(todayWeather.getString("type"));
                            tvHigh.setText(todayWeather.getString("high"));
                            tvLow.setText(todayWeather.getString("low"));
                            tvWindDirection.setText(todayWeather.getString("fengxiang"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyToast.error(WeatherActivity.this, R.string.load_error);
            }
        });
        mRequestQueue.add(mJsonObjectRequest);
    }
}
