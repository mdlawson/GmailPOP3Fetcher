package com.mdlawson.gmailpop3fetcher.refresher;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GmailRefresher {

    private static final String TAG = "Refresher";
    private static final String GMAIL = "https://mail.google.com/mail/u/0/#settings/accounts";
    private static final String DESKTOP_UA = "Mozilla/5.0 (Linux ARM) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";
    private static final String REFRESH = "javascript:Array.prototype.forEach.call(document.querySelectorAll('.rP[role=link]'),function(a){a.click();})";


    WebView webView;

    public GmailRefresher(Context context) {
        webView = new WebView(context);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString(DESKTOP_UA);
        webView.setWebViewClient(new WebViewClient());
    }

    public void reload() {
        webView.loadUrl(GMAIL);
    }

    public void refresh() {
        Log.i(TAG, "Refreshing...");
        webView.loadUrl(REFRESH);
    }
    public Fragment debugFragment() {
        return new DebugFragment();
    }

    public class DebugFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return webView;
        }
    }
}
