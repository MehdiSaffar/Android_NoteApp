package com.example.mehdisaffar.testproject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.example.mehdisaffar.testproject.data.NoteContract;
import com.example.mehdisaffar.testproject.databinding.ActivityMainBinding;
import com.example.mehdisaffar.testproject.utils.NoteUtils;

public class MainActivity extends AppCompatActivity
        implements NoteListAdapter.NoteListOnClickHandler,
        NoteListAdapter.NoteListOnLongClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String[] NOTE_PROJECTION =
            {
                    NoteContract.NoteEntry._ID,
                    NoteContract.NoteEntry.COLUMN_TITLE,
                    NoteContract.NoteEntry.COLUMN_CONTENT,
                    NoteContract.NoteEntry.COLUMN_DATE_CREATED,
                    NoteContract.NoteEntry.COLUMN_DATE_LAST_MODIFIED
            };

    public static final int INDEX_NOTE_ID = 0;
    public static final int INDEX_NOTE_TITLE = 1;
    public static final int INDEX_NOTE_CONTENT = 2;
    public static final int INDEX_NOTE_DATE_CREATED = 3;
    public static final int INDEX_NOTE_DATE_LAST_MODIFIED = 4;

    private static final int ID_NOTE_LIST_LOADER = 100;

    ActivityMainBinding mBinding;
    private NoteListAdapter mNoteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportLoaderManager().initLoader(ID_NOTE_LIST_LOADER, null, this);
        //createFakeData();
        setupList();
    }

    private void setupList() {
        mNoteListAdapter = new NoteListAdapter(this, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.listRecyclerView.setLayoutManager(layoutManager);
        mBinding.listRecyclerView.setAdapter(mNoteListAdapter);
    }

    private void createFakeData() {
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteContract.NoteEntry.COLUMN_TITLE, "Title");
        contentValues.put(NoteContract.NoteEntry.COLUMN_CONTENT, "Contenthjfakjlhfasldjhfjkdshfkjdshfjsdfkbsdlkfbdjldhfdljfgbdskjlhgdskjlfvdshbjkdgfkjldsfhgifkjbdkvhbviljdkfjkdbjfgdbkfljldhfkljsd");
        contentValues.put(NoteContract.NoteEntry.COLUMN_DATE_CREATED, System.currentTimeMillis());
        contentResolver.insert(NoteContract.NoteEntry.CONTENT_URI, contentValues);
    }

    @Override
    public void onClick(long noteId) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.setData(NoteContract.NoteEntry.buildNoteUriWithId(noteId));
        startActivity(intent);
    }

    @Override
    public void onLongClick(final long noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setIcon(R.drawable.ic_toc_black_24dp)
                .setItems(R.array.list_item_note_long_click_actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int clickedIndex) {
                        switch (clickedIndex) {
                            case 0:
                                deleteNote(noteId);
                                dialogInterface.dismiss();
                                break;
                            case 1:
                                long createdTimestamp = mNoteListAdapter.getData().getLong(INDEX_NOTE_DATE_CREATED);
                                long lastModifiedTimestamp = mNoteListAdapter.getData().getLong(INDEX_NOTE_DATE_LAST_MODIFIED);
                                dialogInterface.dismiss();
                                NoteUtils.createInfoDialog(MainActivity.this, createdTimestamp, lastModifiedTimestamp).show();
                            default:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                }).show();
    }

    private void deleteNote(long noteId) {
        getContentResolver().delete(NoteContract.NoteEntry.buildNoteUriWithId(noteId), null, null);
    }

    public void onAddNoteButtonClick(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteContract.NoteEntry.COLUMN_TITLE, "");
        contentValues.put(NoteContract.NoteEntry.COLUMN_CONTENT, "");
        contentValues.put(NoteContract.NoteEntry.COLUMN_DATE_CREATED, System.currentTimeMillis());
        Uri insertedNoteUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, contentValues);
        Intent intent = new Intent(this, NoteActivity.class);
        intent.setData(insertedNoteUri);
        startActivity(intent);
    }

    @Override
    public CursorLoader onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case ID_NOTE_LIST_LOADER:
                return new CursorLoader(this, NoteContract.NoteEntry.CONTENT_URI, NOTE_PROJECTION, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNoteListAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNoteListAdapter.setData(null);
    }


}
