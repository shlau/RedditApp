package com.example.sheldon.reddit.Utils;

import com.example.sheldon.reddit.Models.Post;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

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

    public static Post getPosts(JSONObject json) {
        Post post = new Post();
        return post;
    }
}
