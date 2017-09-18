package com.example.sheldon.reddit.Models;

/**
 * Created by sheldon on 9/15/2017.
 * A model that represents a subreddit post
 */

public class Post {
    private int mScore;
    private String mTitle;
    private int mNumComments;
    private String mDomain;
    private String mSubredit;
    private boolean mClicked;
    private boolean mOverEighteen;
    private String mThumbnail;
    private String mAuthor;
    private String mCreatedUtc;
    private String mPermalink;

    public Post(int mScore, String mTitle, int mNumComments, String mDomain, String mSubreddit, boolean mClicked, boolean mOverEighteen, String mThumbnail, String mAuthor, String mCreatedUtc, String mPermalink) {
        this.mScore = mScore;
        this.mTitle = mTitle;
        this.mNumComments = mNumComments;
        this.mDomain = mDomain;
        this.mSubredit = mSubreddit;
        this.mClicked = mClicked;
        this.mOverEighteen = mOverEighteen;
        this.mThumbnail = mThumbnail;
        this.mAuthor = mAuthor;
        this.mCreatedUtc = mCreatedUtc;
        this.mPermalink = mPermalink;
    }

    @Override
    public String toString() {
        return "Post{" +
                "mScore=" + mScore +
                ", mTitle='" + mTitle + '\'' +
                ", mNumComments=" + mNumComments +
                ", mDomain='" + mDomain + '\'' +
                ", mSubredit='" + mSubredit + '\'' +
                ", mClicked=" + mClicked +
                ", mOverEighteen=" + mOverEighteen +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mCreatedUtc='" + mCreatedUtc + '\'' +
                '}';
    }

    public Post() {
    }

    public String getmPermalink() {
        return mPermalink;
    }

    public void setmPermalink(String mPermalink) {
        this.mPermalink = mPermalink;
    }
    public int getmScore() {
        return mScore;
    }

    public void setmScore(int mScore) {
        this.mScore = mScore;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmNumComments() {
        return mNumComments;
    }

    public void setmNumComments(int mNumComments) {
        this.mNumComments = mNumComments;
    }

    public String getmDomain() {
        return mDomain;
    }

    public void setmDomain(String mDomain) {
        this.mDomain = mDomain;
    }

    public String getmSubredit() {
        return mSubredit;
    }

    public void setmSubredit(String mSubredit) {
        this.mSubredit = mSubredit;
    }

    public boolean ismClicked() {
        return mClicked;
    }

    public void setmClicked(boolean mClicked) {
        this.mClicked = mClicked;
    }

    public boolean ismOverEighteen() {
        return mOverEighteen;
    }

    public void setmOverEighteen(boolean mOverEighteen) {
        this.mOverEighteen = mOverEighteen;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmCreatedUtc() {
        return mCreatedUtc;
    }

    public void setmCreatedUtc(String mCreatedUtc) {
        this.mCreatedUtc = mCreatedUtc;
    }
}
