package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // Initializes Parse SDK a soon as application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Uf07k9UJ01WQPLRKO2cXj4oY0giVBM01ByKeB7W0")
                .clientKey("B67BE5RedrZIs250WHD2LYkE7uu2ls6dHAqla9Kk")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
