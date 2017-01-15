package com.maxfilippi.myblognote.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.maxfilippi.myblognote.R;
import com.maxfilippi.myblognote.fragment.DateDialog;
import com.maxfilippi.myblognote.model.Note;
import com.maxfilippi.myblognote.utils.BitmapUtils;
import com.maxfilippi.myblognote.utils.StringUtils;
import com.maxfilippi.myblognote.utils.TextInputRichEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.maxfilippi.myblognote.core.Constants.KEY_CHOICE_CAMERA;
import static com.maxfilippi.myblognote.core.Constants.KEY_CHOICE_GALLERY;

public class EditNoteActivity extends SuperActivity implements DateDialog.OnDateInteractionListener
{
    // Static class TAG for logs
    private static final String TAG = "EditNoteActivity";
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final String PATTERN_IMG = "\\[img=(.*?)\\]";

    // UI ELEMENTS
    private Toolbar toolbar;

    private TextInputEditText inputTitle;
    private TextInputEditText inputDate;
    private TextInputRichEditText inputContent;

    private ScrollView scrollView;

    private Button buttonActionBold;
    private Button buttonActionItalic;
    private Button buttonActionUnderline;

    private FloatingActionButton buttonSaveNote;

    // DATA
    private Note currentNote;

    private int lastSelectionStart = 0;
    private int lastSelectionEnd = 0;


    //
    // ANDROID LIFE CYCLE
    // =========================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Toolbar support
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the core engine shared data
        currentNote = appCore.selectedNote;

        // Instantiate UI Elements
        inputTitle = (TextInputEditText) findViewById(R.id.edit_note_input_edit_text_title);
        inputDate = (TextInputEditText) findViewById(R.id.edit_note_input_edit_text_date);
        inputContent = (TextInputRichEditText) findViewById(R.id.edit_note_input_edit_text_content);

        scrollView = (ScrollView) findViewById(R.id.edit_note_scrollview);

        // Instantiate custom toolbar for rich edit text
        buttonActionBold = (Button) findViewById(R.id.custom_tools_mode_bold);
        buttonActionItalic = (Button) findViewById(R.id.custom_tools_mode_italic);
        buttonActionUnderline = (Button) findViewById(R.id.custom_tools_mode_underline);

        buttonSaveNote = (FloatingActionButton) findViewById(R.id.edit_note_button_save_note);

        // Disable the default android action toolbar
        //inputContent.disableActionMode();

        // Set edit text listeners
        inputContent.addOnSelectionCursorChanged(new OnSelectionCursorChangedListener());
        inputDate.setOnClickListener(new OnButtonDateClickListener());

        // Set rich toolbar listeners
        buttonActionBold.setOnClickListener(new OnButtonRichToolbarClickListener());
        buttonActionItalic.setOnClickListener(new OnButtonRichToolbarClickListener());
        buttonActionUnderline.setOnClickListener(new OnButtonRichToolbarClickListener());

        buttonSaveNote.setOnClickListener(new OnButtonSaveNoteClickListener());

        // Touch listener on scrollview to hide keyboard
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                hideSoftKeyboard();
                return false;
            }
        });


        // Load data into the view
        initNoteViews(currentNote, inputTitle, inputDate, inputContent);

    }

    //
    // MENU OPTIONS
    // =========================================================================================

    // Common Menu Override
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
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

        // Overflow menu add image
        if(id == R.id.action_add_image)
        {
            // Call the dialog to choose the picture from camera or gallery
            displayPictureDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    //
    // PRIVATE CUSTOM EDIT TEXT
    // =========================================================================================

    // Update the text that has been selected with some SpanStyle
    private void updateInputContentStyle()
    {
        // Prepare list of options (wanted styles)
        List<TextInputRichEditText.InputStyle> styles = new ArrayList<>();

        // if button BOLD Selected
        if(buttonActionBold.isSelected())
        {
            // Add style wanted to options
            styles.add(TextInputRichEditText.InputStyle.TYPE_BOLD);
        }

        // if button ITALIC Selected
        if(buttonActionItalic.isSelected())
        {
            // Add style wanted to options
            styles.add(TextInputRichEditText.InputStyle.TYPE_ITALIC);
        }

        // if button UNDERLINE Selected
        if(buttonActionUnderline.isSelected())
        {
            // Add style wanted to options
            styles.add(TextInputRichEditText.InputStyle.TYPE_UNDERLINE);
        }

        // Add spans style for content rich text
        inputContent.addStyleForSelectedText(styles);
    }


    // CHANGE DESIGN OF BUTTONS IN RICH TEXT TOOLBAR
    private void updateRichTextToolbarDesign()
    {
        // BOLD
        if(buttonActionBold.isSelected())
        {
            // Enable
            buttonActionBold.setTextColor(getResources().getColor(R.color.BLOGNOTE_COLOR_WHITE));
            buttonActionBold.setAlpha(1.0f);
        }
        else
        {
            // Disable
            buttonActionBold.setTextColor(getResources().getColor(R.color.BLOGNOTE_COLOR_BLACK));
            buttonActionBold.setAlpha(0.4f);
        }

        // ITALIC
        if(buttonActionItalic.isSelected())
        {
            // Enable
            buttonActionItalic.setTextColor(getResources().getColor(R.color.BLOGNOTE_COLOR_WHITE));
            buttonActionItalic.setAlpha(1.0f);
        }
        else
        {
            // Disable
            buttonActionItalic.setTextColor(getResources().getColor(R.color.BLOGNOTE_COLOR_BLACK));
            buttonActionItalic.setAlpha(0.4f);
        }

        // UNDERLINE
        if(buttonActionUnderline.isSelected())
        {
            // Enable
            buttonActionUnderline.setTextColor(getResources().getColor(R.color.BLOGNOTE_COLOR_WHITE));
            buttonActionUnderline.setAlpha(1.0f);
        }
        else
        {
            // Disable
            buttonActionUnderline.setTextColor(getResources().getColor(R.color.BLOGNOTE_COLOR_BLACK));
            buttonActionUnderline.setAlpha(0.4f);
        }

    }

    // Check for a selected part of text if there is span attributes as bold / italic / underline...
    private void checkForActiveStyles(int selectionStart, int selectionEnd)
    {

        List<TextInputRichEditText.InputStyle> styles = inputContent.getStylesForSelection(selectionStart, selectionEnd);

        // Prepare our boolean for check style later on
        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderline = false;

        // Loop inside style to see which ones are actived
        for(int i = 0 ; i < styles.size(); i++)
        {
            if(styles.get(i) == TextInputRichEditText.InputStyle.TYPE_BOLD)
            {
                isBold = true;
            }
            else if(styles.get(i) == TextInputRichEditText.InputStyle.TYPE_ITALIC)
            {
                isItalic = true;
            }
            else if(styles.get(i) == TextInputRichEditText.InputStyle.TYPE_UNDERLINE)
            {
                isUnderline = true;
            }
        }
        // Set the buttons states
        buttonActionBold.setSelected(isBold);
        buttonActionItalic.setSelected(isItalic);
        buttonActionUnderline.setSelected(isUnderline);


        // Update the design of buttons if selected or not
        updateRichTextToolbarDesign();
    }



    //
    // EDIT TEXT & BUTTONS LISTENERS
    // =========================================================================================

    // CURSOR CHANGE LISTENER
    private class OnSelectionCursorChangedListener implements TextInputRichEditText.OnSelectionChangedListener
    {
        @Override
        public void onSelectionChanged(int selectionStart, int selectionEnd)
        {
            // Check there is no fallback on the selection
            if(!(selectionStart == 0 && selectionEnd == 0) && (lastSelectionStart != selectionStart && lastSelectionEnd != selectionEnd))
            {
                Log.d(TAG, "inputContent cursor position changed :: input content length = " + inputContent.length() + " selection start = " + String.valueOf(selectionStart) + " / selection end = " + String.valueOf(selectionEnd) + " / last selection start = " + lastSelectionStart + " / last selection end = " + lastSelectionEnd);
                lastSelectionStart = selectionStart;
                lastSelectionEnd = selectionEnd;

                if(selectionEnd == selectionStart && selectionStart == inputContent.length() && selectionEnd == inputContent.length())
                {
                    // Change text attributes while writing
                    updateInputContentStyle();
                    //changeSelectedTextWithAttributes();
                }
                else
                {
                    // Look for attributes (styles) on the selected part of text
                    checkForActiveStyles(selectionStart, selectionEnd);
                }
            }
        }
    }

    // BUTTON DATE LISTENER
    private class OnButtonDateClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            Log.d(TAG, "OnButtonDateClickListener()");
            // Display Date picker dialog fragment
            displayDateDialog();
        }

    }

    // BUTTON SAVE NOTE LISTENER
    private class OnButtonSaveNoteClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            // Hide Keyboard
            hideSoftKeyboard();

            // Check for later error message if fields are not completed
            boolean completeFields = false;

            // Check editText to be sure there is value everywhere
            if(inputTitle != null && inputTitle.getText().length() > 0)
            {
                if(inputDate != null && inputDate.getText().length() > 0)
                {
                    if(inputContent != null && inputContent.getText().length() > 0)
                    {
                        // Check OK
                        completeFields = true;


                        // Check if the current note exist
                        if(currentNote == null)
                        {
                            // Create the current note
                            currentNote = new Note();
                            currentNote.id = appCore.generateRandomKey();
                        }

                        // Set datas
                        currentNote.title = String.valueOf(inputTitle.getText());
                        currentNote.date = String.valueOf(inputDate.getText());

                        // Get the generated Spanned string from html
                        String textWithSpan = String.valueOf(Html.toHtml(inputContent.getText()));
                        // Cleanup the tag markup from spanned to html
                        textWithSpan = StringUtils.cleanUpGeneratedHTML(textWithSpan);

                        // Extract images url from the input content
                        currentNote.uriFiles = StringUtils.extractDatasFromContent(String.valueOf(inputContent.getText()), "\\[img=(.*?)\\]");

                        // Change paths for uri files
                        if(currentNote.uriFiles != null && currentNote.uriFiles.size() > 0)
                        {
                            for(int i = 0; i < currentNote.uriFiles.size(); i++)
                            {
                                String uri = currentNote.uriFiles.get(i);
                                String newPath = "[img=" + uri + "]";
                                textWithSpan = textWithSpan.replaceFirst("<img src=\"null\">", newPath);
                            }
                        }

                        Log.d(TAG, "inputContent formatted = " + textWithSpan);

                        // Set the content to the note
                        currentNote.content = textWithSpan;

                        // Save new note
                        appCore.saveNewNote(currentNote);

                        //Show message Note saved
                        Toast.makeText(EditNoteActivity.this, "Your note has been saved!", Toast.LENGTH_LONG).show();

                        // Prepare the viewer activity
                        Intent intentViewer = new Intent(EditNoteActivity.this, ViewerActivity.class);
                        startActivity(intentViewer);
                        // Transition Animation
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        // Remove from stack
                        finish();
                    }
                    else
                    {
                        // Add input error message
                        inputContent.setError("Please enter some Content");
                    }
                }
                else
                {
                    // Add input error message
                    inputDate.setError("Please select a Date");
                }
            }
            else
            {
                // Add input error message
                inputTitle.setError("Please choose a Title");
            }

            // If all fields are not completed
            if(!completeFields)
            {
                // Show error message
                Toast.makeText(EditNoteActivity.this, "Please complete all fields.", Toast.LENGTH_LONG).show();
            }

        }

    }


    //
    // CUSTOM EDIT TEXT TOOLS LISTENERS
    // =========================================================================================

    // Listener for Rich Text buttons (bold / italic / underline / highlight / etc..)
    private class OnButtonRichToolbarClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            Log.d(TAG, "OnButtonRichToolbarClickListener()");

            // Retrieve the current view
            Button currentButton = (Button) view;

            // Check the corresponding button
            if(currentButton.equals(buttonActionBold))
            {
                buttonActionBold.setSelected(!buttonActionBold.isSelected());
            }
            else if(currentButton.equals(buttonActionItalic))
            {
                buttonActionItalic.setSelected(!buttonActionItalic.isSelected());
            }
            else if(currentButton.equals(buttonActionUnderline))
            {
                buttonActionUnderline.setSelected(!buttonActionUnderline.isSelected());
            }

            // Update the design of the buttons
            updateRichTextToolbarDesign();

            // Change the part of the text that was selected with Spans
            updateInputContentStyle();
        }
    }


    //
    // CUSTOM DIALOGS
    // ============================================================================================

    // Simple function to call the date picker
    private void displayDateDialog()
    {
        // Create date dialog
        final DateDialog dateDialog = new DateDialog();
        // Show date dialog
        dateDialog.show(getSupportFragmentManager(), "dialog");
    }


    // Delegate date picker dialog
    @Override
    public void onDateConfirmedListener(Date date)
    {
        if(currentNote == null)
        {
            currentNote = new Note();
            currentNote.id = appCore.generateRandomKey();
        }

        currentNote.setDate(date);

        // Show the date in editText
        inputDate.setText(currentNote.getDateFormatted());
    }


    // CHOICE PICTURE DIALOG
    private void displayPictureDialog()
    {
        // Item for the dialog
        final String[] items = new String[]{KEY_CHOICE_CAMERA, KEY_CHOICE_GALLERY};

        // Creation on the fly of the Picture dialog
        new AlertDialog.Builder(this).setTitle("Add a Picture from:")
                .setItems(items, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int item)
                    {
                        // If user chose the camera
                        if (items[item].equals(KEY_CHOICE_CAMERA))
                        {
                            // Redirect to the native phone camera
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                        else if (items[item].equals(KEY_CHOICE_GALLERY))
                        {
                            // If api <= KITKAT
                            if (Build.VERSION.SDK_INT <= 19)
                            {
                                // Open normal dialog to select picture
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(intent, "Select a Picture from:"), PICK_IMAGE);
                            }
                            // If api > KITKAT
                            else if (Build.VERSION.SDK_INT > 19)
                            {
                                // Open bottom sheet for pick
                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(Intent.createChooser(intent, "Select a Picture from:"), PICK_IMAGE);
                            }
                        }
                        else
                        {
                            // Close the picture dialog
                            dialog.dismiss();
                        }
                    }
                }).show();
    }


    // ACTIVITY RESULT FOR GALLERY & CAMERA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "request code = " + requestCode + " / result code = " + resultCode + " / data = " + String.valueOf(data));

        // Check if we have data and the result code is OK
        if(resultCode == RESULT_OK && data != null)
        {
            Bitmap _bitmap = null;
            if(requestCode == REQUEST_CAMERA)
            {
                // Get the THUMBNAIL of the picture taken by the camera
                _bitmap = (Bitmap)data.getExtras().get("data");

            }
            else if (requestCode == PICK_IMAGE)
            {
                // Get the uri where the picture is stored in the gallery
                Uri imageUri = data.getData();
                InputStream imageStream = null;
                try
                {
                    // Open the uri and load the input stream
                    imageStream = getContentResolver().openInputStream(imageUri);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                // Decode bitmap from stream
                _bitmap = BitmapFactory.decodeStream(imageStream);
            }


            // Check if current note exist or not
            if(currentNote == null)
            {
                // Create an instance of Note
                currentNote = new Note();
                currentNote.id = appCore.generateRandomKey();
            }

            // Create a path to the app directory
            String path = getFilesDir() + "/" + currentNote.id + "_" + appCore.generateRandomKey() + ".jpg";
            String fullPath = "file://" + path;

            // Create a copy of our bitmap into as a JPG into the files directory of the app
            BitmapUtils.saveBitmapToFile(_bitmap, path, 100);

            // Add picture to our edit text
            inputContent.addPicture(_bitmap, "[img=" + fullPath + "]", inputContent.getSelectionStart(), inputContent.getSelectionEnd());
        }
        else
        {
            Toast.makeText(this, "No picture retrieved", Toast.LENGTH_LONG).show();
        }

    }
}
