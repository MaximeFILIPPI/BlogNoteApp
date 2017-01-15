package com.maxfilippi.myblognote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Max on 1/14/17.
 */

public class BitmapUtils
{


    // Save bitmap into file directory of the app
    public static void saveBitmapToFile(Bitmap bitmap, String path, int quality)
    {
        FileOutputStream out;
        File file = new File(path);

        // Create Path if not exist
        File directory = new File(getDirectoryPath(path));
        if (!directory.exists())
        {
            directory.mkdirs();
        }

        // Save bitmap
        try
        {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    // Retrieve bitmap from uri
    public static Bitmap uriToBitmap(Uri selectedFileUri, Context context)
    {
        Bitmap image = null;
        try
        {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image =  BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return image;
    }




    // clean up end of directory path
    public static String getDirectoryPath(String path)
    {
        String name = path.substring(path.lastIndexOf("/"));
        return path.replace(name, "");
    }




}
