package com.example.organizer.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.organizer.network.api.JSONPlaceholderApi;
import com.example.organizer.network.models.Channel;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private MutableLiveData<Channel> channelLiveData;

    private static final String BASE_URL = "https://www.liga.net/";
    private Retrofit mRetrofit;
    private JSONPlaceholderApi jsonPlaceholderApi;

    public NetworkService() {
        channelLiveData = new MutableLiveData<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        jsonPlaceholderApi = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(JSONPlaceholderApi.class);
    }

    public void loadNews() {
        jsonPlaceholderApi.
                getFeed().
                enqueue(new Callback<Channel>() {
                    @Override
                    public void onResponse(Call<Channel> call, Response<Channel> response) {
                        if (response.body() != null) {
                            channelLiveData.postValue(response.body());
                            Log.d("GOOD", "onResponse");

                        }
                    }

                    @Override
                    public void onFailure(Call<Channel> call, Throwable t) {
                        Log.d("ERROR", "NETWORK_FAILURE");
                        channelLiveData.postValue(null);
                        t.printStackTrace();
                    }
                });
    }

    public LiveData<Channel> getChannelLiveData() {
        return channelLiveData;
    }
}
