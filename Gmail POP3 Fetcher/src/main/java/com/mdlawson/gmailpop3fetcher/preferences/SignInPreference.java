package com.mdlawson.gmailpop3fetcher.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.mdlawson.gmailpop3fetcher.R;

public class SignInPreference extends DialogPreference {

    static final boolean DEFAULT = false;

    boolean mSignedIn;
    WebView webView;

    public SignInPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        webView = new WebView(getContext()) {
            @Override
            public boolean onCheckIsTextEditor() {
                return true;
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!hasFocus())
                            requestFocus();
                        break;
                }
                return super.onTouchEvent(event);
            }
        };
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.equals("https://www.google.com/settings/personalinfo")) {
                    persistBoolean(mSignedIn = true);
                    CookieSyncManager.getInstance().sync();
                    Toast.makeText(getContext(), R.string.signed_in, Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }
        });
        return webView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        webView.loadUrl("https://accounts.google.com/ServiceLoginAuth");
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistBoolean(mSignedIn);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, DEFAULT);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mSignedIn = getPersistedBoolean(DEFAULT);
        } else {
            mSignedIn = (boolean) defaultValue;
            persistBoolean(mSignedIn);
        }
    }
}
