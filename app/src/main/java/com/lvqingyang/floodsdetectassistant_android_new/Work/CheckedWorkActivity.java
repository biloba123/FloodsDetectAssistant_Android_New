package com.lvqingyang.floodsdetectassistant_android_new.Work;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestHelper;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import frame.base.BaseActivity;
import frame.tool.SolidRVBaseAdapter;

public class CheckedWorkActivity extends BaseActivity {

    private android.support.v7.widget.RecyclerView rvwork;
    private List<UploadInfo> mUploadInfoList=new ArrayList<>();
    private SolidRVBaseAdapter mAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, CheckedWorkActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_checked_work;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.checked_work, true);
        this.rvwork = (RecyclerView) findViewById(R.id.rv_work);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        loadWorks();

        mAdapter=new SolidRVBaseAdapter<UploadInfo>(this, mUploadInfoList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, UploadInfo bean) {
                holder.setImageFromInternet(R.id.iv_img, bean.getUpload_resource());
                holder.setText(R.id.tv_name, bean.getUpload_name());
                holder.setText(R.id.tv_location, bean.getUpload_address());
                holder.setText(R.id.tv_time, new SimpleDateFormat("MM-dd HH:mm").format(new Date(bean.getUpload_time())));
                if (bean.getApproval_status()==1) {
                    holder.setText(R.id.tv_state, getString(R.string.accepted));
                    holder.getView(R.id.tv_state).setBackgroundResource(R.drawable.bg_state_green);
                }else if (bean.getApproval_status()==2) {
                    holder.setText(R.id.tv_state, getString(R.string.denied));
                    holder.getView(R.id.tv_state).setBackgroundResource(R.drawable.bg_state_red);
                }

            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_work;
            }

            @Override
            protected void onItemClick(int position, UploadInfo bean) {
                super.onItemClick(position, bean);
                CheckDetailsActivity.start(CheckedWorkActivity.this, bean);
            }
        };
    }

    @Override
    protected void setData() {
        rvwork.setAdapter(mAdapter);
        rvwork.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    private void loadWorks(){
        loading();
        RequestHelper.getUploadInfos(new RequestListener() {
            @Override
            public void onResponce(String responce) {
                mUploadInfoList.clear();
                List<UploadInfo> uploadInfoList=RequestHelper.stringToArray(responce, UploadInfo[].class);
                for (UploadInfo uploadInfo : uploadInfoList) {
                    if (uploadInfo.getApproval_status()!=0) {
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

    @Override
    protected void onRetryClick() {
        super.onRetryClick();
        loadFail();
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
