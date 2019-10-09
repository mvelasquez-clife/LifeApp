package com.mvelasquezp.clife.clientes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.mvelasquezp.clife.clientes.R;

public class HTMLViewer extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_htmlviewer);
        webView = (WebView) findViewById(R.id.webView);
        if (getIntent().hasExtra("html")) {
            String html = getIntent().getStringExtra("html");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
            return;
        }
        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
            return;
        }
    }
}
