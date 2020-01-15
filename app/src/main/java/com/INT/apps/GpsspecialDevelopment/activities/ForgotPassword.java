package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.INT.apps.GpsspecialDevelopment.BuildConfig;
import com.INT.apps.GpsspecialDevelopment.R;

import java.util.Locale;

import timber.log.Timber;

public class ForgotPassword extends BaseActivity {

    private static final String ROUTE = "/users/forgotPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(R.string.forgot_password);

        String currentLocale = Locale.getDefault().getISO3Language().substring(0, 2);
        String url = String.format("%s/%s%s", BuildConfig.BASE_URL, currentLocale, ROUTE);

        WebView webView = (WebView) findViewById(R.id.web_view);
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.loading), true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd.isShowing() && pd != null) {
                    pd.dismiss();
                }
                super.onPageFinished(view, url);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setSupportZoom(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.loadUrl(url);

        Timber.i(url);
    }
}
