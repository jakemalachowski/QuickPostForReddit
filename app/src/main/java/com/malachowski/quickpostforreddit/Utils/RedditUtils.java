package com.malachowski.quickpostforreddit.Utils;

import android.util.Log;

import com.malachowski.quickpostforreddit.Constants;
import com.malachowski.quickpostforreddit.Reddit.RedditAccessToken;
import com.malachowski.quickpostforreddit.Reddit.RedditApiService;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jacob on 7/21/2015.
 */
public class RedditUtils
{

    private RestAdapter restAdapter;
    private RedditApiService service;

    public RedditUtils()
    {
        restAdapter = new RestAdapter.Builder().setEndpoint(Constants.redditBaseURL).build();

        service = restAdapter.create(RedditApiService.class);
    }

    public void fetchToken(String token)
    {
        service.getToken(token, new Callback<RedditAccessToken>()
        {
            @Override
            public void success(RedditAccessToken redditAccessToken, Response response)
            {
                Log.d("RedditAccessToken", redditAccessToken.getAccessToken());
                Log.d("RedditAccessToken", redditAccessToken.getRefreshToken());
                Log.d("RedditAccessToken", redditAccessToken.getScope());
                Log.d("RedditAccessToken", redditAccessToken.getTokenType());
            }

            @Override
            public void failure(RetrofitError error)
            {

                Log.d("RedditAccessTokenFailed", error.toString());
            }
        });
    }
}
