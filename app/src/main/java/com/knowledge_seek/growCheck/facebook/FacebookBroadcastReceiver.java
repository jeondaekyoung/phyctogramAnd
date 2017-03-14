package com.knowledge_seek.growCheck.facebook;

import android.os.Bundle;
import android.util.Log;

/**
 *  This is a simple example to demonstrate how an app could extend FacebookBroadcastReceiver to handle
 * notifications that long-running operations such as photo uploads have finished.
 * Created by sjw on 2015-11-27.
 */
public class FacebookBroadcastReceiver extends com.facebook.FacebookBroadcastReceiver {

    @Override
    protected void onSuccessfulAppCall(String appCallId, String action, Bundle extras) {
        //super.onSuccessfulAppCall(appCallId, action, extras);

        //A real app could update UI or notify the user that their photo was uploaded.
        Log.d("-진우-", "FacebookBroadcastReceiver " + String.format("Photo uploaded by call " + appCallId + " succeeded."));
    }

    @Override
    protected void onFailedAppCall(String appCallId, String action, Bundle extras) {
        //super.onFailedAppCall(appCallId, action, extras);

        //A real app could update UI or notify the user that their photo was not uploaded.
        Log.d("-진우- ", "FacebookBroadcastReceiver " + String.format("Photo uploaded by call " + appCallId + " failed."));
    }
}
