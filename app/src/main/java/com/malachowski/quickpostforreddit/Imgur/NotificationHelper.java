package com.malachowski.quickpostforreddit.Imgur;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.malachowski.quickpostforreddit.R;

/**
 * Created by Jacob on 3/17/2015.
 */

public class NotificationHelper {
    public final static String TAG = NotificationHelper.class.getSimpleName();

    private Context mContext;


    public NotificationHelper(Context mContext) {
        this.mContext = mContext;
    }



    public void createUploadingNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_upload);
        mBuilder.setContentTitle("Uploading image...");

        mBuilder.setColor(mContext.getResources().getColor(R.color.primary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.getString(R.string.app_name).hashCode(), mBuilder.build());

    }
    public void createUploadedNotification(ImageResponse response){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle("Successfully uploaded ");

        mBuilder.setContentText(response.data.link);

        mBuilder.setColor(mContext.getResources().getColor(R.color.primary));


        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.data.link));
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(response.data.link));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.data.link);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.getString(R.string.app_name).hashCode(), mBuilder.build());
    }
    public void createFailedUploadNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle("Image failed to upload...");


        mBuilder.setColor(mContext.getResources().getColor(R.color.primary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.getString(R.string.app_name).hashCode(), mBuilder.build());
    }



}

