package com.example.mehdisaffar.testproject;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mehdisaffar.testproject.databinding.ListItemNoteBinding;

/**
 * Created by Mehdi Saffar on 1/12/2018.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder> {

    private final Context mContext;
    private Cursor mData;
    private NoteListOnClickHandler mOnClickHandler;
    private NoteListOnLongClickHandler mOnLongClickHandler;

    public NoteListAdapter(@NonNull Context context, NoteListOnClickHandler onClickHandler, NoteListOnLongClickHandler onLongClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
        mOnLongClickHandler = onLongClickHandler;
    }

    public Cursor getData() {
        return mData;
    }

    public void setData(@Nullable Cursor data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public NoteListViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int viewType) {
        ListItemNoteBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_item_note, parentViewGroup, false);
        dataBinding.getRoot().setFocusable(true);
        NoteListViewHolder holder = new NoteListViewHolder(dataBinding.getRoot());
        holder.binding = dataBinding;
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteListViewHolder holder, int position) {
        mData.moveToPosition(position);

        String title = mData.getString(MainActivity.INDEX_NOTE_TITLE);
        String content = com.example.mehdisaffar.testproject.utils.NoteUtils.getShortContent(mContext, mData.getString(MainActivity.INDEX_NOTE_CONTENT));
        if (title.isEmpty()) {
            title = "<Empty>";
        }
        if (content.isEmpty()) {
            content = "<Empty>";
        }
        holder.binding.listItemNoteTitle.setText(title);
        holder.binding.listItemNoteContent.setText(content);
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.getCount();
    }

    public interface NoteListOnClickHandler {
        void onClick(long noteId);
    }

    public interface NoteListOnLongClickHandler {
        void onLongClick(long noteId);
    }

    public class NoteListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnLongClickListener {
        private ListItemNoteBinding binding;
        private long mNoteId;
        private int mAdapterPosition;

        public NoteListViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mAdapterPosition = getAdapterPosition();
            mData.moveToPosition(mAdapterPosition);
            mNoteId = mData.getLong(MainActivity.INDEX_NOTE_ID);
            mOnClickHandler.onClick(mNoteId);
        }

        @Override
        public boolean onLongClick(View view) {

            mAdapterPosition = getAdapterPosition();
            mData.moveToPosition(mAdapterPosition);
            mNoteId = mData.getLong(MainActivity.INDEX_NOTE_ID);
            mOnLongClickHandler.onLongClick(mNoteId);
            return true;
        }
    }
}
