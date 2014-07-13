package com.mdlawson.gmailpop3fetcher;

import android.app.Activity;
import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import com.mdlawson.gmailpop3fetcher.preferences.Preferences;
import com.mdlawson.gmailpop3fetcher.refresher.RefreshService;


public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {

    public static final String TAG = "POP3RefresherActivity";

    Intent refresh;
    RefreshService refreshService;
    Menu menu;

    boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refresh = new Intent(this, RefreshService.class);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(refresh, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Bound to RefreshService");
                refreshService = ((RefreshService.RefreshBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                refreshService = null;
            }
        }, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.menu = menu;

        onSignInStateChange(settings.getBoolean(Preferences.SIGNED_IN, false));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (refreshService != null ) refreshService.refresh();
                return true;
            case R.id.action_debug_webview:
                debug = !debug;
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, debug ? refreshService.webview() : new SettingsFragment())
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Preferences.SIGNED_IN:
                onSignInStateChange(sharedPreferences.getBoolean(key, false));
                break;
            case Preferences.AUTO_REFRESH:
            case Preferences.REFRESH_INTERVAL:
                RefreshService.update(this);
        }
    }
    public void onSignInStateChange(boolean signedIn) {
        Log.d(TAG,"Sign in state changed, now: " + signedIn);
        if (signedIn && refreshService != null) {
            Log.d(TAG, "Reloading service page");
            refreshService.reload();
        }
        menu.findItem(R.id.action_refresh).setVisible(signedIn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
