package com.malachowski.quickpostforreddit.Imgur;

import android.os.AsyncTask;
import android.util.Log;

import com.malachowski.quickpostforreddit.Constants;

import java.io.File;

import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

/**
 * Created by Jacob on 3/17/2015.
 */

public class UploadService extends AsyncTask<Void, Void, Void>
{
    public final static String TAG = UploadService.class.getSimpleName();


    public String title, description, albumId;
    public ImageResponse response;
    private File image;


    public UploadService(Upload upload)
    {
        this.image = upload.image;
        this.title = upload.title;
        this.description = upload.description;
        this.albumId = upload.albumId;


        Log.d("Submit", "In uploadService");


    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
//        notificationHelper.createUploadingNotification();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        //if(NetworkUtils.isConnected(activity)) {
        //    if (NetworkUtils.connectionReachable()) {

        /*
          Create rest adapter using our imgur API
         */


        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPI.server)
                .build();

        /*
          Set rest adapter logging if we're already logging
         */

        if (Constants.LOGGING)
            imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        /*
          Upload image, get response for image
         */
        try
        {


            response = imgurAdapter.create(ImgurAPI.class)
                    .postImage(
                            Constants.getClientAuth(), title, description, albumId, null, new TypedFile("image/*", image)
                    );

        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

