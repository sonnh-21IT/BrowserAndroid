package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.dbhandler.MyDBBookmarkHandler;
import com.example.myapplication.dbhandler.MyDBSiteHandler;
import com.example.myapplication.model.Website;

public class BrowserActivity extends AppCompatActivity {
    private ImageView imgClose, imgShare, imgBookMark, imgReload;
    private TextView txtTitle, txtDomain;
    private ProgressBar progressBar;
    private WebView webView;
    private MyDBSiteHandler myDBSiteHandler;
    private MyDBBookmarkHandler myDBBookmarkHandler;

    private boolean isDesktopMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initView();
        setEvent();

        myDBSiteHandler = new MyDBSiteHandler(this, null, null, 1);
        myDBBookmarkHandler = new MyDBBookmarkHandler(this, null, null, 1);

        String success = getIntent().getStringExtra("success");
        if (success != null) {
            String title = getIntent().getStringExtra("title");
            String domain = getIntent().getStringExtra("domain");
            String url = getIntent().getStringExtra("url");
            webView.loadUrl(url.trim());

            txtTitle.setText(title);
            txtDomain.setText(domain);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setEvent() {
        imgBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Website website = new Website(webView.getUrl(), webView.getTitle());
                myDBBookmarkHandler.addUrl(website);
                Toast.makeText(BrowserActivity.this, "Added a page path to favorites", Toast.LENGTH_SHORT).show();
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = "Sharing";
                String sub = webView.getUrl();
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_TEXT, sub);
                startActivity(Intent.createChooser(intent, "share using"));
            }
        });
        imgReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
    }

    private void initView() {
        imgClose = findViewById(R.id.custom_actionbar_browser_close);
        imgShare = findViewById(R.id.custom_actionbar_browser_share);
        imgBookMark = findViewById(R.id.custom_actionbar_browser_bookmark);
        imgReload = findViewById(R.id.custom_actionbar_browser_reload);

        txtTitle = findViewById(R.id.custom_actionbar_browser_title);
        txtDomain = findViewById(R.id.custom_actionbar_browser_domain);

        progressBar = findViewById(R.id.browser_progressBar);

        webView = findViewById(R.id.web_view_browser);
        webViewSetting(webView);
        webViewEventSetting(webView, progressBar);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void webViewSetting(WebView myWebView) {
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.getSettings().setBuiltInZoomControls(false);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setAllowFileAccess(true);
    }

    private void webViewEventSetting(WebView myWebView, ProgressBar myProgressBar) {
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                myProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!containsDot(myWebView.getTitle())) {
                    if (!isGoogleSearch(url) && view.getTitle() != null && !view.getTitle().isEmpty()) {
                        saveToHistory(url, view.getTitle());
                    }
                }
            }

            private boolean isGoogleSearch(String url) {
                return url != null && (url.equals("https://www.google.com/") || url.contains("https://www.google.com/search?q="));
            }

            private void saveToHistory(String url, String title) {
                Website website = new Website(url, title);
                myDBSiteHandler.addUrl(website);
            }

            private boolean containsDot(String title) {
                return title != null && title.contains(".");
            }
        });
        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);

                request.addRequestHeader("user-Agent", userAgent);
                request.setDescription("Download File...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
//                request.allowScanningByMediaScanner();
                request.setAllowedOverMetered(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(BrowserActivity.this, "Downloading File", Toast.LENGTH_SHORT).show();
            }
        });
        myProgressBar.setProgress(0);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                myProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.GONE);
                } else {
                    myWebView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void setDesktopMode(WebView webView, boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidDosString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidDosString, "x11; Linux x86_64");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(enabled);
        webView.getSettings().setLoadWithOverviewMode(enabled);
        webView.reload();
    }
}