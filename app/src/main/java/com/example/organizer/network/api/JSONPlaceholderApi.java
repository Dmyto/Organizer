package com.example.organizer.network.api;



import com.example.organizer.network.models.Channel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JSONPlaceholderApi {
    @GET("tech/all/rss.xml")

    Call<Channel> getFeed();
}
