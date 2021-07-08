package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;


@ParseClassName("Post")
public class Post extends ParseObject {

    public Post() {}

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_ID = "objectId";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_LIKES_ARRAY = "likesArray";

    public String getDescription () {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public long getLikes() { return getNumber(KEY_LIKES).longValue(); }

    public void setLikes(long likes) { put(KEY_LIKES,likes); }

    public JSONArray getComments() { return getJSONArray(KEY_COMMENTS);}

    public void setComments(JSONArray arr) { put(KEY_COMMENTS, arr);   }

    public JSONArray getLikesArray() { return getJSONArray(KEY_LIKES_ARRAY);}

    public void setLikesArray(JSONArray arr) {put(KEY_LIKES_ARRAY, arr);}

    public boolean liked() {
        JSONArray likesArr = getLikesArray();
        for(int i = 0; i < likesArr.length(); i++) {
            try {
                if (likesArr.getJSONObject(i).getString("username").equals(ParseUser.getCurrentUser().getUsername())) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
