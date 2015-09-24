package com.malachowski.quickpostforreddit.Reddit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.malachowski.quickpostforreddit.Constants;
import com.malachowski.quickpostforreddit.R;


//Class to open a webview in order to allow the user to authorize
//the app to post on behalf of the user

public class OAuthView extends Activity
{

    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_view);


        wv = (WebView) findViewById(R.id.oAuthWebView);
        wv.setWebViewClient(new WebViewClient()
        {

            //Take over when the user finishes authorizing the app and is
            //redirected to OAUTH_REDIRECT

            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url)
            {
                if (url.startsWith(Constants.OAUTH_REDIRECT))
                {
                    //replace the '#' with a '?' because it was breaking the parsing functionality
                    StringBuilder sb = new StringBuilder(url);
                    sb.replace(sb.indexOf("#"), sb.indexOf("#") + 1, "?");
                    Log.d("Check Replacement", sb.toString());

                    Uri uri = Uri.parse(sb.toString());

                    String state = uri.getQueryParameter("state");

                    if (state != null && state.equals(Constants.randString))
                    {
                        String error = uri.getQueryParameter("error");

                        if (error != null && error.length() > 0)
                        {
                            if (error.equals("access_denied"))
                            {
                                //user chose not to login
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }

                        //Go back to MainActivity with authorization code
                        Intent resultIntent = getIntent();
                        resultIntent.putExtra("authCode", uri.getQueryParameter("access_token"));
                        setResult(RESULT_OK, resultIntent);
                        finish();

                        return true;
                    }
                }
                return false;
            }
        });

        wv.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=" + Constants.CLIENT_ID + "&response_type=token&" +
                "state=" + Constants.randString + "&redirect_uri=" + Constants.OAUTH_REDIRECT + "&scope=identity,submit");
    }
}