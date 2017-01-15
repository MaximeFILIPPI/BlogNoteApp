package com.maxfilippi.myblognote.utils;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Max on 1/14/17.
 */

public class StringUtils
{

    public static List<String> extractDatasFromContent(String content, String regex)
    {
        final List<String> tagValues = new ArrayList<String>();

        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(content);

        while (matcher.find())
        {
            tagValues.add(matcher.group(1));
        }

        return tagValues;
    }


    public static SpannableStringBuilder trimSpannable(SpannableStringBuilder spannable)
    {
        int trimStart = 0;
        int trimEnd = 0;

        String text = spannable.toString();

        while (text.length() > 0 && text.startsWith("\n"))
        {
            text = text.substring(1);
            trimStart += 1;
        }

        while (text.length() > 0 && text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 1);
            trimEnd += 1;
        }

        return spannable.delete(0, trimStart).delete(spannable.length() - trimEnd, spannable.length());
    }


    public static String replaceLast(String string, String substring, String replacement)
    {
        int index = string.lastIndexOf(substring);
        if (index == -1)
            return string;
        return string.substring(0, index) + replacement
                + string.substring(index+substring.length());
    }



    // Cleanup nested emphasis markup in HTML due to writing span
    public static String cleanUpGeneratedHTML(String html)
    {
        String formattedHTML = "";

        // First pass for tag bold <b></b>
        String[] fullSplit = html.split("(?=</b>)", Integer.MAX_VALUE);

        for(int i = 0; i < fullSplit.length; i++)
        {
            String currentString = fullSplit[i];

            if(i == 0)
            {
                formattedHTML = currentString;
            }
            else
            {
                if(currentString.startsWith("</b><b>"))
                {
                    fullSplit[i] = currentString.replace("</b><b>", "");
                }

                formattedHTML = formattedHTML + fullSplit[i];
            }
        }

        // Second pass for tag italic <i></i>
        String[] fullSplit2 = formattedHTML.split("(?=</i>)", Integer.MAX_VALUE);

        for(int i = 0; i < fullSplit2.length; i++)
        {
            String currentString = fullSplit2[i];

            if(i == 0)
            {
                formattedHTML = currentString;
            }
            else
            {
                if(currentString.startsWith("</i><i>"))
                {
                    fullSplit2[i] = currentString.replace("</i><i>", "");
                }


                formattedHTML = formattedHTML + fullSplit2[i];
            }
        }

        // Third pass for tag underline <u></u>
        String[] fullSplit3 = formattedHTML.split("(?=</u>)", Integer.MAX_VALUE);

        for(int i = 0; i < fullSplit3.length; i++)
        {
            String currentString = fullSplit3[i];

            if(i == 0)
            {
                formattedHTML = currentString;
            }
            else
            {
                if(currentString.startsWith("</u><u>"))
                {
                    fullSplit3[i] = currentString.replace("</u><u>", "");
                }

                formattedHTML = formattedHTML + fullSplit3[i];
            }
        }

        // Return html cleaned up
        return formattedHTML;
    }



}
