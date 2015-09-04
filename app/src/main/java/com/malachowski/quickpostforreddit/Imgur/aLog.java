package com.malachowski.quickpostforreddit.Imgur;

import android.util.Log;

import com.malachowski.quickpostforreddit.Constants;

/**
 * Created by Jacob on 3/17/2015.
 */

public class aLog {
    public static void w (String TAG, String msg){
        if(Constants.LOGGING) {
            if (TAG != null && msg != null)
                Log.w(TAG, msg);
        }
    }

}
