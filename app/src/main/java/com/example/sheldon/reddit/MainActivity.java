package com.example.sheldon.reddit;

import android.content.Context;


import android.content.Intent;
import android.os.AsyncTask;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AbsListView;
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
    private String nextPageQuery = "";
    private boolean loading = true;
    private int previousTotalCount = 0;
    private int mPostCount = 25;

    private static final String URL_FORMAT = "https://www.reddit.com%s/.json?count=%d&after=%s";
    private static final int DIVIDER_HEIGHT = 10;
    private static final int POST_INTERVAL = 25;

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
                mPostCount = POST_INTERVAL;
                Log.d("MainActivity", "onItemSelected: Item selected is " + selectedItem);
                if(selectedItem.equals("FRONTPAGE")) {
                    mSubredditPath = "";
                }
                else {
                    mSubredditPath = "/r/" + selectedItem;
                }

                Log.d("MainActivity", "onItemSelected: string format is " + String.format(URL_FORMAT,mSubredditPath,POST_INTERVAL, ""));
                loadPage(String.format(URL_FORMAT,mSubredditPath,POST_INTERVAL, ""));
                previousTotalCount = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mlistView = (ListView)findViewById(R.id.listviewSubreddit);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String permalink = ((Post)adapterView.getItemAtPosition(i)).getmPermalink();
                Bundle bundle = new Bundle();
                bundle.putString("permalink",permalink);

                Fragment commentFragment = new CommentsFragment();
                commentFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.container,commentFragment).addToBackStack(CommentsFragment.class.getSimpleName()).commit();

            }
        });
        mlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("MainActivity", "onScroll: loading is " + loading);
                Log.d("MainActivity", "onScroll: totalItemCount is " + totalItemCount);
                Log.d("MainActivity", "onScroll: previousTotalCount is " + previousTotalCount);
                if(loading) {
                    if(totalItemCount > previousTotalCount) {
                        loading = false;
                        previousTotalCount = totalItemCount;
                    }
                }
                if(!loading && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    updateList(nextPageQuery);
                    loading = true;
                    Log.d("MainActivity", "onScroll: reached end of list");
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layoutSwipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage(String.format(URL_FORMAT,mSubredditPath,POST_INTERVAL, ""));
            }
        });

        initializeSpinner();
        loadPage(String.format(URL_FORMAT,mSubredditPath,POST_INTERVAL, nextPageQuery));
    }

    /**
     * Initialize spinner to change subreddits
     */
    private void initializeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.default_subreddits, R.layout.spinner_item);
        mSpinner.setAdapter(adapter);
    }

    private void updateList(String after) {
        mPostCount += POST_INTERVAL;
        loadPage(String.format(URL_FORMAT,mSubredditPath,POST_INTERVAL, nextPageQuery));
        Log.d("MainActivity", "updateList: query is " + String.format(URL_FORMAT,mSubredditPath,POST_INTERVAL, nextPageQuery));
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
                String query =  JsonParser.getNextPageListing(jsonObject);
                if(query.equals(nextPageQuery)) {
                    return;
                }
                nextPageQuery = query;
               if(mPostCount > POST_INTERVAL) {
                    ArrayList<Post> morePosts = JsonParser.getPosts(jsonObject);
                    mPosts.addAll(morePosts);
                    mAdapter.notifyDataSetChanged();


                }
                else {
                    mPosts = JsonParser.getPosts(jsonObject);
                    mAdapter = new PostsArrayAdapter(mContext, R.layout.list_item_post, mPosts);
                    mlistView.setAdapter(mAdapter);
                }

                mlistView.setDividerHeight(DIVIDER_HEIGHT);


            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

}
