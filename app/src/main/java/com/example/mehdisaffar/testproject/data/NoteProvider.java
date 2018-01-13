package com.example.mehdisaffar.testproject.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Mehdi Saffar on 1/12/2018.
 */

public class NoteProvider extends ContentProvider {
    private static final int CODE_ALL_NOTES = 1000;
    private static final int CODE_NOTE_WITH_ID = 1001;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private Context mContext;
    private ContentResolver mContentResolver;
    private NoteDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(NoteContract.AUTHORITY, NoteContract.NoteEntry.PATH, CODE_ALL_NOTES);
        uriMatcher.addURI(NoteContract.AUTHORITY, NoteContract.NoteEntry.PATH + "/#", CODE_NOTE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext(); // We get the context so that we don't have to call getContext() every time
        mContentResolver = mContext.getContentResolver(); // Same thing for the content resolver
        mDbHelper = new NoteDbHelper(mContext); // We create the database helper
        return true; // We say that everything was successful
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String orderBy) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_ALL_NOTES: {
                Cursor cursor = queryAllNotes(columns, selection, selectionArgs, orderBy);
                cursor.setNotificationUri(mContext.getContentResolver(), uri);
                return cursor;
            }
            case CODE_NOTE_WITH_ID: {
                String idStr = uri.getLastPathSegment();
                int id = Integer.parseInt(idStr);
                Cursor cursor = queryNoteWithId(id, columns);
                cursor.setNotificationUri(mContext.getContentResolver(), uri);
                return cursor;
            }
            default:
                throw new UnsupportedOperationException("The uri '" + uri.toString() + "' is not a valid uri.");
        }
    }

    @NonNull
    private Cursor queryAllNotes(@Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.query(NoteContract.NoteEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy);
        if (cursor == null) {
            throw new RuntimeException("The database " + database.getPath() + " could not return a cursor.");
        }
        return cursor;
    }

    @NonNull
    private Cursor queryNoteWithId(int noteId, @Nullable String[] columns) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String selection = NoteContract.NoteEntry._ID + "=" + noteId;
        Cursor cursor = database.query(NoteContract.NoteEntry.TABLE_NAME, columns, selection, null, null, null, null);
        if (cursor == null) {
            throw new RuntimeException("The database " + database.getPath() + " could not return a cursor.");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (contentValues == null || contentValues.size() == 0) return null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_ALL_NOTES:
                long id = insertNote(contentValues);
                mContentResolver.notifyChange(uri, null);
                return NoteContract.NoteEntry.buildNoteUriWithId(id);
            default:
                throw new UnsupportedOperationException("The uri '" + uri.toString() + "' is not a valid uri.");
        }
    }

    private long insertNote(@NonNull ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long successfulInsertCount = database.insert(NoteContract.NoteEntry.TABLE_NAME,
                null,
                contentValues);
        if (successfulInsertCount < 0) {
            throw new RuntimeException("The values given could not be inserted to the database.");
        }
        return successfulInsertCount;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case CODE_ALL_NOTES: {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                int successfullyDeletedCount = database.delete(NoteContract.NoteEntry.TABLE_NAME, null, null);
                if (successfullyDeletedCount == -1) {
                    throw new RuntimeException("All the notes could not be deleted.");
                }
                mContentResolver.notifyChange(uri, null);
                return successfullyDeletedCount;
            }
            case CODE_NOTE_WITH_ID: {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                int id = Integer.parseInt(uri.getLastPathSegment());
                int successfullyDeletedCount = database.delete(NoteContract.NoteEntry.TABLE_NAME,
                        NoteContract.NoteEntry._ID + "=" + id, null);
                if (successfullyDeletedCount < 0) {
                    throw new RuntimeException("Could not delete note " + id);
                }
                mContentResolver.notifyChange(uri, null);
                return successfullyDeletedCount;
            }
            default:
                throw new UnsupportedOperationException("The uri '" + uri.toString() + "' is not a valid uri.");
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        switch (sUriMatcher.match(uri)) {
            case CODE_NOTE_WITH_ID: {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                int id = Integer.parseInt(uri.getLastPathSegment());
                int successfullyUpdatedCount = database.update(NoteContract.NoteEntry.TABLE_NAME,
                        contentValues,
                        NoteContract.NoteEntry._ID + "=" + id,
                        null);
                if (successfullyUpdatedCount < 0) {
                    throw new RuntimeException("The note " + id + " could not be updated.");
                }
                mContentResolver.notifyChange(uri, null);
                return successfullyUpdatedCount;
            }
            default:
                throw new UnsupportedOperationException("The uri '" + uri.toString() + "' is not a valid uri.");
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_ALL_NOTES:
                database.beginTransaction();
                int successfullyInsertedCount = 0;
                for (ContentValues contentValue : contentValues) {
                    long id = database.insert(NoteContract.NoteEntry.TABLE_NAME,
                            null,
                            contentValue);
                    if (id != -1) {
                        successfullyInsertedCount++;
                    }
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                if (successfullyInsertedCount > 0) {
                    mContentResolver.notifyChange(uri, null);
                }
                return successfullyInsertedCount;
            default:
                return super.bulkInsert(uri, contentValues);
        }
    }
}
