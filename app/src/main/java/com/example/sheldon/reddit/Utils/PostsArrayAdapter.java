package com.example.sheldon.reddit.Utils;

import android.content.Context;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sheldon on 9/16/2017.
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
        TextView author = (TextView) rowView.findViewById(R.id.textPostAuthor);
        author.setText(mPosts.get(position).getmAuthor());
        TextView score = (TextView) rowView.findViewById(R.id.textPostScore);
        score.setText(String.valueOf(mPosts.get(position).getmScore()));
        TextView subreddit = (TextView) rowView.findViewById(R.id.textPostSR);
        subreddit.setText(mPosts.get(position).getmSubredit());
        TextView title = (TextView) rowView.findViewById(R.id.postTextTitle);
        title.setText(mPosts.get(position).getmTitle());
        TextView comments = (TextView) rowView.findViewById(R.id.textPostComments);
        comments.setText(String.valueOf(mPosts.get(position).getmNumComments()));
        TextView domain = (TextView) rowView.findViewById(R.id.textPostDomain);
        domain.setText(mPosts.get(position).getmDomain());
        TextView time = (TextView) rowView.findViewById(R.id.postTextTime);
        String utc = mPosts.get(position).getmCreatedUtc();
        Log.d(TAG, "getView: Time passed is " + (Double.valueOf(utc)).longValue());
        time.setText(TimeConverter.timePassed((Double.valueOf(utc)).longValue()));

        ImageView thumbnail = (ImageView) rowView.findViewById(R.id.postImageThumbnail);
        Picasso.with(mContext).load(mPosts.get(position).getmThumbnail()).into(thumbnail);

        return rowView;
    }
}
