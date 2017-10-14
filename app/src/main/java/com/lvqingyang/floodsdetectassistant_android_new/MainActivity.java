package com.lvqingyang.floodsdetectassistant_android_new;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.lvqingyang.floodsdetectassistant_android_new.Mine.MineFragment;
import com.lvqingyang.floodsdetectassistant_android_new.Warn.WarnFragment;

import frame.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private MineFragment mMineFragment;
    private WarnFragment mWarnFragment;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);

        mFragmentManager=getSupportFragmentManager();
        if (paramBundle != null) {
            mMineFragment= (MineFragment) mFragmentManager
                    .findFragmentByTag(MineFragment.class.getName());
            mWarnFragment= (WarnFragment) mFragmentManager
                    .findFragmentByTag(WarnFragment.class.getName());
            mFragmentManager.beginTransaction()
                    .hide(mWarnFragment)
                    .hide(mMineFragment)
                    .commit();
        }else {
            mWarnFragment=WarnFragment.newInstance();
            mMineFragment=MineFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.content, mWarnFragment, WarnFragment.class.getName())
                    .add(R.id.content, mMineFragment, MineFragment.class.getName())
                    .hide(mMineFragment)
                    .commit();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
    }

    @Override
    protected void setListener() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_warn:{
                        mFragmentManager.beginTransaction()
                                .hide(mMineFragment)
                                .show(mWarnFragment)
                                .commit();
                        return true;
                    }
                    case R.id.navigation_work:{
                        return true;
                    }
                    case R.id.navigation_discover:{
                        return true;
                    }
                    case R.id.navigation_resource:{
                        return true;
                    }
                    case R.id.navigation_mine:{
                        mFragmentManager.beginTransaction()
                                .hide(mWarnFragment)
                                .show(mMineFragment)
                                .commit();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            case R.id.item_map:
                return false;
            case R.id.item_setting:
                return false;
        }
        return super.onOptionsItemSelected(paramMenuItem);
    }
}
