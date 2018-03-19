package spnetworking.sbiautomate;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MyApplication extends Application {
    public static OkHttpClient client;
    //public static String domain = "http://192.168.0.5:5000";
    public static String domain = "https://mediaboxcloud.in";

    @Override
    public void onCreate() {
        super.onCreate();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    public static OkHttpClient getClient() {
        return client;
    }



}

