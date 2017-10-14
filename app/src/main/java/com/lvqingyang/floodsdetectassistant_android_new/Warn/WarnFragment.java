package com.lvqingyang.floodsdetectassistant_android_new.Warn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.Warn.Chart.ChartActivity;
import com.lvqingyang.floodsdetectassistant_android_new.bean.DeviceInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestHelper;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import frame.base.BaseFragment;
import frame.tool.MyToast;
import frame.tool.SolidRVBaseAdapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/10/13
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class WarnFragment extends BaseFragment {

    private Toolbar mToolbar;
    private RecyclerView mRvDevice;
    private List<DeviceInfo> mDeviceInfoList=new ArrayList<>();
    private SolidRVBaseAdapter mAdapter;
    private static final String TAG = "WarnFragment";
    private FloatingActionButton mFloatingActionButton;

    public static WarnFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WarnFragment fragment = new WarnFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_warn, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolbar(mToolbar, getString(R.string.warn), false);
        mRvDevice = (RecyclerView) view.findViewById(R.id.rv_device);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_map);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        RequestHelper.getDevices(new RequestListener() {
            @Override
            public void onResponce(String res) {
                mAdapter.clearAllItems();
                mAdapter.addItems(RequestHelper.stringToArray(res, DeviceInfo[].class));
            }

            @Override
            public void onError(Throwable throwable) {
                MyToast.error(getActivity(), R.string.load_error);
            }
        });
        
        mAdapter=new SolidRVBaseAdapter<DeviceInfo>(getActivity(), mDeviceInfoList) {
            @Override
            protected void onBindDataToView(SolidRVBaseAdapter.SolidCommonViewHolder holder, DeviceInfo bean) {
                holder.setText(R.id.tv_name, bean.getName());
                holder.setText(R.id.tv_location, bean.getPosition());
                holder.setText(R.id.tv_description, bean.getDescription());
                ImageView ivWarn= (ImageView) holder.getView(R.id.iv_warn);
                ivWarn.getDrawable().setLevel(bean.getDangerLevel()*10);
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_device;
            }

            @Override
            protected void onItemClick(int position, DeviceInfo bean) {
                super.onItemClick(position, bean);
                Intent intent=new Intent(mContext, ChartActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceMapActivity.start(getActivity());
            }
        });
    }

    @Override
    protected void setData() {
        mRvDevice.setAdapter(mAdapter);
        mRvDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_warn, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.item_map) {
            DeviceMapActivity.start(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }
}
