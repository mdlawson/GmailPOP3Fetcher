package com.mdlawson.gmailpop3fetcher.refresher;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GmailRefresher {

    private static final String TAG = "Refresher";
    private static final String GMAIL = "https://mail.google.com/mail/u/0/#settings/accounts";
    private static final String DESKTOP_UA = "Mozilla/5.0 (Linux ARM) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";
    private static final String REFRESH = "javascript:Array.prototype.forEach.call(document.querySelectorAll('.rP[role=link]'),function(a){a.click();})";


    WebView webView;
    Handler handler;

    public GmailRefresher(Context context) {
        webView = new WebView(context);
        handler = new Handler(Looper.getMainLooper()); // UI thread for refreshing the webview
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(true);
        settings.setLoadsImagesAutomatically(false);
        settings.setUserAgentString(DESKTOP_UA);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String viewParam;
                if ((viewParam = uri.getQueryParameter("view")) != null && viewParam.equals("up")) {
                    Log.d(TAG, "Fetch request sent");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pause();
                        }
                    });
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    private void pause() {
        webView.onPause();
        webView.pauseTimers();
    }

    private void resume() {
        webView.resumeTimers();
        webView.onResume();
    }

    public void reload() {
        resume();
        webView.loadUrl(GMAIL);
    }

    public void refresh() {
        Log.i(TAG, "Refreshing...");
        resume();
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
