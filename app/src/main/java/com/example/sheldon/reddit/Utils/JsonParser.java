package com.example.sheldon.reddit.Utils;

import android.util.Log;

import com.example.sheldon.reddit.Models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by sheldon on 9/15/2017.
 */

public class JsonParser {
    public JsonParser() {
    }

    public void test(){}
    public static JSONObject getJSON(String url) throws IOException, JSONException {
        return new JSONObject(IOUtils.toString( new URL(url), Charset.forName("UTF-8")));
    }

    public static ArrayList<Post> getPosts(JSONObject json) throws JSONException {
        JSONObject data = json.getJSONObject("data");
        JSONArray children = data.getJSONArray("children");

        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0; i < children.length(); i++) {
            JSONObject cur = children.getJSONObject(i)
                    .getJSONObject("data");

            Post post = new Post();
            post.setmClicked(Boolean.valueOf(cur.optString("clicked")));
            post.setmAuthor(cur.optString("author"));
            post.setmCreatedUtc(cur.optString("created_utc"));
            post.setmDomain(cur.optString("domain"));
            post.setmNumComments(Integer.valueOf(cur.optString("num_comments")));
            post.setmOverEighteen(Boolean.valueOf(cur.optString("over_18")));
            post.setmScore(Integer.valueOf(cur.optString("score")));
            Log.d(TAG, "getPosts: score being set to "  + Integer.valueOf(cur.optString("score")));
            post.setmSubredit(cur.optString("subreddit"));
            post.setmThumbnail(cur.optString("thumbnail"));
            post.setmTitle(cur.optString("title"));

            posts.add(post);
        }
        return posts;
    }
}
