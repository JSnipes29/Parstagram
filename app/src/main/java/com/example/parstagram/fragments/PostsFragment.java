package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.OnBackPressed;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentComposeBinding;
import com.example.parstagram.databinding.FragmentPostsBinding;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class PostsFragment extends BaseFragment {

    FragmentPostsBinding binding;
    private EndlessRecyclerViewScrollListener scrollListener;
    public static final String TAG = "PostsFragment";
    private static final int LIMIT = 20;
    private PostsAdapter adapter;
    private List<Post> posts;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), posts);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        binding.rvPosts.addOnScrollListener(scrollListener);
        binding.swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchPostsAsync(0);
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        queryPosts();

    }

    private void loadNextDataFromApi(int page) {
        Log.i(TAG, "Endless Scrolling");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereLessThan(Post.KEY_CREATED_AT, posts.get(posts.size() - 1).getCreatedAt());
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Couldn't get post", e);
                return;
            }
            for (Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
            }
            this.posts.addAll(posts);
            adapter.notifyDataSetChanged();
            scrollListener.resetState();
        });

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Couldn't get post", e);
                return;
            }
            for (Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
            }
            this.posts.addAll(posts);
            adapter.notifyDataSetChanged();
            scrollListener.resetState();
        });
    }

    public void fetchPostsAsync(int page) {
        Log.i(TAG, "Pull to refresh");
        // Send the network request to fetch the updated data
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Couldn't get post", e);
                return;
            }
            for (Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
            }
            this.posts.clear();
            this.posts.addAll(posts);
            Log.i(TAG, "Size: " + this.posts.size());
            adapter.notifyDataSetChanged();
            binding.swipeContainer.setRefreshing(false);
            scrollListener.resetState();
        });
    }

}