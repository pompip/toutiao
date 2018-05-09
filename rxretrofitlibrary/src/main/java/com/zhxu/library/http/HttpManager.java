package com.zhxu.library.http;

import android.app.Dialog;
import android.util.Log;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.zhxu.library.RxRetrofitApp;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.exception.RetryWhenNetworkException;
import com.zhxu.library.listener.HttpOnNextListener;
import com.zhxu.library.subscribers.ProgressSubscriber;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * http交互处理类
 */
public class HttpManager  {
    private volatile static HttpManager INSTANCE;
    private Dialog mProgressDialog;
    private Class mDialogClass;

    //构造方法私有
    private HttpManager() {
    }

    //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 处理http请求
     *
     * @param basePar 封装的请求数据
     */
    public void doHttpDeal(BaseApi basePar) {
        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(basePar.getConnectionTime(), TimeUnit.SECONDS);
       // builder.addInterceptor(new CookieInterceptor(basePar.isCache(), basePar.getUrl()));
        //添加 Cookie
        builder.addInterceptor(new ParamsInterceptor(basePar.getRxAppCompatActivity()));

        if(RxRetrofitApp.isDebug()){
            builder.addInterceptor(getHttpLoggingInterceptor());
        }

        /*创建retrofit对象*/
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(basePar.getBaseUrl())
                .build();



        /*rx处理*/

        ProgressSubscriber subscriber = new ProgressSubscriber(basePar,mDialogClass);

        //定义Observable，并没有执行，只有订阅的时候才会执行
        Observable observable = basePar.getObservable(retrofit)
                 /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException(basePar.getRetryCount(),
                        basePar.getRetryDelay(), basePar.getRetryIncreaseDelay()))
                /*生命周期管理*/
//                .compose(basePar.getRxAppCompatActivity().bindToLifecycle())
                .compose(basePar.getRxAppCompatActivity().bindUntilEvent(ActivityEvent.DESTROY))
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                .map(basePar);


        /*链接式对象返回*/
       // SoftReference<HttpOnNextListener> httpOnNextListener = basePar.getListener();
        HttpOnNextListener httpOnNextListener =basePar.getListener();
        if (httpOnNextListener != null) {
            httpOnNextListener.onNext(observable);
        }

        /*数据回调*///完成订阅
        observable.subscribe(subscriber);

    }


    /**
     * 日志输出
     * 自行判定是否添加
     * @return
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor(){
        //日志显示级别
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("RxRetrofit","Retrofit====Message:"+message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

    /**
     *从外边注入一个 对话框的class对象；
     */
    public  void setProgressDialogClass(Class dialogClass){
        mDialogClass = dialogClass;
    }

}
