package com.example.mehdisaffar.testproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mehdisaffar.testproject.data.NoteContract;
import com.example.mehdisaffar.testproject.databinding.ActivityNoteBinding;
import com.example.mehdisaffar.testproject.utils.NoteUtils;

public class NoteActivity extends AppCompatActivity {
    public static final String[] NOTE_PROJECTION = new String[]{
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
    private Uri mContentUri;
    private Cursor mData;
    private ActivityNoteBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_note);

        Intent receivedIntent = getIntent();
        mContentUri = receivedIntent.getData();

        fetchData();
        mData.moveToFirst();
        mBinding.detailNoteTitle.setText(mData.getString(INDEX_NOTE_TITLE));
        mBinding.detailNoteContent.setText(mData.getString(INDEX_NOTE_CONTENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_note_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm delete")
                        .setMessage("Are you sure you want to delete the note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onActivityExit(ExitReason.DeletedNote);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            case R.id.menu_activity_note_info:
                long createdTimestamp = mData.getLong(INDEX_NOTE_DATE_CREATED);
                long modifiedTimestamp = mData.getLong(INDEX_NOTE_DATE_LAST_MODIFIED);
                // TODO: put these date format in a DateFormatUtils class
                NoteUtils.createInfoDialog(this, createdTimestamp, modifiedTimestamp).show();
            case android.R.id.home:
                onActivityExit(ExitReason.UsedPressedBack);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onActivityExit(ExitReason exitReason) {
        switch (exitReason) {
            case UsedPressedBack:
                saveNote();
                // Android automatically exits the user
                break;
            case ActivityDestroyed:
                saveNote();
                break;
            case DeletedNote:
                deleteNote();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onActivityExit(ExitReason.ActivityDestroyed);
    }

    private void fetchData() {
        mData = getContentResolver().query(mContentUri, NOTE_PROJECTION, null, null, null);
    }

    private void deleteNote() {
        getContentResolver().delete(mContentUri, null, null);
    }

    private void saveNote() {
        ContentValues values = new ContentValues();
        values.put(NOTE_PROJECTION[INDEX_NOTE_TITLE], mBinding.detailNoteTitle.getText().toString());
        values.put(NOTE_PROJECTION[INDEX_NOTE_CONTENT], mBinding.detailNoteContent.getText().toString());
        values.put(NOTE_PROJECTION[INDEX_NOTE_DATE_LAST_MODIFIED], System.currentTimeMillis());
        getContentResolver().update(mContentUri, values, null, null);
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
    }

    private enum ExitReason {
        None,
        UsedPressedBack,
        ActivityDestroyed,
        DeletedNote
    }
}
