package com.example.sheldon.reddit.Utils;

import android.text.format.DateUtils;

import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;

/**
 * Created by sheldon on 9/16/2017.
 * Uses PrettyTime to format an epoch into time passed
 */

public class TimeConverter {
    public static String timePassed(long timeStamp) {
        long pastTime = timeStamp * 1000;
        PrettyTime p = new PrettyTime();
        String relativeTime = p.format(new Date(pastTime));

        // get rid of "ago"
        return relativeTime.substring(0, relativeTime.lastIndexOf("a"));
    }
}
