package com.mdlawson.gmailpop3fetcher.refresher;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import com.mdlawson.gmailpop3fetcher.preferences.Preferences;

import java.util.Date;

public class RefreshService extends Service {

    public static final String TAG = "RefresherService";
    private final IBinder binder = new RefreshBinder();
    private Date lastRefreshed = new Date();

    GmailRefresher refresher;

    @Override
    public void onCreate() {
        Log.d(TAG,"created");
        refresher = new GmailRefresher(this);
        reload();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"started");
        refresh();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class RefreshBinder extends Binder {
        public RefreshService getService() {
            return RefreshService.this;
        }
    }

    public void refresh() {
        refresher.refresh();
        Log.d(TAG, "Refreshed. Last refreshed " + DateUtils.getRelativeTimeSpanString(lastRefreshed.getTime()));
        lastRefreshed = new Date();
    }

    public void reload() {
        refresher.reload();
    }

    public Fragment webview() {
        return refresher.debugFragment();
    }

    public static void update(Context context) {
        Intent refresh = new Intent(context, RefreshService.class);
        PendingIntent refreshServiceIntent = PendingIntent.getService(context, 0, refresh, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        int interval = settings.getInt(Preferences.REFRESH_INTERVAL, 15);
        boolean start = settings.getBoolean(Preferences.AUTO_REFRESH, false);

        alarmManager.cancel(refreshServiceIntent);
        if (start) {
            Log.d(TAG,"Service started, refreshing every " + interval + " mins");
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, interval * 60 * 1000, refreshServiceIntent);
        } else {
            Log.d(TAG,"Service stopped");
        }
    }
}
