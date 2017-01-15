package com.maxfilippi.myblognote.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Max on 1/12/17.
 */

public class Note
{
    private static final String TAG = "Note";
    public static final String DATE_FORMAT = "dd - MM - yyyy";

    public String id;
    public String title;
    public String date;
    public String content;
    public List<String> uriFiles;


    public Note()
    {

    }


    public Note(String title, String date, String content, List<String> uriFiles)
    {
        this.title = title;
        this.date = date;
        this.content = content;
        this.uriFiles = uriFiles;
    }




    //
    // USEFUL MODEL GETTERS
    // =========================================================================================

    // Return title as String
    public String getTitle()
    {
        return String.valueOf(title);
    }

    // Return date formatted as String
    public String getDateFormatted()
    {
        return String.valueOf(this.date);
    }

    // Return the FIRST Picture of the note (IF Any) as String
    public String getFirstPictureURI()
    {
        if(uriFiles != null && uriFiles.size() > 0)
        {
            return String.valueOf(uriFiles.get(0));
        }
        else
        {
            return "";
        }
    }


    //
    // USEFUL MODEL SETTER
    // =========================================================================================

    public void setDate(Date date)
    {
        // Instantiate the date format
        DateFormat dateFormat = new SimpleDateFormat(Note.DATE_FORMAT, Locale.US);

        // Format and return the date as String
        this.date = dateFormat.format(date);

        Log.d(TAG, "new date = " + String.valueOf(this.date));
    }


    public Date getRealDate()
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(Note.DATE_FORMAT, Locale.US);

        try
        {
            date = format.parse(this.date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date;
    }


}
