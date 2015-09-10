package com.malachowski.quickpostforreddit.Reddit;


import com.malachowski.quickpostforreddit.Constants;
import com.malachowski.quickpostforreddit.Reddit.RedditAccessToken;

import retrofit.Callback;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Jacob on 7/21/2015.
 */
public interface RedditApiService
{

    @Headers({
            "user: " + Constants.CLIENT_ID,
            "client_secret: "
    })

    @POST("/api/v1/{access_token}")
    void getToken(@Path("access_token") String token, Callback<RedditAccessToken> cb);

}
