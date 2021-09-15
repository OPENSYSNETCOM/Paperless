package kr.co.opensysnet.paperless.server;

import java.util.concurrent.TimeUnit;

import kr.co.opensysnet.paperless.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static RetrofitService retrofitInstance = new RetrofitService();

    public static RetrofitService getInstance() {
        return retrofitInstance;
    }

    private Retrofit retrofit;

    private RetrofitAPI service;

    private RetrofitService() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }

        client.connectTimeout(40, TimeUnit.SECONDS);
        client.readTimeout(40, TimeUnit.SECONDS);
        client.writeTimeout(40, TimeUnit.SECONDS);
        client.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://141.164.57.38:8080" + "/paperless/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        service = retrofit.create(RetrofitAPI.class);
    }

    public RetrofitAPI getService() {
        return service;
    }

    public void setBaseUrl(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(RetrofitAPI.class);
    }
}
