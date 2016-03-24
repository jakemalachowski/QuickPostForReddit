package com.malachowski.quickpostforreddit;

import com.malachowski.quickpostforreddit.Utils.SessionIdentifierGenerator;

/**
 * Created by Jacob on 3/17/2015.
 */

public class Constants
{
    /*
      Logging flag
     */
    public static final boolean LOGGING = false;

    /*
      Your imgur client id. You need this to upload to imgur.

      More here: https://api.imgur.com/
     */
    public static final String MY_IMGUR_CLIENT_ID = "3c7c7df5232ecac";

    /*
      Client Auth
     */

    //Imgur Constants
    public static String getClientAuth()
    {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

    public static String uploadURL = "https://api.imgur.com/3/image";

    //Reddit Constants
    public static String OAUTH_REDIRECT = "http://www.quickpostforreddit.com";
    public static final String CLIENT_ID = "mJeA6das-uB1iw";
    public static final String randString = new SessionIdentifierGenerator().nextSessionId(); //generate random code for Reddit oAuth
    public static final String redditBaseURL = "https://oauth.reddit.com";

    //Intent Codes
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_LOGIN = 2;

}


