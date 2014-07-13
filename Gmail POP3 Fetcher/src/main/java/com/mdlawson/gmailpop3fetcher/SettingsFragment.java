package com.mdlawson.gmailpop3fetcher;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.webkit.CookieManager;
import com.mdlawson.gmailpop3fetcher.preferences.Preferences;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference(Preferences.SIGN_OUT).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CookieManager.getInstance().removeAllCookie();
                getPreferenceManager().getSharedPreferences().edit().putBoolean(Preferences.SIGNED_IN, false).commit();
                return true;
            }
        });
    }
}
