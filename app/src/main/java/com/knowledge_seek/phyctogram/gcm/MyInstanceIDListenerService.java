package com.knowledge_seek.phyctogram.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by shj on 2016-03-22.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, MyRegistrationIntentService.class);
        startService(intent);
    }
}
