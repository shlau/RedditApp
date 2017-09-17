package com.example.sheldon.reddit.Utils;

import android.text.format.DateUtils;

import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sheldon on 9/16/2017.
 */

public class TimeConverter {
    public static String timePassed(long timeStamp) {
//        long currentTime = System.currentTimeMillis();
        long pastTime = timeStamp * 1000;
        PrettyTime p = new PrettyTime();
        String relativeTime = p.format(new Date(pastTime));
        return relativeTime.substring(0, relativeTime.lastIndexOf("a"));
    }
}
