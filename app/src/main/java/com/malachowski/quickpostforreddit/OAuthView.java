package com.malachowski.quickpostforreddit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.HashMap;


public class OAuthView extends Activity {

    WebView wv;
    WebViewClient wvclient;
    Activity mActivity;
    public static final String CLIENT_ID = "mJeA6das-uB1iw";
    public static final String OAUTH_REDIRECT = "oauth://quickpostforreddit.malachowski.com";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_view);

        wv = (WebView) findViewById(R.id.oAuthWebView);
        wv.loadUrl();

    }

/*    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (url.startsWith(RedditAccountManager.REDIRECT_URI)) {
            Uri uri = Uri.parse(url);

            String state = uri.getQueryParameter("state");

            if (state != null && state.equals(randomString)) {
                String error = uri.getQueryParameter("error");

                if (error != null && error.length() > 0) {
                    if (error.equals("access_denied")) {
                        //user choose not to login
                        finish();
                    }
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("authCode", uri.getQueryParameter("code"));
                setResult(RESULT_OK, resultIntent);
                finish();

                return true;
            }
        }

        return false;
    }
};
*/


class LoginTask extends AsyncTask<Uri, String, Boolean> {
        Exception exception;
        boolean loginSuccess = false;

        @Override
        protected void onPreExecute() {
            wv.setVisibility(View.INVISIBLE);
        }

        protected Boolean doInBackground(Uri... uris) {


            return true;
        }

        @Override
        protected void onProgressUpdate(String... statusText){

        }

        protected void onPostExecute(Boolean success) {

        }
    }

    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            wv.stopLoading();
            wv.loadData("", "text/html", "utf-8");
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


