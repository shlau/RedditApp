package com.example.sheldon.reddit;

import android.content.Context;


import android.os.AsyncTask;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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
    private Spinner mSpinner;
    private PostsArrayAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mSubredditPath = "";
    private static final String URL_BASE = "https://www.reddit.com/";
    private static final int DIVIDER_HEIGHT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mSpinner = (Spinner) findViewById(R.id.spinner_activity_main);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String)adapterView.getItemAtPosition(i);
                Log.d("MainActivity", "onItemSelected: Item selected is " + selectedItem);
                if(selectedItem.equals("FRONTPAGE")) {
                    mSubredditPath = "";
                }
                else {
                    mSubredditPath = "r/" + selectedItem;
                }
                loadPage(URL_BASE + mSubredditPath + ".json");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mlistView = (ListView)findViewById(R.id.listviewSubreddit);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layoutSwipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage(URL_BASE + mSubredditPath + ".json");
            }
        });

        initializeSpinner();
        new MyTask().execute(URL_BASE + mSubredditPath + ".json");
    }

    private void initializeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.default_subreddits, R.layout.spinner_item);
        mSpinner.setAdapter(adapter);
    }

    /**
     * Acquire json again and update the listview adapter
     */
    private void loadPage(String path) {
        new MyTask().execute(path);
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
