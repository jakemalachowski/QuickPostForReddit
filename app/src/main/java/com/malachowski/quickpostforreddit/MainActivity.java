package com.malachowski.quickpostforreddit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends Activity {

    Button btSelectPic, btSubmit;
    EditText subredditET, titleET;
    ImageView thumbnail;

    private Upload upload;
    private Bitmap returnBitmap;

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_LOGIN = 2;

    public String ANDROID_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(getApplicationContext(), OAuthView.class);
        startActivityForResult(i, RESULT_LOGIN);

        ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        upload = new Upload();

        btSelectPic = (Button) findViewById(R.id.selectPhotoBt);
        btSubmit = (Button) findViewById(R.id.submitBt);
        subredditET = (EditText) findViewById(R.id.subredditET);
        subredditET.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        titleET = (EditText) findViewById(R.id.titleET);
        thumbnail = (ImageView) findViewById(R.id.quickPostImage);



        btSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.
                        Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, RESULT_LOAD_IMAGE);
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UploadService uploadService = new UploadService(upload);
                    Log.d("submit", "In UploadImage, calling uploadService");
                    uploadService.execute();

                    uploadService.get(250000, TimeUnit.MILLISECONDS);
                    Log.d("Link: ", uploadService.response.data.link);

                } catch (InterruptedException | ExecutionException | TimeoutException | NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_LOAD_IMAGE) {
            Uri uri;
            try {
                uri = data.getData();
            } catch (NullPointerException e) {
                return;
            }

            String fullPath = getRealPathFromURI(this, uri);
            upload.image = new File(fullPath);

            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                returnBitmap = BitmapFactory.decodeFile(picturePath);
                Log.d("size", "Width: " + returnBitmap.getWidth() + " Height: " + returnBitmap.getHeight());

                //Check if the picture was taken in portrait or landscape, then scales the image to save memory
                //TODO Set the proper orientation of the picture based on width and height.
                if (returnBitmap.getWidth() > returnBitmap.getHeight()) {

                    Log.d("ifelse", "Called if");
                    int nh = (int) (returnBitmap.getHeight() * (800.0 / returnBitmap.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(returnBitmap, 800, nh, true);
                    thumbnail.setImageBitmap(scaled);
                } else {
                    Log.d("ifelse", "Called else");
                    int nh = (returnBitmap.getWidth() * (800 / returnBitmap.getHeight()));
                    Bitmap scaled = Bitmap.createScaledBitmap(returnBitmap, 800, nh, true);
                    thumbnail.setImageBitmap(scaled);
                }

                String subreddit = subredditET.getText().toString();

                upload.title = titleET.getText().toString();

            }
        } else if (resultCode == RESULT_LOGIN)
        {
            Toast.makeText(this,"RESULT_LOGIN", Toast.LENGTH_LONG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
