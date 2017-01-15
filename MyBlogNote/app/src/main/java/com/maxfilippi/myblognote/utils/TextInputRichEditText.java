package com.maxfilippi.myblognote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Max on 1/13/17.
 */

public class TextInputRichEditText extends TextInputEditText
{
    private static final String TAG = "TextInputRichEditText";

    // Enum for text style
    public enum InputStyle
    {
        TYPE_BOLD,
        TYPE_ITALIC,
        TYPE_UNDERLINE
    }

    private Context context;

    // Listener / Delegate
    private OnSelectionChangedListener delegate;

    // Cursor position
    private int lastStartPosition = 0;
    private int lastEndPosition = 0;



    //
    // CONSTRUCTORS
    // =========================================================================================

    public TextInputRichEditText(Context context)
    {
        super(context);
        initTextInputRichEditText(context);
    }

    public TextInputRichEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initTextInputRichEditText(context);
    }

    public TextInputRichEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initTextInputRichEditText(context);
    }


    private void initTextInputRichEditText(Context context)
    {
        this.context = context;
        this.addTextChangedListener(new ImageSpanTextWatcher());
    }


    //
    // OVERRIDED METHODS
    // =========================================================================================

    @Override
    protected void onSelectionChanged(int selStart, int selEnd)
    {
        if(delegate != null && (selStart != lastStartPosition || selEnd != lastEndPosition))
        {
            lastStartPosition = selStart;
            lastEndPosition = selEnd;

            delegate.onSelectionChanged(selStart, selEnd);
        }

        super.onSelectionChanged(selStart, selEnd);
    }


    //
    // CUSTOM LISTENERS / DELEGATE
    // =========================================================================================

    public interface OnSelectionChangedListener
    {
        void onSelectionChanged(int selectionStart, int selectionEnd);
    }


    public void addOnSelectionCursorChanged(OnSelectionChangedListener listener)
    {
        this.delegate = listener;
    }



    //
    // CUSTOM UTILS METHODS
    // =========================================================================================

    // To disable the native android copy/cut/past toolbar on the top of the screen
    public void disableActionMode()
    {
        this.setCustomSelectionActionModeCallback(new ActionMode.Callback()
        {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode)
            {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                return false;
            }
        });
    }


    // Update the design of the text with spans
    public void addStyleForSelectedText(List<InputStyle> options)
    {
        Log.d(TAG, "changeSelectedTextWithAttributes()");
        // Create the spannable string builder to get / apply styles
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(this.getText());

        // Retrieve the selection part
        int selStart = this.getSelectionStart();
        int selEnd = this.getSelectionEnd();

        // Create realStart and realEnd because selStart, selEnd
        // and current TextInputRichEditText will be changed automatically
        int realStart = selStart;
        int realEnd = selEnd;

        // If we are at the end of the input and we want to modify span
        if(selStart == selEnd && selStart > 0)
        {
            // decrease one character
            selStart = selStart -1;
        }

        // Find the exact string for later replacement
        String selectedText = this.getText().toString().substring(selStart, selEnd);
        SpannableString selection = new SpannableString(selectedText);

        // Primitives for later removing unused style spans if any
        boolean removeBold = true;
        boolean removeItalic = true;
        boolean removeUnderline = true;

        // Check options for adding new spans
        for (int i = 0; i < options.size() ; i++)
        {
            // Check if button bold is enable by user
            if(options.get(i) == InputStyle.TYPE_BOLD)
            {
                // Add Span
                builder.setSpan(new StyleSpan(Typeface.BOLD), selStart, selEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Check for not removing this span later on
                removeBold = false;
            }

            // Check if button italic is enable by user
            if(options.get(i) == InputStyle.TYPE_ITALIC)
            {
                // Add Span
                builder.setSpan(new StyleSpan(Typeface.ITALIC), selStart, selEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Check for not removing this span later on
                removeItalic = false;
            }


            // Check if button underline is enable by user
            if(options.get(i) == InputStyle.TYPE_UNDERLINE)
            {
                // Add Span
                builder.setSpan(new UnderlineSpan(), selStart, selEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Check for not removing this span later on
                removeUnderline = false;
            }

        }


        // Remove unnecessary style spans present on the edit text (if any)
        builder = removeUnwantedSpansFromText(builder, removeBold, removeItalic, removeUnderline, selStart, selEnd);


        // current selection is replace with new selection
        //builder.replace(inputContent.getSelectionStart(), inputContent.getSelectionEnd(), selection);
        builder.replace(selStart, selEnd, selection);

        // Finally put the new builder with all the new spans inside the edit text
        this.setText(builder);


        // Re-select the area before force loss of the builder
        this.setSelection(realStart, realEnd);
    }



    // REMOVE UNNECESSARY STYLE SPANS PRESENT (IF ANY)
    private SpannableStringBuilder removeUnwantedSpansFromText(SpannableStringBuilder builder,
                                                               boolean removeBold,
                                                               boolean removeItalic,
                                                               boolean removeUnderline,
                                                               int positionStart,
                                                               int positionEnd)
    {

        // Prepare to remove unuse style span of the text view
        StyleSpan[] ss = builder.getSpans(positionStart, positionEnd, StyleSpan.class);
        UnderlineSpan[] us = builder.getSpans(positionStart, positionEnd, UnderlineSpan.class);

        // Actual part to remove unuse style span of the text
        for (int i = 0; i < ss.length; i++)
        {
            // Check if still has as bold span style while button bold is not enable by user
            if (ss[i].getStyle() == Typeface.BOLD && removeBold)
            {
                // Remove the old style span from builder
                builder.removeSpan(ss[i]);
            }
            // Check if still has as italic span style while button bold is not enable by user
            else if(ss[i].getStyle() == Typeface.ITALIC && removeItalic)
            {
                // Remove the old style span from builder
                builder.removeSpan(ss[i]);
            }
        }

        // Part to remove unuse underline span of the text
        for (int j = 0; j < us.length; j++)
        {
            // Check if still has as underline span while button underline is not enable by user
            if(removeUnderline)
            {
                // Remove the old underline span from builder
                builder.removeSpan(us[j]);
            }
        }

        // Return current builder
        return builder;
    }


    public void addPicture(Bitmap bitmap, String selectedText, int selectionStart, int selectionEnd)
    {
        Log.d(TAG, "addPicture( selectedText = " + selectedText + " , selectionStart = " + selectionStart + " , selectionEnd = " + selectionEnd + ")");

        // Create the image span from our bitmap
        ImageSpan imageSpan = new ImageSpan(context, bitmap);

        // Create the spannable string builder to apply our image span
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(this.getText());


        // current selection is replaceв with imageId
        builder.replace(selectionStart, selectionEnd, selectedText);

        // This adds a span to display image where the imageId is. If you do builder.toString() - the string will contain imageId where the imageSpan is.
        // you can use it later - if you want to find location of imageSpan in text;
        builder.setSpan(imageSpan, selectionStart, selectionStart + selectedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        this.setText(builder);

        this.setSelection(selectionStart + selectedText.length());
    }


    public static SpannableStringBuilder addPictureToStringBuilder(Bitmap bitmap, Context context, SpannableStringBuilder builder, String selectedText, int selectionStart, int selectionEnd)
    {
        // Create the image span from our bitmap
        ImageSpan imageSpan = new ImageSpan(context, bitmap);


        Log.d(TAG, "builder length = " + String.valueOf(builder.length()));

        // current selection is replaceв with imageId
        builder.replace(selectionStart, selectionEnd, selectedText);

        // This adds a span to display image where the imageId is. If you do builder.toString() - the string will contain imageId where the imageSpan is.
        // you can use it later - if you want to find location of imageSpan in text;
        builder.setSpan(imageSpan, selectionStart, selectionStart + selectedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }


    public List<InputStyle> getStylesForSelection(int selectionStart, int selectionEnd)
    {
        Log.d(TAG, "getStylesForSelection( selectionStart = " + selectionStart + ", selection end = " + selectionEnd + ")");
        List<InputStyle> currentStyles = new ArrayList<>();

        // Build the string to retrieve the span
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(this.getText());

        // Retrieve all StyleSpan (IF Any)
        StyleSpan[] ss = builder.getSpans(selectionStart, selectionEnd, StyleSpan.class);

        // Retrieve all UnderlineSpan (IF Any)
        UnderlineSpan[] us = builder.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);

        // Check the StyleSpan that we have retrieved to see the content
        for(int i = 0; i < ss.length; i++)
        {
            if (ss[i].getStyle() == Typeface.BOLD)
            {
                currentStyles.add(InputStyle.TYPE_BOLD);
            }

            if (ss[i].getStyle() == Typeface.ITALIC)
            {
                currentStyles.add(InputStyle.TYPE_ITALIC);
            }

        }

        // If UnderlineSpan is present that's mean the text is underlined
        if(us.length > 0)
        {
            currentStyles.add(InputStyle.TYPE_UNDERLINE);
        }

        // Return list of styles present
        return currentStyles;
    }




    public class ImageSpanTextWatcher implements TextWatcher
    {
        Object[] mTouchedSpans;
        int[] mSpanLength;
        boolean replacing = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            if (s instanceof SpannableStringBuilder)
            {
                SpannableStringBuilder ssb = (SpannableStringBuilder) s;
                mTouchedSpans = ssb.getSpans(start, start + count, ImageSpan.class);
                if (mTouchedSpans != null && mTouchedSpans.length > 0)
                {
                    mSpanLength = new int[mTouchedSpans.length];
                    for (int i = 0; i < mTouchedSpans.length; i++)
                    {
                        mSpanLength[i] = ssb.getSpanEnd(mTouchedSpans[i]) - ssb.getSpanStart(mTouchedSpans[i]);
                    }
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (s instanceof SpannableStringBuilder)
            {
                SpannableStringBuilder ssb = (SpannableStringBuilder) s;

                if (replacing)
                    return;
                replacing = true;
                if (mTouchedSpans != null && mTouchedSpans.length > 0)
                    for (int i = 0; i < mTouchedSpans.length; i++)
                    {
                        int newLen = ssb.getSpanEnd(mTouchedSpans[i]) - ssb.getSpanStart(mTouchedSpans[i]);
                        if (newLen < mSpanLength[i])
                        {
                            ssb.replace(ssb.getSpanStart(mTouchedSpans[i]), ssb.getSpanEnd(mTouchedSpans[i]), "");
                        }
                    }
                mTouchedSpans = null;
                mSpanLength = null;
                replacing = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }


}
