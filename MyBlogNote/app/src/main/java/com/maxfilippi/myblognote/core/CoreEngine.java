package com.maxfilippi.myblognote.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maxfilippi.myblognote.model.Note;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Max on 1/12/17.
 *
 * Main Singleton of the application
 *
 */

public class CoreEngine
{

    // Static class TAG for logs
    private static String TAG = "CoreEngine";

    // Private static instance of our CoreEngine
    private static CoreEngine instance;
    public static Context application;

    // Data Manager
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    // Useful Data
    public static List<Note> listOfNotes;
    public static Note selectedNote;


    // Method shared for instance of AppCore
    public static synchronized CoreEngine shared()
    {
        // If instance not initialized yet
        if (instance == null)
        {
            // Create the instance
            instance = new CoreEngine();
        }

        // Return the RKMoteur instance
        return instance;
    }

    // Method shared for instance of RKMoteur
    public static synchronized CoreEngine shared(Context applicationContext)
    {
        // If instance not initialized yet
        if (instance == null)
        {
            // Create the instance
            instance = new CoreEngine();
        }

        // Adding application context
        application = applicationContext;

        // Initialize shared preferences (Data Manager)
        preferences = application.getSharedPreferences("MyBlogNote-Preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        // First launch of monitoring
        //launchMonitoring(application);

        // Prepare webservices client
        //prepareWebserviceAPI();

        listOfNotes = getNotes();

        // Return CoreEngine instance
        return instance;
    }


    // Private forbidden initializer
    private CoreEngine()
    {
        Log.d(TAG, "CoreEngine singleton initialized");
    }



    //
    // PUBLIC PROJECT METHODS
    // ============================================================================================

    // GENERATE RANDOM KEY FOR ID
    public static String generateRandomKey()
    {
        String key = "";
        for(int i = 0; i < 7; i++)
        {
            Random r = new Random();
            char c = (char) (r.nextInt(26) + 'a');
            key = key + c;
        }
        return key.toUpperCase();
    }


    // Delete existing file for a determined path
    public static void deleteFile(String path)
    {
        File file = new File(path);
        if(file.exists())
        {
            boolean deleted = file.delete();
        }
    }


    //
    // SIMPLE CACHE MANAGEMENT
    // ============================================================================================

    // Add new notes to the list and save in cache
    public static void saveNewNote(Note newNote)
    {
        selectedNote = newNote;

        if(listOfNotes == null)
        {
            listOfNotes = new ArrayList<>();
        }

        if(!listOfNotes.contains(newNote))
        {
            listOfNotes.add(newNote);
        }

        // Save in cache
        saveNotes(listOfNotes);
    }

    // Save in user pref cache notes
    private static void saveNotes(List<Note> listNotes)
    {
        // Use of the library Gson
        Gson gsonNotes = new Gson();
        Type typeNotes = new TypeToken<List<Note>>() {}.getType();

        // Convert list of objects to json
        String jsonNote = gsonNotes.toJson(listNotes, typeNotes);
        Log.d(TAG, "json note converted = " + jsonNote);

        // Fast storage of the file in the user prefs
        editor.putString(Constants.KEY_SAVE_NOTES, jsonNote);
        editor.commit();
    }

    // Retrieve saved notes in user cache
    private static List<Note> getNotes()
    {
        List<Note> notesInCache = new ArrayList<>();
        // Use of the library Gson
        Gson gsonNotes = new Gson();
        Type typeNotes = new TypeToken<List<Note>>() {}.getType();

        // Get the json file from the user prefs
        String jsonNotes = preferences.getString(Constants.KEY_SAVE_NOTES, "[]");

        Log.d(TAG, "json note converted = " + jsonNotes);

        // load the list of objects from json
        notesInCache = gsonNotes.fromJson(jsonNotes, typeNotes);

        return notesInCache;
    }

}
