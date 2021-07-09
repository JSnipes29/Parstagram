package com.example.parstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.fragments.BaseFragment;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.DetailsPostFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int GALLERY_REQUEST = 14;
    ActivityMainBinding binding;
    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Log.i(TAG, "Action home pressed");
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", Parcels.wrap(ParseUser.getCurrentUser()));
                        fragment.setArguments(bundle);
                        break;
                    default:
                        fragment = new PostsFragment();
                        break;

                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // Compress image to lower quality scale 1 - 100
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] image = stream.toByteArray();


                        ParseUser user = ParseUser.getCurrentUser();
                        ParseFile parseFile = new ParseFile(user.getUsername() + "_pic.jpeg", image);
                        ParseUser.getCurrentUser().put("profileImage", parseFile);
                        ParseUser.getCurrentUser().saveInBackground();
                        Toast.makeText(this, "You have updated your profile image", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Problem with uploading profile image",e);
                    }
                    break;
            }
    }
    }

    public void logout(MenuItem mi) {
        ParseUser.logOut();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        String f = tellFragments();
        if (f.equals(DetailsPostFragment.class.getSimpleName())) {
            return;
        }
        super.onBackPressed();
    }

    public String tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f != null && f instanceof BaseFragment) {
                Log.i(TAG, f.getClass().getSimpleName());
                ((BaseFragment) f).onBackPressed();
                return f.getClass().getSimpleName();
            }
        }
        return "";
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    public void changeProfileImage(MenuItem mi) {
        Log.i(TAG, "Clicked profile image menu item");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        Log.i(TAG, "activity started");
    }



}