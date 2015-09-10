package com.malachowski.quickpostforreddit.Reddit;

/**
 * Created by Jacob on 7/16/2015.
 */
public class OAuthTokenRetriever
{

    String oAuthCode = null;
    String token = null;

    OAuthTokenRetriever(String oAuthCode)
    {
        this.oAuthCode = oAuthCode;
    }

    String getToken()
    {
        if (token.equals(null))
        {
            //First time retrieving the token


        }

        return null; //temp placeholder
    }
}
