package com.malachowski.quickpostforreddit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.malachowski.quickpostforreddit.Imgur.Upload;
import com.malachowski.quickpostforreddit.Imgur.UploadService;
import com.malachowski.quickpostforreddit.Reddit.OAuthView;
import com.malachowski.quickpostforreddit.Utils.GeneralUtils;
import com.malachowski.quickpostforreddit.Utils.RedditUtils;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.datatype.Duration;

public class MainActivity extends Activity
{

    Button btSelectPic, btSubmit;
    EditText subredditET, titleET;
    ImageView thumbnail;

    private Upload upload;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Skip the authorization proccess if the app already has an auth token
        //Launching it every time the app is started for debugging purposes

        Intent oAuthIntent = new Intent(getApplicationContext(), OAuthView.class);
        startActivityForResult(oAuthIntent, Constants.RESULT_LOGIN);


        upload = new Upload();

        btSelectPic = (Button) findViewById(R.id.selectPhotoBt);
        btSubmit = (Button) findViewById(R.id.submitBt);
        subredditET = (EditText) findViewById(R.id.subredditET);
        subredditET.setText("quickpostforreddit");
        subredditET.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        titleET = (EditText) findViewById(R.id.titleET);
        thumbnail = (ImageView) findViewById(R.id.quickPostImage);


        //Button to select the picture to upload
        btSelectPic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.
                        Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Constants.RESULT_LOAD_IMAGE);
            }
        });

        //Button to upload picture to Imgur and post to Reddit
        btSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    UploadService uploadService = new UploadService(upload);
                    Log.d("submit", "In UploadImage, calling uploadService");
                    uploadService.execute();

                    uploadService.get(250000, TimeUnit.MILLISECONDS);
                    Log.d("Link: ", uploadService.response.data.link);

                } catch (InterruptedException | ExecutionException | TimeoutException | NullPointerException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_LOAD_IMAGE)
        {
            Uri uri;
            try
            {
                uri = data.getData();


            } catch (NullPointerException e)
            {
                return;
            }

            String fullPath = GeneralUtils.getRealPathFromURI(this, uri);
            upload.image = new File(fullPath);

            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();

                thumbnail.setImageBitmap(GeneralUtils.resizeBitmap(this, selectedImage));
                String subreddit = subredditET.getText().toString();

                upload.title = titleET.getText().toString();

            }
        } else if (requestCode == Constants.RESULT_LOGIN)
        {
            String authCode = data.getStringExtra("authCode");
            Toast.makeText(this, authCode, Toast.LENGTH_LONG).show();

            RedditUtils redditUtils = new RedditUtils();

            redditUtils.fetchToken(authCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
