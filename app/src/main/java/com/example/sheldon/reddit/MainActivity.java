package com.example.sheldon.reddit;

import android.content.Context;


import android.os.AsyncTask;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.ListView;

import com.example.sheldon.reddit.Models.Post;
import com.example.sheldon.reddit.Utils.JsonParser;
import com.example.sheldon.reddit.Utils.PostsArrayAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    private Context mContext;
    private ListView mlistView ;
    private ArrayList<Post> mPosts;
    private PostsArrayAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String mJsonUrl = "https://www.reddit.com/.json";
    private static final int DIVIDER_HEIGHT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mlistView = (ListView)findViewById(R.id.listviewSubreddit);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layoutSwipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadPage();
            }
        });

        new MyTask().execute(mJsonUrl);
    }

    /**
     * Acquire json again and update the listview adapter
     */
    private void reloadPage() {
        new MyTask().execute(mJsonUrl);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Parse json in the background and load into listview
     */
    private class MyTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonParser.getJSON(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                mPosts = JsonParser.getPosts(jsonObject);
                mAdapter = new PostsArrayAdapter(mContext, R.layout.list_item_post, mPosts);
                mlistView.setDividerHeight(DIVIDER_HEIGHT);
                mlistView.setAdapter(mAdapter);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

}
