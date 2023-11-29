package com.example.myapplication.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.dbhandler.MyDBBookmarkHandler;
import com.example.myapplication.dbhandler.MyDBSiteHandler;
import com.example.myapplication.model.Website;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private boolean isDesktopMode = false;
    TextView btnSearch, btnClearUrl, btnMic, btnMore, btnHome;
    //    ImageButton back, forward, stop, refresh, home, more;
    TextView btnTab;
    WebView myWebView;
    EditText myEditTextUrl;
    private final String MAIN_URL = "https://google.com";
    private MyDBSiteHandler myDBSiteHandler;
    private MyDBBookmarkHandler myDBBookmarkHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        myDBSiteHandler = new MyDBSiteHandler(this, null, null, 1);
        myDBBookmarkHandler = new MyDBBookmarkHandler(this, null, null, 1);

        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.toString();
            this.myWebView.loadUrl(path);
        }

        if (getIntent().getStringExtra("url") != null) {
            myWebView.loadUrl(getIntent().getStringExtra("url"));
        }
    }

    private void saveHistory() {
        if (myWebView.getUrl().equals(MAIN_URL)||myWebView.getUrl().equals("https://www.google.com/search?q=")) {
            return;
        }
        Website website = new Website(myWebView.getUrl());
        myDBSiteHandler.addUrl(website);
    }

    private void saveBookmark() {
        Website website = new Website(myWebView.getUrl());
        myDBBookmarkHandler.addUrl(website);
    }

    @Override
    public void onBackPressed() {
        if (this.myWebView.canGoBack()) {
            this.myWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private void showPopupMenu(View anchorView) {
        // Create a custom view for the popup menu
        View popupView = LayoutInflater.from(this).inflate(R.layout.custom_popup_menu_more, null);

        // Initialize the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setEvent(popupView, popupWindow);
        // Show the popup window
        popupWindow.showAsDropDown(anchorView, 0, 25);
    }

    private void setEvent(View popupView, PopupWindow popupWindow) {
        popupView.findViewById(R.id.menu_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.menu_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebView.reload();
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.menu_desktop_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDesktopMode) {
                    setDesktopMode(myWebView, false);
                    isDesktopMode = false;
                } else {
                    setDesktopMode(myWebView, true);
                    isDesktopMode = true;
                }
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.menu_book_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBookmark();
                Toast.makeText(MainActivity.this, "Page added in bookmark", Toast.LENGTH_SHORT).show();
            }
        });
        popupView.findViewById(R.id.menu_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        popupView.findViewById(R.id.menu_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(intent);
            }
        });
        popupView.findViewById(R.id.menu_scan_qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ScanQRCodeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    myEditTextUrl.setText(result.get(0));
                    String urls = myEditTextUrl.getText().toString();
                    showSoftInputFromWindow(myEditTextUrl);
                }
                break;
            }
        }
    }

    private void goForward() {
        if (this.myWebView.canGoForward()) {
            this.myWebView.goForward();
        }
    }

    private void stopLoad() {
        this.myWebView.stopLoading();
    }

    private void goHome() {
        this.myWebView.loadUrl(MAIN_URL);
    }

    private void initView() {
        this.myWebView = findViewById(R.id.webView);

        btnMic = findViewById(R.id.btnMic);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearUrl = findViewById(R.id.btnClearUrl);
        btnTab = findViewById(R.id.btnTab);
        btnMore = findViewById(R.id.btnMore);
        btnHome = findViewById(R.id.btnHome);

        this.myEditTextUrl = findViewById(R.id.edtUrl);
        this.myEditTextUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    hideSoftInputFromWindow(myEditTextUrl);
                    goSearch(myWebView, myEditTextUrl);
                    return true;
                }
                return false;
            }
        });

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webViewSetting(this.myWebView);
        this.myWebView.loadUrl(MAIN_URL);
        webViewEventSetting(this.myWebView, progressBar);

        setOnclickToolbar(btnMore);
    }

    private void setOnclickToolbar(TextView btnMore) {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myEditTextUrl.getText().toString().trim().isEmpty()) {
                    hideSoftInputFromWindow(myEditTextUrl);
                }
                goSearch(myWebView, myEditTextUrl);
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInputFromWindow(myEditTextUrl);
                showPopupMenu(btnMore);
            }
        });
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "btnTab", Toast.LENGTH_SHORT).show();
            }
        });
        btnClearUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEditTextUrl.selectAll();
                myEditTextUrl.setText("");

                showSoftInputFromWindow(myEditTextUrl);
            }
        });
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    private void goSearch(WebView webView, EditText editTextUrl) {
        if (!editTextUrl.getText().toString().isEmpty()) {
            String address = editTextUrl.getText().toString();
            boolean isLoad = address.contains(".com") || address.contains(".net") || address.contains(".in");
            if (isLoad) {
                if (address.contains("http") || address.contains("https")) {
                    webView.loadUrl(address);
                } else {
                    webView.loadUrl("http://" + address);
                }
            } else {
                webView.loadUrl("https://www.google.com/search?q=" + address.replace("http", "").replace("https", ""));
            }
        }
    }

    private void hideSoftInputFromWindow(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void showSoftInputFromWindow(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
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
                hideSoftInputFromWindow(myEditTextUrl);
                myProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                saveHistory();
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
                Toast.makeText(MainActivity.this, "Downloading File", Toast.LENGTH_SHORT).show();
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
}