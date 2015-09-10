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

            Log.d("ifelse", "Called if");
            int nh = (int) (returnBitmap.getHeight() * (800.0 / returnBitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(returnBitmap, 800, nh, true);
            return scaled;
        } else
        {
            Log.d("ifelse", "Called else");
            int nh = (returnBitmap.getWidth() * (800 / returnBitmap.getHeight()));
            Bitmap scaled = Bitmap.createScaledBitmap(returnBitmap, 800, nh, true);
            return scaled;
        }
    }
}
