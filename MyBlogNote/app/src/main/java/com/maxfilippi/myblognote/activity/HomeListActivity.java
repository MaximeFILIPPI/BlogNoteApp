package com.maxfilippi.myblognote.activity;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.maxfilippi.myblognote.R;
import com.maxfilippi.myblognote.adapter.ListNotesRecyclerAdapter;
import com.maxfilippi.myblognote.listener.OnListNotesInteractionListener;
import com.maxfilippi.myblognote.model.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeListActivity extends SuperActivity implements NavigationView.OnNavigationItemSelectedListener, OnListNotesInteractionListener
{
    // Static class TAG for logs
    private static final String TAG = "HomeListActivity";

    // UI ELEMENTS
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private FloatingActionButton buttonAddNote;
    private RelativeLayout layoutEmptyNoteList;

    private NavigationView navigationView;
    private DrawerLayout navDrawer;

    private ListNotesRecyclerAdapter adapter;


    //
    // ANDROID LIFE CYCLE
    // =========================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        // Toolbar support
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_sort_by_alpha_white_24dp));

        // Instantiate UI Elements
        recyclerView = (RecyclerView) findViewById(R.id.home_list_recycler_view);
        buttonAddNote = (FloatingActionButton) findViewById(R.id.home_list_button_add_note);
        navDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        // Setup RecyclerView and Adapter
        setupRecyclerAdapter();

        // Setup Navigation Drawer (Sliding menu panel)
        setupNavDrawer();


        // Add listener
        buttonAddNote.setOnClickListener(new OnButtonAddNoteClickListener());
    }


    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        // Check if everything is initialized
        if(appCore != null && appCore.listOfNotes != null && recyclerView != null && adapter != null)
        {
            // Make a copy of notes
            List<Note> newListOfNotes = new ArrayList<>(appCore.listOfNotes);
            // Refresh and Update the list of notes
            adapter.updateNotesList(newListOfNotes);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }



    //
    // PRIVATE CLASS METHODS
    // =========================================================================================

    // TITLE ASCENDING
    private void sortTitleAscending(List<Note> listNotes)
    {
        Collections.sort(listNotes, new Comparator<Note>()
        {
            @Override
            public int compare(Note note1, Note note2)
            {
                return note1.title.compareToIgnoreCase(note2.title);
            }

        });
    }

    // TITLE DESCENDING
    private void sortTitleDescending(List<Note> listNotes)
    {
        Collections.sort(listNotes, new Comparator<Note>()
        {
            @Override
            public int compare(Note note1, Note note2)
            {
                return note2.title.compareToIgnoreCase(note1.title);
            }

        });
    }

    // DATE ASCENDING
    private void sortDateAscending(List<Note> listNotes)
    {
        Collections.sort(listNotes, new Comparator<Note>()
        {
            @Override
            public int compare(Note note1, Note note2)
            {
                return note1.getRealDate().compareTo(note2.getRealDate());
            }

        });
    }

    // DATE DESCENDING
    private void sortDateDescending(List<Note> listNotes)
    {
        Collections.sort(listNotes, new Comparator<Note>()
        {
            @Override
            public int compare(Note note1, Note note2)
            {
                return note2.getRealDate().compareTo(note1.getRealDate());
            }

        });
    }

    // SEND EMAIL
    private void sendEmail()
    {
        try
        {
            // Prepare intent with Action
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:filippi.maxime@gmail.com"));
            startActivity(emailIntent);
        }
        catch (Exception e)
        {
            // Catch error for some hazardous devices without any mail app
        }
    }


    //
    // MENU OPTIONS
    // =========================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        // Check if everything is initialized
        if(appCore != null && appCore.listOfNotes != null && recyclerView != null && adapter != null)
        {
            // Make a copy of notes
            List<Note> listNotes = new ArrayList<>(appCore.listOfNotes);

            if (id == R.id.action_title_ascending)
            {
                sortTitleAscending(listNotes);
            }
            else if (id == R.id.action_title_descending)
            {
                sortTitleDescending(listNotes);
            }
            else if (id == R.id.action_date_ascending)
            {
                sortDateAscending(listNotes);
            }
            else if (id == R.id.action_date_descending)
            {
                sortDateDescending(listNotes);
            }

            adapter.updateNotesList(listNotes);
        }

        return super.onOptionsItemSelected(item);
    }



    //
    // NAVIGATION DRAWER OPTIONS
    // =========================================================================================

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        // Check if everything is initialized
        if(appCore != null && appCore.listOfNotes != null && recyclerView != null && adapter != null)
        {
            // Make a copy of notes
            List<Note> listNotes = new ArrayList<>(appCore.listOfNotes);

            if (id == R.id.nav_title_ascending)
            {
                sortTitleAscending(listNotes);
            }
            else if (id == R.id.nav_title_descending)
            {
                sortTitleDescending(listNotes);
            }
            else if (id == R.id.nav_date_ascending)
            {
                sortDateAscending(listNotes);
            }
            else if (id == R.id.nav_date_descending)
            {
                sortDateDescending(listNotes);
            }

            adapter.updateNotesList(listNotes);
        }


        if (id == R.id.nav_send)
        {
            sendEmail();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    //
    // PRIVATE CLASS METHODS
    // =========================================================================================

    // Setup recycler
    private void setupRecyclerAdapter()
    {

        // Create a new empty list
        List<Note> noteList = new ArrayList<>();

        // Check if the core engine has a list of notes loaded
        if(appCore.listOfNotes != null)
        {
            // Make a copy of it
            noteList = new ArrayList<>(appCore.listOfNotes);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListNotesRecyclerAdapter(noteList, this);
        recyclerView.setAdapter(adapter);
    }


    private void setupNavDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navDrawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


    // Launch next activity in viewer or edit mode
    private void startNoteActivity(boolean isEditMode)
    {
        // Launch next screen activity
        Intent intentAddNote = null;

        if(isEditMode)
        {
            // Edit mode
            intentAddNote = new Intent(HomeListActivity.this, EditNoteActivity.class);
        }
        else
        {
            // Viewer mode
            intentAddNote = new Intent(HomeListActivity.this, ViewerActivity.class);
        }

        // Start Activity
        startActivity(intentAddNote);

        // Animate the transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    //
    // RECYCLER VIEW DELEGATE
    // =========================================================================================

    @Override
    public void onItemNoteClickListener(Note note)
    {
        Log.d(TAG, "onItemNoteClickListener() :: Note clicked = " + note.getTitle());
        appCore.selectedNote = note;

        // Launch NoteActivity in viewer mode
        startNoteActivity(false);
    }


    //
    // PRIVATE CLASS LISTENERS
    // =========================================================================================

    private class OnButtonAddNoteClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            Log.d(TAG, "OnButtonAddNoteClickListener");
            appCore.selectedNote = new Note();
            appCore.selectedNote.id = appCore.generateRandomKey();

            // Launch NoteActivty in edit mode
            startNoteActivity(true);
        }
    }

}
