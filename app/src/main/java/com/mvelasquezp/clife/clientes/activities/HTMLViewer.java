package com.mvelasquezp.clife.clientes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;

public class HTMLViewer extends AppCompatActivity implements View.OnClickListener {

    WebView webView;
    ImageButton HtmlViewerClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_htmlviewer);
        webView = (WebView) findViewById(R.id.webView);
        HtmlViewerClose = (ImageButton) findViewById(R.id.HtmlViewerClose);
        HtmlViewerClose.setOnClickListener(HTMLViewer.this);
        if (getIntent().hasExtra("button")) {
            HtmlViewerClose.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onClick(View view) {
        finish();
    }
}
