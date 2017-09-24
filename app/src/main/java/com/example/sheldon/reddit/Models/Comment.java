package com.example.sheldon.reddit.Models;

import java.util.ArrayList;

/**
 * Created by Sheldon on 9/24/2017.
 */

public class Comment {
    private String author;
    private String created_utc;
    private int score;
    private boolean score_hidden;
    private String body;
    private ArrayList<Comment> replies;

    public Comment() {
    }

    public Comment(String author, String created_utc, int score, boolean score_hidden, String body, ArrayList<Comment> replies) {
        this.author = author;
        this.created_utc = created_utc;
        this.score = score;
        this.score_hidden = score_hidden;
        this.body = body;
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", created_utc='" + created_utc + '\'' +
                ", score=" + score +
                ", score_hidden=" + score_hidden +
                ", body='" + body + '\'' +
                ", replies=" + replies +
                '}';
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(String created_utc) {
        this.created_utc = created_utc;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isScore_hidden() {
        return score_hidden;
    }

    public void setScore_hidden(boolean score_hidden) {
        this.score_hidden = score_hidden;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
