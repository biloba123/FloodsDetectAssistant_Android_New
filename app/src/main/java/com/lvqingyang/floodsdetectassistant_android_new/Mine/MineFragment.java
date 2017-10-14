package com.lvqingyang.floodsdetectassistant_android_new.Mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lvqingyang.floodsdetectassistant_android_new.R;

import de.hdodenhof.circleimageview.CircleImageView;
import frame.base.BaseFragment;
import frame.tool.MyPrefence;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/10/12
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class MineFragment extends BaseFragment {

    private android.support.v7.widget.Toolbar toolbar;
    private de.hdodenhof.circleimageview.CircleImageView civhead;
    private android.widget.TextView tvusername;
    private android.widget.RelativeLayout rluserinfo;
    private static final int REQUEST_EDIT_INFO = 607;
    private MyPrefence mPrefence;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();

        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mine, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        this.rluserinfo = (RelativeLayout) view.findViewById(R.id.rl_user_info);
        this.tvusername = (TextView) view.findViewById(R.id.tv_username);
        this.civhead = (CircleImageView) view.findViewById(R.id.civ_head);
        this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolbar(toolbar, getString(R.string.mine), false);
    }

    @Override
    protected void setListener() {
        rluserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), EditPersonalInfoActivity.class),
                        REQUEST_EDIT_INFO);
            }
        });
    }

    @Override
    protected void initData() {
        mPrefence=MyPrefence.getInstance(getActivity());
    }

    @Override
    protected void setData() {
        showUserInfo();
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mine,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_EDIT_INFO) {
            if (resultCode== Activity.RESULT_OK) {
                showUserInfo();
            }
        }
    }

    /**
     * 显示用户信息
     */
    private void showUserInfo(){
        String path=mPrefence.getString(getString(R.string.avater_path), null);
        if (path != null) {
            Glide.with(this)
                    .load(path)
                    .into(civhead);
        }

    }

}
