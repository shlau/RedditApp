package com.example.sheldon.reddit;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sheldon.reddit.Models.Post;
import com.example.sheldon.reddit.Utils.JsonParser;
import com.example.sheldon.reddit.Utils.PostsArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ListView mlistView ;
    private ArrayList<Post> posts;
    private static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mlistView = (ListView)findViewById(R.id.listviewSubreddit);
        if(mlistView == null) {
            Log.d(TAG, "onCreate: listview null before execute");
        }
        new MyTask().execute("https://www.reddit.com/top/.json?sort=top&t=all");
    }

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
                posts = JsonParser.getPosts(jsonObject);
                PostsArrayAdapter adapter = new PostsArrayAdapter(mContext, R.layout.list_item_post, posts);
                mlistView.setAdapter(adapter);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

}
