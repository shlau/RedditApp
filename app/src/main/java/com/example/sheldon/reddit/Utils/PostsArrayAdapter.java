package com.example.sheldon.reddit.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sheldon.reddit.Models.Post;
import com.example.sheldon.reddit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;

/**
 * Created by sheldon on 9/16/2017.
 * Custom arrayadapter representing a subreddit page post structure
 */

public class PostsArrayAdapter extends ArrayAdapter<Post>{

    private Context mContext;
    private ArrayList<Post> mPosts;

    public PostsArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Post> posts) {
        super(context, resource, posts);
        mContext = context;
        mPosts = posts;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_post, parent, false);

        TextView author =  rowView.findViewById(R.id.textPostAuthor);
        author.setText(mPosts.get(position).getmAuthor());
        TextView score =  rowView.findViewById(R.id.textPostScore);
        score.setText(String.valueOf(mPosts.get(position).getmScore()));
        TextView subreddit =  rowView.findViewById(R.id.textPostSR);
        subreddit.setText(mPosts.get(position).getmSubredit());
        TextView title =  rowView.findViewById(R.id.postTextTitle);
        title.setText(mPosts.get(position).getmTitle());
        TextView comments =  rowView.findViewById(R.id.textPostComments);
        comments.setText(String.valueOf(mPosts.get(position).getmNumComments()));
        TextView domain =  rowView.findViewById(R.id.textPostDomain);
        domain.setText(mPosts.get(position).getmDomain());
        TextView time =  rowView.findViewById(R.id.postTextTime);

        // get epoch of post and convert to time passed
        String utc = mPosts.get(position).getmCreatedUtc();
        time.setText(TimeConverter.timePassed((Double.valueOf(utc)).longValue()));

        ImageView thumbnail = rowView.findViewById(R.id.postImageThumbnail);
        Log.d(TAG, "getView: title is " + mPosts.get(position).getmTitle());
        String thumbnailLink = mPosts.get(position).getmThumbnail();
        Log.d(TAG, "getView: thumbnailLink is " + thumbnailLink);
        // hide imageview if there is no thumbnail image
        if(!thumbnailLink.equals("self") && !thumbnailLink.equals("default") && !thumbnailLink.equals("")) {
            Picasso.with(mContext).load(mPosts.get(position).getmThumbnail()).into(thumbnail);
        }
        else {
            thumbnail.setVisibility(View.GONE);
        }

        return rowView;
    }
}
