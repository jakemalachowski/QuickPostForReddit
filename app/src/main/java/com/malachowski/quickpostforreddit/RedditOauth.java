package com.malachowski.quickpostforreddit;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jacob on 6/17/2015.
 */
public class RedditOauth {

    public static final String CLIENT_ID = "mJeA6das-uB1iw";

    static String ANDROID_ID = null;

    public RedditOauth(String androidID)
    {
        ANDROID_ID = androidID;
    }

    public void main() throws IOException {



            class retrieveAuthentication extends AsyncTask<Void, Void, Void> {


                @Override
                protected Void doInBackground(Void... params) {
                    String url = "https://www.reddit.com/api/v1/access_token";
                    URL obj = null;
                    try {
                        obj = new URL(url);

                        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                        con.setRequestMethod("POST");
                        con.setRequestProperty("grant_type", "https://oauth.reddit.com/grants/installed_client&\\");
                        con.setRequestProperty("device_id", ANDROID_ID);
                        con.setRequestProperty("user", CLIENT_ID);

                        con.setDoOutput(true);
                        int responseCode = con.getResponseCode();

                        Log.d("Response Code: ", String.valueOf(responseCode));

                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while((inputLine=in.readLine())!=null)

                        {
                            response.append(inputLine);
                        }

                        Log.d("Reddit oAuth Response",response.toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }


            }

    }


}
