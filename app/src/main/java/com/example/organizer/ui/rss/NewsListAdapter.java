package com.example.organizer.ui.rss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.organizer.R;
import com.example.organizer.network.models.Post;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String headerTextView;
    private List<Post> results;
    private static ClickListener clickListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_header, parent, false);
            return new HeaderNewsListViewHolder(view);
        }
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            return new ItemNewsListViewHolder(view);

        } else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (results != null) {
            if (holder instanceof HeaderNewsListViewHolder) {
                HeaderNewsListViewHolder headerNewsListViewHolder = (HeaderNewsListViewHolder) holder;
                headerNewsListViewHolder.headerTextView.setText(headerTextView);
            } else if (holder instanceof ItemNewsListViewHolder) {
                ItemNewsListViewHolder itemNewsListViewHolder = (ItemNewsListViewHolder) holder;
                Post post = results.get(position);

                itemNewsListViewHolder.titletextView.setText(post.getTitle());

                if (post.getImageUrl() != null) {
                    String imageUrl = post.getImageUrl().getImageUrl()
                            .replace("http://", "https://");

                    Glide.with(holder.itemView)
                            .load(imageUrl)
                            .into(itemNewsListViewHolder.imageImageView);
                }
            }


        } else {
            return;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void setResults(List<Post> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void setTitle(String title) {
        this.headerTextView = title;
    }

    @Override
    public int getItemCount() {
        if (results != null) {
            return results.size();
        } else {
            return 0;
        }

    }

    public Post getPostAtPosition(int position) {
        return results.get(position);
    }

    class HeaderNewsListViewHolder extends RecyclerView.ViewHolder {

        private TextView headerTextView;

        public HeaderNewsListViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.header_news_list);
        }
    }

    class ItemNewsListViewHolder extends RecyclerView.ViewHolder {

        private TextView titletextView;
        private ImageView imageImageView;


        public ItemNewsListViewHolder(@NonNull View itemView) {
            super(itemView);


            titletextView = itemView.findViewById(R.id.news_item_title);
            imageImageView = itemView.findViewById(R.id.image_item_news);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        NewsListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }
}
