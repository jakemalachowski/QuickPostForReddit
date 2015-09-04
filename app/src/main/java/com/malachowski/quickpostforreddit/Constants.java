package com.malachowski.quickpostforreddit;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by Jacob on 3/17/2015.
 */

public class Constants {
    /*
      Logging flag
     */
    public static final boolean LOGGING = true;

    /*
      Your imgur client id. You need this to upload to imgur.

      More here: https://api.imgur.com/
     */
    public static final String MY_IMGUR_CLIENT_ID = "3c7c7df5232ecac";

    /*
      Redirect URL for android.
     */
    public static final String MY_IMGUR_REDIRECT_URL = "http://android";

    /*
      Client Auth
     */
    public static String getClientAuth(){
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

    public static String uploadURL = "https://api.imgur.com/3/image";


}


