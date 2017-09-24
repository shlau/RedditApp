package com.example.sheldon.reddit.Utils;

import android.util.Log;

import com.example.sheldon.reddit.Models.Comment;
import com.example.sheldon.reddit.Models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by sheldon on 9/15/2017.
 * Functions that reads and parses json from a url into Post models
 */

public class JsonParser {
    public JsonParser() {
    }

    /**
     * Puts json into a JSONobject
     * @param url The url address we want to read json from
     * @return a JSONobject representing the json from the url
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject getJSONObject(String url) throws IOException, JSONException {
        return new JSONObject(IOUtils.toString( new URL(url), Charset.forName("UTF-8")));
    }

    public static JSONArray getJSONArray(String url) throws IOException, JSONException {
        return new JSONArray(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
    }
    /**
     * Parses json to store corresponding data into an arraylist of Post models
     * @param json the JSONobject containing the data
     * @return  an arraylist of posts
     * @throws JSONException
     */
    public static ArrayList<Post> getPosts(JSONObject json) throws JSONException {
        JSONObject data = json.getJSONObject("data");
        JSONArray children = data.getJSONArray("children");

        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0; i < children.length(); i++) {
            JSONObject cur = children.getJSONObject(i)
                    .getJSONObject("data");

            Post post = new Post();
            post.setmPermalink(cur.optString("permalink"));
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

    public static ArrayList<Comment> getComments(JSONObject json) throws JSONException {
        ArrayList<Comment> commentsList = new ArrayList<>();
        ArrayList<Comment> replies = null;
        JSONObject data = json.getJSONObject("data");
        JSONArray children = data.getJSONArray("children");

        Log.d(TAG, "getComments: length of children "  + children.length());
        for (int i = 0; i < children.length(); i++) {
            Comment comment = new Comment();

            JSONObject cur = children.getJSONObject(i)
                    .getJSONObject("data");
            if(!cur.optString("replies").equals("")) {
                JSONObject repliesObject = cur.getJSONObject("replies");
                replies = getComments(repliesObject);
            }
            String author = cur.optString("author");
            String created_utc = cur.optString("created_utc");
            int score = Integer.valueOf(cur.optString("score"));
            boolean score_hidden = Boolean.valueOf(cur.optString("score_hidden"));
            String body = cur.optString("body");

            comment.setReplies(replies);
            comment.setAuthor(author);
            Log.d(TAG, "getComments: Author is "  + author);
            comment.setCreated_utc(created_utc);
            comment.setScore(score);
            comment.setBody(body);

            commentsList.add(comment);
        }
        return commentsList;
    }
    public static String getNextPageListing(JSONObject json) throws JSONException {
        JSONObject data = json.getJSONObject("data");
        String after = data.optString("after");
        Log.d(TAG, "getNextPageListing: after is " + after);
        return after;
    }
}
