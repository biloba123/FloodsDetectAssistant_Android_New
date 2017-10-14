package com.lvqingyang.floodsdetectassistant_android_new.net;

import android.util.Log;

import com.google.gson.Gson;
import com.lvqingyang.floodsdetectassistant_android_new.BuildConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import frame.tool.MyOkHttp;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
public class RequestHelper {
    private static MyOkHttp sMyOkHttp=MyOkHttp.getInstance();
    private static Gson mGson=new Gson();
    private static final String TAG = "RequestHelper";
    private static final String GET_DEVICE = "http://47.92.48.100:8080/GetMachineInfo";
    private static final String GET_UPLOAD_INFO = "http://47.92.48.100:8099/urban/getAll";

    public static <T> void getResult(final String url, final RequestListener listener){
        Subscription subscribe = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(sMyOkHttp.run(url));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onNext: " + s);
                        listener.onResponce(s);
                    }
                });
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }



    public static void getDevices(RequestListener lis){
        getResult(GET_DEVICE, lis);
    }

    public static void getUploadInfos(RequestListener lis){
        getResult(GET_UPLOAD_INFO, lis);
    }
}
