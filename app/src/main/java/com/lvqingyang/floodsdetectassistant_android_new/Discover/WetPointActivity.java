package com.lvqingyang.floodsdetectassistant_android_new.Discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.Work.CheckDetailsActivity;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestHelper;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import frame.base.BaseActivity;
import frame.tool.SolidRVBaseAdapter;

public class WetPointActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private List<UploadInfo> mUploadInfoList=new ArrayList<>();
    private SolidRVBaseAdapter mAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, WetPointActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wet_point;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.wet_point, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_wet_point);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        onRetryClick();

        mAdapter=new SolidRVBaseAdapter<UploadInfo>(this, mUploadInfoList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, final UploadInfo bean) {
                holder.setImageFromInternet(R.id.iv_img, bean.getUploadResource());
                holder.setText(R.id.tv_name, bean.getUploadName());
                holder.setText(R.id.tv_other, bean.getUploadAddress());
                Button btnGo=holder.getView(R.id.btn_go);
                btnGo.setVisibility(View.VISIBLE);
                btnGo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WetPointActivity.this, MapRouteActivity.class);
                        intent.putExtra("info", bean);
                        intent.putExtra("route_type", 2);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_wet_point;
            }

            @Override
            protected void onItemClick(int position, UploadInfo bean) {
                super.onItemClick(position, bean);
                CheckDetailsActivity.start(WetPointActivity.this, bean);
            }
        };
    }

    @Override
    protected void setData() {
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    @Override
    protected void onRetryClick() {
        super.onRetryClick();
        loadWetPoints();
    }

    private void loadWetPoints() {
        RequestHelper.getUploadInfos(new RequestListener() {
            @Override
            public void onResponce(String responce) {
                mUploadInfoList.clear();
                List<UploadInfo> uploadInfoList=RequestHelper.stringToArray(responce, UploadInfo[].class);
                for (UploadInfo uploadInfo : uploadInfoList) {
                    if (uploadInfo.getApprovalStatus()==1) {
                        mUploadInfoList.add(uploadInfo);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount()>0) {
                    loadComplete();
                }else {
                    showEmpty();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                loadFail();
            }
        });
    }
}
