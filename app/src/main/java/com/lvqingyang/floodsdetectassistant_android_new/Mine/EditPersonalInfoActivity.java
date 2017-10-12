package com.lvqingyang.floodsdetectassistant_android_new.Mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lvqingyang.floodsdetectassistant_android_new.BuildConfig;
import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import frame.base.BaseActivity;
import frame.tool.MyPrefence;
import frame.tool.MyToast;
import frame.view.CardItem;


public class EditPersonalInfoActivity extends BaseActivity {

    /**
     * view
     */
    private CircleImageView civhead;
    private CardItem cisex;
    private CardItem cipwd;
    private CardItem cinick;
    private CardItem cibirthday;
    private CardItem ciaccount;

     /**
     * fragment
     */


     /**
     * data
     */
     private MyPrefence mPrefence;

     /**
     * tag
     */
     private static final String TAG = "EditPersonalInfoActivit";
    private static final String FILE_AVATER = "avater.png";
    private RelativeLayout mRlAvater;
    private static final int CHOOSE_PHOTO = 588;
    private CircleImageView mCivHead;

    public static void start(Context context) {
        Intent starter = new Intent(context, EditPersonalInfoActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    public static Intent newIntent(Context context) {
        Intent starter = new Intent(context, EditPersonalInfoActivity.class);
//        starter.putExtra();
        return starter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.edit_personal_info,true);
        this.cibirthday = (CardItem) findViewById(R.id.ci_birthday);
        this.cinick = (CardItem) findViewById(R.id.ci_nick);
        this.ciaccount = (CardItem) findViewById(R.id.ci_account);
        this.cipwd = (CardItem) findViewById(R.id.ci_pwd);
        this.cisex = (CardItem) findViewById(R.id.ci_sex);
        this.civhead = (CircleImageView) findViewById(R.id.civ_head);
        mRlAvater = (RelativeLayout) findViewById(R.id.ll_avater);
        mCivHead = (CircleImageView) findViewById(R.id.civ_head);
    }

    @Override
    protected void setListener() {
        mRlAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("image/*");
                startActivityForResult(intent2,CHOOSE_PHOTO);
            }
        });

    }

    @Override
    protected void initData() {
        mPrefence=MyPrefence.getInstance(this);
    }

    @Override
    protected void setData() {
        showUserInfo();
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CHOOSE_PHOTO:{
                if (resultCode==RESULT_OK){
                    deleteRecursive(getFilesDir());

                    Uri source=data.getData();
                    String sourceStr=source.toString();

                    UCrop.of(source,
                            Uri.fromFile(new File(getFilesDir(), sourceStr.substring(sourceStr.lastIndexOf('/')))))
                            .withAspectRatio(1, 1)
                            .start(this);
                }
            }

            case UCrop.REQUEST_CROP:{
                if (resultCode == RESULT_OK ) {
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        String path=resultUri.getPath();
                        if (BuildConfig.DEBUG) Log.d(TAG, "onActivityResult: "+path);
                        mPrefence.saveString(getString(R.string.avater_path), resultUri.getPath());
                        //显示新头像
                        Glide.with(EditPersonalInfoActivity.this)
                                .load(path)
                                .into(civhead);
                        setResult(Activity.RESULT_OK);
                    }

                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    if (BuildConfig.DEBUG) Log.d(TAG, "onActivityResult: "+cropError);
                    MyToast.error(this, R.string.error);
                }
            }
        }
    }

    /**
     * 删除目录下所有文件
     * @param dir
     */
    void deleteRecursive(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }



}
