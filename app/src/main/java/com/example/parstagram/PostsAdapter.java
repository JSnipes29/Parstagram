package com.example.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.fragments.DetailsPostFragment;
import com.parse.ParseFile;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts) {
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvDescription;
        private ImageView ivImage;
        private RelativeLayout rlPost;
        private TextView tvTimestamp;
        private ImageView ivLike;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            rlPost = itemView.findViewById(R.id.rlPost);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivLike = itemView.findViewById(R.id.ivLike);
        }

        public void bind(final Post post) {
            tvDescription.setText(post.getDescription());
            tvName.setText(post.getUser().getUsername());
            tvTimestamp.setText(post.getCreatedAt().toString());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            ivLike.setOnClickListener(v -> {
                Log.i("Adapter", "Clicked");
                post.setLikes(post.getLikes() + 1);
                post.saveInBackground();
                Log.i("Adapter", String.valueOf(post.getLikes()));
            });
            rlPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailPost(post);
                }
            });
        }

        public void detailPost(Post post) {
            Log.i("PostsAdapter", "Clicked post");
            Fragment fragment = new DetailsPostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("postId", post.getObjectId());
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }
}
