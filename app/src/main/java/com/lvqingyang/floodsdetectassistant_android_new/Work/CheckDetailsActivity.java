package com.lvqingyang.floodsdetectassistant_android_new.Work;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.lvqingyang.floodsdetectassistant_android_new.Discover.MapRouteActivity;
import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import frame.tool.MyToast;


/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.3.27 16:50
 * 修改备注：
 */
public class CheckDetailsActivity extends AppCompatActivity {
    //控件
    private ImageView ivShow;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvStatus;
    private TextView tvTime;
    private TextView tvType;
    private TextView tvDescribe;
    private Button btnGo;
    private LinearLayout llOperate;
    private Button btnAccept;
    private Button btnReject;
    //volley
    private RequestQueue mRequestQueue;
    //数据
    private UploadInfo mUploadInfo;
    //0为未审核，1为已通过，2为已拒绝
    private final String[] status = {"未审核", "已通过", "已拒绝"};
    private static final String KEY_UPLOAD_INFO = "UPLOAD_INFO";

    public static void start(Context context, UploadInfo uploadInfo) {
        Intent starter = new Intent(context, CheckDetailsActivity.class);
        starter.putExtra(KEY_UPLOAD_INFO, uploadInfo);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_details);

        mRequestQueue = Volley.newRequestQueue(this);
        mUploadInfo = (UploadInfo) getIntent().getSerializableExtra(KEY_UPLOAD_INFO);
        initView();
    }

    private void initView() {
        ivShow = (ImageView) findViewById(R.id.iv_check_details_show);
        tvName = (TextView) findViewById(R.id.tv_check_details_name);
        tvAddress = (TextView) findViewById(R.id.tv_check_details_address);
        tvStatus = (TextView) findViewById(R.id.tv_check_details_status);
        tvTime = (TextView) findViewById(R.id.tv_check_details_time);
        tvType = (TextView) findViewById(R.id.tv_check_details_type);
        tvDescribe = (TextView) findViewById(R.id.tv_check_details_describe);
        btnGo = (Button) findViewById(R.id.btn_check_details_go);
        llOperate = (LinearLayout) findViewById(R.id.ll_check_details_operate);
        btnAccept = (Button) findViewById(R.id.btn_check_details_accept);
        btnReject = (Button) findViewById(R.id.btn_check_details_reject);


        Glide.with(this)
                .load(mUploadInfo.getUploadResource())
                .into(ivShow);
        tvName.setText(mUploadInfo.getUploadName());
        tvAddress.setText(mUploadInfo.getUploadAddress());
        tvStatus.setText(status[mUploadInfo.getApprovalStatus()]);
        switch (mUploadInfo.getApprovalStatus()) {
            case 0:
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                break;
            case 1:
                tvStatus.setTextColor(getResources().getColor(R.color.accent_green));
                break;
            case 2:
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                break;
        }
        tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mUploadInfo.getUploadTime())));
        tvType.setText(mUploadInfo.getUploadType());
        tvDescribe.setText(mUploadInfo.getUploadDescription());
        if (mUploadInfo.getApprovalStatus() == 0)
            llOperate.setVisibility(View.VISIBLE);
        else {
            llOperate.setVisibility(View.GONE);
        }
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckDetailsActivity.this, MapRouteActivity.class);
                intent.putExtra("info", mUploadInfo);
                intent.putExtra("route_type", 2);
                startActivity(intent);
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(
                        "http://47.92.48.100:8099/urban/changeStatus?" +
                                "id=" + mUploadInfo.getId() + "&status=" + 1,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(CheckDetailsActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                MyToast.error(CheckDetailsActivity.this, R.string.op_error);
                            }
                        });
                mRequestQueue.add(stringRequest);

            }
        });
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(
                        "http://47.92.48.100:8099/urban/changeStatus?" +
                                "id=" + mUploadInfo.getId() + "&status=" + 2,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(CheckDetailsActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                MyToast.error(CheckDetailsActivity.this, R.string.op_error);
                            }
                        });
                mRequestQueue.add(stringRequest);
                finish();
            }
        });
    }


}
