package com.example.organizer.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.organizer.network.NetworkService;
import com.example.organizer.network.models.Channel;


public class NewsListViewModel extends AndroidViewModel {
    private NetworkService networkService;
    private LiveData<Channel> channelLiveData;

    public NewsListViewModel(@NonNull Application application) {
        super(application);
        initialize();
        getChannelLiveData();
    }

    public void initialize() {
        if (networkService == null) {
            networkService = new NetworkService();
            channelLiveData = networkService.getChannelLiveData();
            loadNews();
        }
    }

    public void loadNews() {
        networkService.loadNews();
    }

    public LiveData<Channel> getChannelLiveData() {
        return channelLiveData;
    }
}
