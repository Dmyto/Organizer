package com.example.organizer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizer.R;
import com.example.organizer.network.models.Post;

public class NewsListFragment extends Fragment {
    private NewsListViewModel viewModel;
    private NewsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NewsListAdapter();

        viewModel = ViewModelProviders.of(this).get(NewsListViewModel.class);
        viewModel.getChannelLiveData().observe(this, channel -> {
            adapter.setResults(channel.getPosts());
            adapter.setTitle(channel.getTitle());
        });

        adapter.setOnItemClickListener(new NewsListAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post post = adapter.getPostAtPosition(position);

                Navigation.findNavController(v).navigate(NewsListFragmentDirections.actionNewsListToWebFragment(post.getLink()));
            }
        });

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_fragment_news_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation()));

        return view;
    }
}
