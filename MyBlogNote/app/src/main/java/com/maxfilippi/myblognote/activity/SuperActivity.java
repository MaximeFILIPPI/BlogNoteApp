package com.maxfilippi.myblognote.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.maxfilippi.myblognote.R;
import com.maxfilippi.myblognote.core.CoreEngine;
import com.maxfilippi.myblognote.model.Note;
import com.maxfilippi.myblognote.utils.BitmapUtils;
import com.maxfilippi.myblognote.utils.StringUtils;
import com.maxfilippi.myblognote.utils.TextInputRichEditText;

/**
 * Created by Max on 1/12/17.
 *
 * SUPER CLASS extends from AppCompatActivity
 *
 */

public class SuperActivity extends AppCompatActivity
{

    // Static class TAG for logs
    private static final String TAG = "SuperActivity";

    // Static instance of the core engine
    protected static CoreEngine appCore;

    // Toolbar menu
    protected Menu menu;



    //
    //  PROTECTED SUPER CLASS LIFE CYCLE
    // ============================================================================================

    // On Create method to initialize the moteur
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Obtain the shared Moteur
        appCore = CoreEngine.shared();
    }

    // Common Menu Override
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.no_menu, menu);
        // Action bar menu
        this.menu = menu;
        return true;
    }


    // Common Actions Menu Override
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            // Action back
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Back pressed with custom transition
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        // Animate transition
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }


    //
    //  PUBLIC SUPER CLASS METHODS
    // ============================================================================================

    // Close keyboard when called
    public void hideSoftKeyboard()
    {
        // Check if a view has focus:
        View view = this.getCurrentFocus();

        // If the view is not null
        if (view != null)
        {
            // Android methods to hide the keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void initNoteViews(Note note, TextView textTitle, TextView textDate, TextInputRichEditText textContent)
    {

        if(note != null && note.title != null && note.date != null && note.content != null)
        {

            textTitle.setText(note.title);

            textDate.setText(note.date);

            String contentHTML = note.content;


            // Generate Spannable String builder
            SpannableStringBuilder html = (SpannableStringBuilder) Html.fromHtml(contentHTML);

            String shtml = html.toString();

            if(note.uriFiles != null && note.uriFiles.size() > 0)
            {
                for(int i = 0; i < note.uriFiles.size(); i++)
                {
                    String uri = note.uriFiles.get(i);
                    String newPath = "[img=" + uri + "]";
                    int startIndex = shtml.indexOf(newPath);
                    int endIndex = startIndex + newPath.length();

                    Log.d(TAG, "New path = " + newPath + "/ start index of img new path = " + String.valueOf(startIndex) + " / end index = " + endIndex);

                    if(startIndex > 0 && endIndex < html.length())
                    {
                        Bitmap bitmap = BitmapUtils.uriToBitmap(Uri.parse(uri), this);
                        html = TextInputRichEditText.addPictureToStringBuilder(bitmap, this, html, newPath, startIndex, endIndex);
                    }
                }
            }

            // Remove unwanted annoying extra line breaks that html will generate
            html = StringUtils.trimSpannable(html);
            // Add a space to the end to not cancel the last span when clicked again
            html.append(" ");

            // Set the content to the input
            textContent.setText(new SpannableString(html));

            Log.d(TAG, "input loaded = " + String.valueOf(textContent.getText()));
        }





    }

}
