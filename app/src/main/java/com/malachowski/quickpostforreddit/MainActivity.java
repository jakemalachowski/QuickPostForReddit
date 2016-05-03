package com.malachowski.quickpostforreddit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.malachowski.quickpostforreddit.Imgur.ImageResponse;
import com.malachowski.quickpostforreddit.Imgur.ImgurAPI;
import com.malachowski.quickpostforreddit.Imgur.Upload;
import com.malachowski.quickpostforreddit.Reddit.OAuthView;
import com.malachowski.quickpostforreddit.Reddit.Submission;
import com.malachowski.quickpostforreddit.Utils.GeneralUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

public class MainActivity extends Activity
{

    Button btSelectPic, btSubmit;
    EditText subredditET, titleET;
    ImageView thumbnail;
    String authCode;

    static ImageResponse response;
    private Upload upload;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        authCode = prefs.getString("authCode", null);
        if(authCode == null)
        {
            Intent oAuthIntent = new Intent(getApplicationContext(), OAuthView.class);
            startActivityForResult(oAuthIntent, Constants.RESULT_LOGIN);
        }

        upload = new Upload();

        btSelectPic = (Button) findViewById(R.id.selectPhotoBt);
        btSubmit = (Button) findViewById(R.id.submitBt);
        subredditET = (EditText) findViewById(R.id.subredditET);
        subredditET.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        titleET = (EditText) findViewById(R.id.titleET);
        thumbnail = (ImageView) findViewById(R.id.quickPostImage);

        subredditET.setText(prefs.getString("subreddit", ""));


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
                    if(titleET.getText().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "You must enter a title", Toast.LENGTH_LONG).show();
                        throw new NullPointerException("TitleET is empty");
                    }

                    if(subredditET.getText().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "You must enter a subreddit", Toast.LENGTH_LONG).show();
                        throw new NullPointerException("SubredditET is empty");
                    }
                    UploadService uploadService = new UploadService(upload);
                    uploadService.execute();

                } catch (NullPointerException e)
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
                String fullPath = GeneralUtils.getRealPathFromURI(this, uri);
                upload.image = new File(fullPath);

            } catch (NullPointerException e)
            {
                return;
            }

            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();

                thumbnail.setImageBitmap(GeneralUtils.resizeBitmap(this, selectedImage));

                upload.title = titleET.getText().toString();

            }
        } else if (requestCode == Constants.RESULT_LOGIN)
        {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            authCode = data.getStringExtra("authCode");
            editor.putString("authCode", authCode);
            editor.commit();

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
        return super.onOptionsItemSelected(item);
    }

    public class UploadService extends AsyncTask<Void, Void, Void>
    {
        private String title, description, albumId;
        private File image;


        public UploadService(Upload upload)
        {
            this.image = upload.image;
            this.title = upload.title;
            this.description = upload.description;
            this.albumId = upload.albumId;
        }

        ProgressDialog pDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog.setMessage("Uploading to Imgur...");
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            try
            {
                Submission submission = new Submission();
                submission.title = titleET.getText().toString();
                submission.subreddit = subredditET.getText().toString();
                submission.authCode = authCode;
                submission.imageLink = response.data.link;

                Log.d("Image Link: ", submission.imageLink);

                RedditUploadService redditUploadService = new RedditUploadService(getApplicationContext(), submission);
                redditUploadService.execute();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params)
        {
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

                Log.d("Image Link: ", response.data.link);

            } catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            return null;
        }


    }

    public class RedditUploadService extends AsyncTask<String, Void, Integer>
    {

        public String title, subreddit, imageLink, authCode, url;
        public Context context;
        public HttpResponse response;
        public String error = "none";


        ProgressDialog pDialog = new ProgressDialog(MainActivity.this);

        public RedditUploadService(Context context, Submission submission)
        {
            this.title = submission.title;
            this.subreddit = submission.subreddit;
            this.imageLink = submission.imageLink;
            this.authCode = submission.authCode;
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://oauth.reddit.com/api/submit");
            httppost.setHeader("Authorization", "bearer " + authCode);
            httppost.setHeader("User-Agent", "QuickPostForReddit/V1.0 by I_cant_speel");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("api_type", "json"));
                nameValuePairs.add(new BasicNameValuePair("kind", "link"));
                nameValuePairs.add(new BasicNameValuePair("resubmit", "true"));
                nameValuePairs.add(new BasicNameValuePair("sr", subreddit));
                nameValuePairs.add(new BasicNameValuePair("title", title));
                nameValuePairs.add(new BasicNameValuePair("url", imageLink));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                response = httpclient.execute(httppost);

                String responseStr = EntityUtils.toString(response.getEntity());

                Log.d("Reddit Response: ", responseStr);

                JSONObject json = new JSONObject(responseStr);
                json = json.getJSONObject("json");
                Log.d("JSON Error Array", json.getJSONArray("errors").toString());
                if(!json.getJSONArray("errors").toString().equals("[]"))
                {
                    error = json.getJSONArray("errors").getJSONArray(0).getString(1);
                    throw new Exception("Submission Failed");
                }
                json = json.getJSONObject("data");
                url = json.getString("url");
                Log.d("JSON URL", url);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            } catch (JSONException e)
            {
                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog.setMessage("Posting to Reddit...");
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Integer integer)
        {
            pDialog.dismiss();

            try
            {
                Log.d("Error Status", error);
                if (!error.equals("none"))
                {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                } else
                {
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("subreddit", subreddit);
                    editor.apply();
                    Uri uri = Uri.parse(url);
                    Intent launchWebLink = new Intent(Intent.ACTION_VIEW);
                    launchWebLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchWebLink.setData(uri);
                    context.startActivity(launchWebLink);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            super.onPostExecute(integer);
        }
    }

}
