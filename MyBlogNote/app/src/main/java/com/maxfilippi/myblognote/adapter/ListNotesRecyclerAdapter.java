package com.maxfilippi.myblognote.adapter;

import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.maxfilippi.myblognote.R;
import com.maxfilippi.myblognote.listener.OnListNotesInteractionListener;
import com.maxfilippi.myblognote.model.Note;

import java.util.List;

/**
 * Created by Max on 1/12/17.
 */

public class ListNotesRecyclerAdapter extends RecyclerView.Adapter<ListNotesRecyclerAdapter.ViewHolder>
{

    private final List<Note> mValues;
    private final OnListNotesInteractionListener mListener;



    //
    // CUSTOM CONSTRUCTOR
    // =========================================================================================

    public ListNotesRecyclerAdapter(List<Note> items, OnListNotesInteractionListener listener)
    {
        this.mValues = items;
        this.mListener = listener;
    }



    //
    // RECYCLERVIEW ADAPTER LIFE CYCLE
    // =========================================================================================

    // Create view from layout
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // Inflate the row layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_note, parent, false);
        // Return the view inside the reusable view holder
        return new ViewHolder(view);
    }

    // Bind datas to view
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);

        holder.mTextTitle.setText(holder.mItem.getTitle());
        holder.mTextDate.setText(holder.mItem.getDateFormatted());
        holder.mImgFirstPicture.setImageURI(holder.mItem.getFirstPictureURI());

        holder.mButtonSelect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemNoteClickListener(holder.mItem);
                }
            }
        });

    }

    // Return size of the recyclerview
    @Override
    public int getItemCount()
    {
        return mValues.size();
    }



    //
    // VIEW HOLDER (for reusable rows)
    // =========================================================================================

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;

        public final TextView mTextTitle;
        public final TextView mTextDate;
        public final SimpleDraweeView mImgFirstPicture;

        public final Button mButtonSelect;

        public Note mItem;


        public ViewHolder(View view)
        {
            super(view);
            mView = view;

            mTextTitle = (TextView) view.findViewById(R.id.row_item_note_text_title);
            mTextDate = (TextView) view.findViewById(R.id.row_item_note_text_date);
            mImgFirstPicture = (SimpleDraweeView) view.findViewById(R.id.row_item_note_img_first_picture);
            mButtonSelect = (Button) view.findViewById(R.id.row_item_note_button_select);

        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + String.valueOf(mTextTitle.getText()) + "'";
        }

    }




    //
    // PUBLIC ADAPTER METHODS
    // =========================================================================================

    // Update list of notes
    public void updateNotesList(List<Note> newItems)
    {
        // Prefer to clear then add than equal for consequent reduced use of heap memory
        mValues.clear();
        mValues.addAll(newItems);

        // Notify the list to update content on the user's screen
        notifyDataSetChanged();
    }



}
