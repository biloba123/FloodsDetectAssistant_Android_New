package com.lvqingyang.floodsdetectassistant_android_new.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.lvqingyang.floodsdetectassistant_android_new.bean.UploadInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.6.3 15:39
 * 修改备注：
 */
public class UploadSituationTask {

    private String mUrl;
    private UploadInfo mInfo;
    private File mFile;
    private Context mContext;
    private ProgressDialog dialog;
    private Handler handler = new Handler();

    public UploadSituationTask(Context context, String url, UploadInfo info, File file) {
        mContext = context;
        mUrl = url;
        mInfo = info;
        mFile = file;
    }

    public void upload() {

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("上传中，请等待。");
        dialog.show();

        OkHttpClient client = new OkHttpClient();

        File file= getSmallPicture(mFile);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_name", mInfo.getUploadName())
                .addFormDataPart("upload_type", mInfo.getUploadType())
                .addFormDataPart("longitude", mInfo.getLongitude() + "")
                .addFormDataPart("latitude", mInfo.getLatitude() + "")
                .addFormDataPart("upload_address", mInfo.getUploadAddress())
                .addFormDataPart("upload_time", new Date().getTime() + "")
                .addFormDataPart("upload_description", mInfo.getUploadDescription())
                .addFormDataPart("approval_status", mInfo.getApprovalStatus() + "")
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.jpg\""),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request request = new Request.Builder()
                .url(mUrl)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });

    }

    private File getSmallPicture(File file) {
        Bitmap bitmap= BitmapFactory.decodeFile(file.getPath());
        while(bitmap.getByteCount()/1024/1024>3){
            bitmap=bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,true);
        }

        File f=new File(Environment.getExternalStorageDirectory(), "smallBitmap.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


}
