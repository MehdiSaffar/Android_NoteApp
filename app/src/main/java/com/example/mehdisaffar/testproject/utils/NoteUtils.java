package com.example.mehdisaffar.testproject.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;

import com.example.mehdisaffar.testproject.R;

/**
 * Created by Mehdi Saffar on 1/13/2018.
 */

public class NoteUtils {
    public static AlertDialog createInfoDialog(Context context, long createdTimestamp, long modifiedTimestamp) {
        int dateFlags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_NO_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_SHOW_TIME;
        String formattedCreatedDate = DateUtils.formatDateTime(context, createdTimestamp, dateFlags);
        String formattedLastModifiedDate = DateUtils.formatDateTime(context, modifiedTimestamp, dateFlags);
        return new AlertDialog.Builder(context)
                .setTitle("Note info")
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setMessage("Date created: " + formattedCreatedDate + "\n" +
                        "Date last modified: " + formattedLastModifiedDate)
                .create();
    }

    public static String getShortContent(Context context, String content) {
        Resources resources = context.getResources();
        int maxLength = resources.getInteger(R.integer.note_short_content_max_length);
        int strLength = content.length();
        String ellipsis = "...";
        if (strLength > maxLength) {
            int lengthToTake = maxLength - ellipsis.length();
            return content.substring(0, lengthToTake - 1) + ellipsis;
        }
        return content;
    }
}
