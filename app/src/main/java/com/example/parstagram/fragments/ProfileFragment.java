package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.parstagram.CommentAdapter;
import com.example.parstagram.ImageAdapter;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentDetailsPostBinding;
import com.example.parstagram.databinding.FragmentProfileBinding;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class ProfileFragment extends BaseFragment {

    ParseUser user;
    FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";
    public static final int LIMIT = 20;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (ParseUser) Parcels.unwrap(getArguments().getParcelable("user"));
        Log.i(TAG, user.getUsername());
        queryPosts(user);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        binding.tvName.setText(user.getUsername());

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void queryPosts(ParseUser user) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
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
            ImageAdapter adapter = new ImageAdapter(getContext(), posts);
            binding.rvPosts.setAdapter(adapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            binding.rvPosts.setLayoutManager(gridLayoutManager);
            ParseFile profileImage = user.getParseFile("profileImage");
            Log.i(TAG, profileImage.getUrl());
            if (profileImage != null) {
                Log.i(TAG, "Loading image");
                Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(binding.ivProfileImage);
            } else {
                Log.i(TAG, "image is null");
            }
        });
    }
}