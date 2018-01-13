package com.example.mehdisaffar.testproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mehdi Saffar on 1/12/2018.
 */

public class NoteDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_FILE_NAME = "notes.db";
    private static final String LOG_TAG = NoteDbHelper.class.getSimpleName();

    private static final String SQL_NOTE_TABLE_CREATE_V1 =
            "CREATE TABLE note ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title TEXT NOT NULL,"
                    + "content TEXT NOT NULL,"
                    + "timestamp REAL NOT NULL" // timestamp is called 'dateCreated' in V2
                    + ");";
    private static final String SQL_NOTE_TABLE_CREATE_V2 =
            "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + "("
                    + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NoteContract.NoteEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                    + NoteContract.NoteEntry.COLUMN_CONTENT + " TEXT NOT NULL,"
                    + NoteContract.NoteEntry.COLUMN_DATE_CREATED + " REAL NOT NULL,"
                    + NoteContract.NoteEntry.COLUMN_DATE_LAST_MODIFIED + " REAL"
                    + ");";

    public NoteDbHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    private static void createDatabase(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_NOTE_TABLE_CREATE_V2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "onUpgrade: Found upgrade of database " + DATABASE_FILE_NAME + " from " + oldVersion + " to " + newVersion);
        switch (oldVersion) {
            case 1:
                Log.d(LOG_TAG, "onUpgrade: Upgrading database " + DATABASE_FILE_NAME + " from " + oldVersion + " to " + oldVersion + 1);
                upgradeDatabaseFrom1To2(sqLiteDatabase);
                Log.d(LOG_TAG, "onUpgrade: Success");
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown oldVersion: " + oldVersion);
        }
    }

    private void upgradeDatabaseFrom1To2(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL("ALTER TABLE note RENAME TO tmp_note;");
        sqLiteDatabase.execSQL(SQL_NOTE_TABLE_CREATE_V2);
        sqLiteDatabase.execSQL(
                "INSERT INTO note (_id, title, content, dateCreated) " +
                        "SELECT _id, title, content, timestamp " + // timestamp is called 'dateCreated' in V2
                        "FROM tmp_note;");
        sqLiteDatabase.execSQL("UPDATE note SET dateLastModified=dateCreated");
        sqLiteDatabase.execSQL("DROP TABLE tmp_note;");
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }
}
