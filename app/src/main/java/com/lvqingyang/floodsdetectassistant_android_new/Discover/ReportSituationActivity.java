package com.lvqingyang.floodsdetectassistant_android_new.Discover;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lvqingyang.floodsdetectassistant_android_new.R;
import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;
import com.lvqingyang.floodsdetectassistant_android_new.net.UploadSituationTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import frame.base.BaseActivity;
import frame.tool.MyToast;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2016/7/21 8:05
 * 修改备注：
 */
public class ReportSituationActivity extends BaseActivity {
    private EditText etName;
    private Spinner spType;
    private final String[] type = {"公众", "维修人员"};
    private TextView tvAddress;
    private ImageView ivAddress;
    private EditText etDescribe;
    private ImageView ivPicture;
    private Bitmap mBitmap = null;
    private File fileImage=null;
    private Uri imageUri;

    private double mLatitude;
    private double mLongitude;
    public static final int CHOOSE_ADDRESS = 11;
    public static final int TAKE_PHOTO = 12;
    public static final int CHOOSE_PHOTO = 13;
    private TextView mTvPostImg;

    public static void start(Context context) {
        Intent starter = new Intent(context, ReportSituationActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_situation;
    }

    protected void initView() {
        initeActionbar(R.string.upload, true);
        etName = (EditText) findViewById(R.id.et_report_name);
        spType = (Spinner) findViewById(R.id.sp_report_type);
        tvAddress = (TextView) findViewById(R.id.tv_report_address);
        ivAddress = (ImageView) findViewById(R.id.iv_report_address);
        etDescribe = (EditText) findViewById(R.id.et_report_describe);
        mTvPostImg = (TextView) findViewById(R.id.tv_post_img);
        ivPicture = (ImageView) findViewById(R.id.iv_report_picture);




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, type);
        spType.setAdapter(adapter);

    }

    @Override
    protected void setListener() {
        ivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportSituationActivity.this, ReportAddressActivity.class);
                startActivityForResult(intent, CHOOSE_ADDRESS);
            }
        });

        mTvPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] type = {"拍照上传", "从相册中选择"};
                final AlertDialog.Builder alert = new AlertDialog.Builder(ReportSituationActivity.this);
                alert.setTitle("上传图片")
                        .setSingleChoiceItems(type, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        takePhoto();
                                        break;
                                    case 1:
                                        choosePhoto();
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
                alert.show();

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

    private void takePhoto() {
        fileImage = new File(getExternalCacheDir(), "ouput_image.jpg");
        try {
            if (fileImage.exists())
                fileImage.delete();
            fileImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(fileImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    //如果第一次打开，就申请访问权限
    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(ReportSituationActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ReportSituationActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, CHOOSE_PHOTO);
        }
    }

    private void postInfo() {
        String name = etName.getText().toString();
        String address = tvAddress.getText().toString();
        String describe = etDescribe.getText().toString();
        double latitude = mLatitude;
        double longitude = mLongitude;

//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//        String dateNowStr = sdf.format(date);

        if (mBitmap == null)
            MyToast.info(ReportSituationActivity.this, "请选择上报图片");
        if (describe.equals(""))
            MyToast.info(ReportSituationActivity.this, "请输入描述信息");
        if (address.equals(""))
            MyToast.info(ReportSituationActivity.this, "请选择上报地点");
        if (name.equals(""))
            MyToast.info(ReportSituationActivity.this, "请输入上报名称");

        UploadInfo info=new UploadInfo();
        info.setUploadName(name);
        info.setUploadType(spType.getSelectedItem().toString());
        info.setLongitude(longitude);
        info.setLatitude(latitude);
        info.setUploadAddress(address);
//        info.setLongitude(114.275386);
//        info.setLatitude(30.452728);
//        info.setUploadAddress("虚拟机测试地点");
        info.setUploadDescription(describe);
        info.setApprovalStatus(0);

        //设置编码类型为utf-8
        UploadSituationTask task=new UploadSituationTask(
                ReportSituationActivity.this,"http://47.92.48.100:8099/urban/upload", info,fileImage);
        task.upload();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_ADDRESS:
                    mLatitude = data.getDoubleExtra("latitude", 30.4);
                    mLongitude = data.getDoubleExtra("longitude", 114.2);
                    tvAddress.setText(data.getStringExtra("address"));
                    break;
                case TAKE_PHOTO:
                    try {
                        mBitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        ivPicture.setImageBitmap(mBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case CHOOSE_PHOTO:
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                    break;
            }
        }

    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果不是document类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实地图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            fileImage=new File(imagePath);
            mBitmap = BitmapFactory.decodeFile(imagePath);
            ivPicture.setImageBitmap(mBitmap);
        } else {
            Toast.makeText(this, "图片获取失败了", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId()== R.id.item_post) {
            postInfo();
        }
        return super.onOptionsItemSelected(paramMenuItem);
    }
}
