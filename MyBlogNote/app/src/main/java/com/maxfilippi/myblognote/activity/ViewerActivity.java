package com.maxfilippi.myblognote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maxfilippi.myblognote.R;
import com.maxfilippi.myblognote.model.Note;
import com.maxfilippi.myblognote.utils.TextInputRichEditText;

public class ViewerActivity extends SuperActivity
{

    // UI ELEMENTS
    private Toolbar toolbar;

    private TextView textTitle;
    private TextView textDate;
    private TextInputRichEditText textContent;

    private FloatingActionButton buttonEditNote;

    // DATA
    private Note currentNote;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        // Toolbar support
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instantiate UI Elements
        textTitle = (TextView) findViewById(R.id.viewer_note_text_title);
        textDate = (TextView) findViewById(R.id.viewer_note_text_date);
        textContent = (TextInputRichEditText) findViewById(R.id.viewer_note_text_content);

        buttonEditNote = (FloatingActionButton) findViewById(R.id.viewer_note_button_edit_note);


        // Add listener
        buttonEditNote.setOnClickListener(new OnButtonEditNoteClickListener());

        // Get the core engine shared data
        currentNote = appCore.selectedNote;

        // Load data into the view
        initNoteViews(currentNote, textTitle, textDate, textContent);
    }



    private class OnButtonEditNoteClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            //Show message Note saved
            Toast.makeText(ViewerActivity.this, "Your are now in Edit Mode!", Toast.LENGTH_LONG).show();

            // Prepare the edit activity
            Intent intentEdit = new Intent(ViewerActivity.this, EditNoteActivity.class);
            startActivity(intentEdit);
            // Transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            // Remove from stack
            finish();
        }

    }

}
