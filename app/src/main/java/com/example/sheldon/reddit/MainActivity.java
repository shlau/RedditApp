package com.example.sheldon.reddit;

import android.content.Intent;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sheldon.reddit.Utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MyTask().execute("https://www.reddit.com/r/AskReddit/top.json?sort=top&t=all");
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
            try{
                JSONObject data= jsonObject.getJSONObject("data");
                JSONArray children=data.getJSONArray("children");

                //Using this property we can fetch the next set of
                //posts from the same subreddit

                for(int i=0;i<children.length();i++){
                    JSONObject cur=children.getJSONObject(i)
                            .getJSONObject("data");
                    Log.d(TAG, "onPostExecute: " +cur.optString("title"));

                }
            }catch(Exception e){
                Log.e("fetchPosts()",e.toString());
            }
//            Log.d(TAG, "onPostExecute: " + jsonObject.toString());
        }
    }

}
