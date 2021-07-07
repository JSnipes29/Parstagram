package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.parstagram.OnBackPressed;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentDetailsPostBinding;
import com.example.parstagram.databinding.FragmentPostsBinding;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsPostFragment extends BaseFragment implements OnBackPressed {

    public static final String TAG = "DetailsPostFragment";
    FragmentDetailsPostBinding binding;
    Post post;
    String id;

    public DetailsPostFragment() {
        // Required empty public constructor
    }


    public static DetailsPostFragment newInstance(Post post) {
        DetailsPostFragment fragment = new DetailsPostFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", Parcels.wrap(post));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("postId");
        queryPosts(id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsPostBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void queryPosts(String id) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_ID, id);
        query.setLimit(1);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Couldn't get post", e);
                return;
            }
            for (Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
            }
            post = posts.get(0);
            binding.tvName.setText(post.getUser().getUsername());
            binding.tvDescription.setText(post.getDescription());
            binding.tvTimestamp.setText(post.getCreatedAt().toString());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(getContext()).load(image.getUrl()).into(binding.ivImage);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new PostsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}