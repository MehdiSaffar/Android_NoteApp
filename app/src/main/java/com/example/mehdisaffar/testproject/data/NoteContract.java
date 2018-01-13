package com.example.mehdisaffar.testproject.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mehdi Saffar on 1/12/2018.
 */

public final class NoteContract {
    public static final String AUTHORITY = "com.example.mehdisaffar.testproject";
    public static final Uri CONTENT_URI = new Uri.Builder().authority(AUTHORITY).scheme("content").build();

    public static final class NoteEntry implements BaseColumns {
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = NoteContract.CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String TABLE_NAME = "note";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_DATE_CREATED = "dateCreated";
        public static final String COLUMN_DATE_LAST_MODIFIED = "dateLastModified";


        public static Uri buildNoteUriWithId(long noteId) {
            return ContentUris.withAppendedId(CONTENT_URI, noteId);
        }
    }
}
