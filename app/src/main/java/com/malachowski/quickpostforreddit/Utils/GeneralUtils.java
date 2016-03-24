package com.malachowski.quickpostforreddit.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by Jacob on 8/5/2015.
 */
public class GeneralUtils
{

    public static Bitmap resizeBitmap(Context context, Uri image)
    {
        Bitmap returnBitmap;

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(image,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        returnBitmap = BitmapFactory.decodeFile(picturePath);
        Log.d("size", "Width: " + returnBitmap.getWidth() + " Height: " + returnBitmap.getHeight());

        //Check if the picture was taken in portrait or landscape, then scales the image to save memory
        //TODO Set the proper orientation of the picture based on width and height.
        if (returnBitmap.getWidth() > returnBitmap.getHeight())
        {
            int nh = (int) (returnBitmap.getHeight() * (800.0 / returnBitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(returnBitmap, 800, nh, true);
            return scaled;
        } else
        {
            int nw = (int) (returnBitmap.getWidth() * (800.0 / returnBitmap.getHeight()));
            Bitmap scaled = Bitmap.createScaledBitmap(returnBitmap, nw, 800, true);
            return scaled;
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
}
