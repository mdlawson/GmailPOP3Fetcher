package com.mdlawson.gmailpop3fetcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.mdlawson.gmailpop3fetcher.refresher.RefreshService;

public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = "RefresherBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Booted!");

        RefreshService.update(context);
    }
}
