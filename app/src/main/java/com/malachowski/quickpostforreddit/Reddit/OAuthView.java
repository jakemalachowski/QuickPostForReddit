package com.malachowski.quickpostforreddit.Reddit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.malachowski.quickpostforreddit.Constants;
import com.malachowski.quickpostforreddit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.HashMap;


public class OAuthView extends Activity
{

    WebView wv;
    WebViewClient wvclient;
    Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_view);


        wv = (WebView) findViewById(R.id.oAuthWebView);
        wv.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url)
            {
                if (url.startsWith(Constants.OAUTH_REDIRECT))
                {
                    Uri uri = Uri.parse(url);

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

                        Intent resultIntent = getIntent();
                        resultIntent.putExtra("authCode", uri.getQueryParameter("code"));

                        setResult(RESULT_OK, resultIntent);
                        finish();

                        return true;
                    }
                }
                return false;

            }
        });

        wv.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=" + Constants.CLIENT_ID + "&response_type=code&" +
                "state=" + Constants.randString + "&redirect_uri=" + Constants.OAUTH_REDIRECT + "&duration=permanent&scope=identity,submit");


    }
}