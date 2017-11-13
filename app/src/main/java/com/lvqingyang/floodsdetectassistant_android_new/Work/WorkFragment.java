package com.lvqingyang.floodsdetectassistant_android_new.Work;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestHelper;
import com.lvqingyang.floodsdetectassistant_android_new.net.RequestListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import frame.base.BaseFragment;
import frame.tool.MyToast;
import frame.tool.SolidRVBaseAdapter;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/10/14
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class WorkFragment extends BaseFragment {

    private android.widget.LinearLayout llcheck;
    private android.support.v7.widget.RecyclerView rvwork;
    private Toolbar mToolbar;
    private List<UploadInfo> mUploadInfoList=new ArrayList<>();
    private SolidRVBaseAdapter mAdapter;

    public static WorkFragment newInstance() {

        Bundle args = new Bundle();

        WorkFragment fragment = new WorkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_work, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mToolbar=view.findViewById(R.id.toolbar);
        initToolbar(mToolbar, getString(R.string.work), false);
        this.rvwork = (RecyclerView) view.findViewById(R.id.rv_work);
        this.llcheck = (LinearLayout) view.findViewById(R.id.ll_check);
    }

    @Override
    protected void setListener() {
        llcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedWorkActivity.start(getActivity());
            }
        });
    }

    @Override
    protected void initData() {
        loadWorks();

        mAdapter=new SolidRVBaseAdapter<UploadInfo>(getActivity(), mUploadInfoList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, UploadInfo bean) {
                holder.setImageFromInternet(R.id.iv_img, bean.getUploadResource());
                holder.setText(R.id.tv_name, bean.getUploadName());
                holder.setText(R.id.tv_location, bean.getUploadAddress());
                holder.setText(R.id.tv_time, new SimpleDateFormat("MM-dd HH:mm").format(new Date(bean.getUploadTime())));
                holder.setText(R.id.tv_state, getString(R.string.uncheck));
                holder.getView(R.id.tv_state).setBackgroundResource(R.drawable.bg_state_blue);
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_work;
            }

            @Override
            protected void onItemClick(int position, UploadInfo bean) {
                super.onItemClick(position, bean);
                CheckDetailsActivity.start(getActivity(), bean);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        loadWorks();
    }

    @Override
    protected void setData() {
        rvwork.setAdapter(mAdapter);
        rvwork.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    private void loadWorks(){
        RequestHelper.getUploadInfos(new RequestListener() {
            @Override
            public void onResponce(String responce) {
                mUploadInfoList.clear();
                List<UploadInfo> uploadInfoList=RequestHelper.stringToArray(responce, UploadInfo[].class);
                for (UploadInfo uploadInfo : uploadInfoList) {
                    if (uploadInfo.getApprovalStatus()==0) {
                        mUploadInfoList.add(uploadInfo);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable) {
                MyToast.error(getActivity(), R.string.load_error);
            }
        });
    }
}
