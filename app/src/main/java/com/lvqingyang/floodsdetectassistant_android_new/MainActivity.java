package com.lvqingyang.floodsdetectassistant_android_new;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.lvqingyang.floodsdetectassistant_android_new.Discover.DiscoverFragment;
import com.lvqingyang.floodsdetectassistant_android_new.Mine.MineFragment;
import com.lvqingyang.floodsdetectassistant_android_new.Resource.ResourceFragment;
import com.lvqingyang.floodsdetectassistant_android_new.Warn.WarnFragment;
import com.lvqingyang.floodsdetectassistant_android_new.Work.WorkFragment;

import java.util.ArrayList;
import java.util.List;

import frame.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private WarnFragment mWarnFragment;
    private WorkFragment mWorkFragment;
    private DiscoverFragment mDiscoverFragment;
    private ResourceFragment mResourceFragment;
    private MineFragment mMineFragment;
    private List<Fragment> mFragmentList=new ArrayList<>();
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);

        mFragmentManager=getSupportFragmentManager();
        if (paramBundle != null) {
            mWarnFragment= (WarnFragment) mFragmentManager
                    .findFragmentByTag(WarnFragment.class.getName());
            mWorkFragment= (WorkFragment) mFragmentManager
                    .findFragmentByTag(WorkFragment.class.getName());
            mDiscoverFragment= (DiscoverFragment) mFragmentManager
                    .findFragmentByTag(DiscoverFragment.class.getName());
            mResourceFragment= (ResourceFragment) mFragmentManager
                    .findFragmentByTag(ResourceFragment.class.getName());
            mMineFragment= (MineFragment) mFragmentManager
                    .findFragmentByTag(MineFragment.class.getName());
        }else {
            mWarnFragment=WarnFragment.newInstance();
            mWorkFragment=WorkFragment.newInstance();
            mDiscoverFragment=DiscoverFragment.newInstance();
            mResourceFragment=ResourceFragment.newInstance();
            mMineFragment=MineFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.content, mWarnFragment, WarnFragment.class.getName())
                    .add(R.id.content, mWorkFragment, WorkFragment.class.getName())
                    .add(R.id.content, mDiscoverFragment, DiscoverFragment.class.getName())
                    .add(R.id.content, mResourceFragment, ResourceFragment.class.getName())
                    .add(R.id.content, mMineFragment, MineFragment.class.getName())
                    .commit();
        }

        mFragmentList.add(mWarnFragment);
        mFragmentList.add(mWorkFragment);
        mFragmentList.add(mDiscoverFragment);
        mFragmentList.add(mResourceFragment);
        mFragmentList.add(mMineFragment);

        mBottomNavigationView.setSelectedItemId(R.id.navigation_discover);
        showFragment(mDiscoverFragment);
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
                        showFragment(mWarnFragment);
                        return true;
                    }
                    case R.id.navigation_work:{
                        showFragment(mWorkFragment);
                        return true;
                    }
                    case R.id.navigation_discover:{
                        showFragment(mDiscoverFragment);
                        return true;
                    }
                    case R.id.navigation_resource:{
                        showFragment(mResourceFragment);
                        return true;
                    }
                    case R.id.navigation_mine:{
                        showFragment(mMineFragment);
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

    private void showFragment(Fragment fragment){
        FragmentTransaction transaction=mFragmentManager.beginTransaction();
        for (Fragment fragment1 : mFragmentList) {
            if (fragment1.equals(fragment)) {
                transaction.show(fragment1);
            }else {
                transaction.hide(fragment1);
            }
        }
        transaction.commit();
    }
}
