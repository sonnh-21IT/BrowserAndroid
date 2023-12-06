package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.NetworkStateReceiver;
import com.example.myapplication.R;
import com.example.myapplication.adapters.HistoryAdapter;
import com.example.myapplication.adapters.SearchResultAdapter;
import com.example.myapplication.http.GoogleCustomSearchApi;
import com.example.myapplication.listeners.OnItemSearchResultClickListener;
import com.example.myapplication.model.SearchResult;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity  implements  NetworkStateReceiver.NetworkStateReceiverListener {
    private EditText edtSearch;
    private TextView txtClean;
    private RecyclerView rc;
    RelativeLayout NointernetLayout;
    MaterialButton Reload;
    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initEvent();

        String search = getIntent().getStringExtra("search");
        if (search != null) {
            edtSearch.setText(search.trim());
        }

    }

    private void setStatusSearchBar(EditText editText, boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            edtSearch.requestFocus();
            imm.showSoftInput(editText, 0);
        } else {
            edtSearch.clearFocus();
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    private void initView() {
        edtSearch = findViewById(R.id.edt_search);
        txtClean = findViewById(R.id.txt_search_clean);
        rc = findViewById(R.id.rc_search_result);
        NointernetLayout = findViewById(R.id.NoInternetLayout);
        Reload = findViewById(R.id.reloadid);

        startNetworkBroadcastReceiver(this);

        setStatusSearchBar(edtSearch, true);
    }


    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }

    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onResume() {
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }


    private void initEvent() {
        txtClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
                setStatusSearchBar(edtSearch,true);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchString = edtSearch.getText().toString().trim();

                    if (!searchString.isEmpty()) {
                        boolean isLoad = searchString.contains(".com") || searchString.contains(".net") || searchString.contains(".in");
                        if (isLoad) {
                            if (searchString.contains("http") || searchString.contains("https")) {
//                                webView.loadUrl(address);
//                                go to activity browser with address
                            } else {
//                                webView.loadUrl("http://" + address);
//                                go to activity browser with "http://" + address
                            }
                        } else {
                            GoogleCustomSearchApi.search(searchString,10, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        List<SearchResult> list = new ArrayList<>();
                                        // Xử lý kết quả JSON
                                        JSONArray items = response.getJSONArray("items");
                                        for (int i = 0; i < items.length(); i++) {
                                            JSONObject item = items.getJSONObject(i);

                                            String title = item.getString("title");
                                            String url = item.getString("link");
                                            String snippet = item.getString("snippet");

                                            // Trích xuất thông tin hình ảnh (nếu có)
                                            JSONObject pagemap = item.optJSONObject("pagemap");
                                            String imageUrl = "";
                                            if (pagemap != null) {
                                                JSONArray cseImages = pagemap.optJSONArray("cse_image");
                                                if (cseImages != null && cseImages.length() > 0) {
                                                    JSONObject image = cseImages.getJSONObject(0);
                                                    imageUrl = image.optString("src", "N/A");
                                                }
                                            }

                                            //domain
                                            String domain = getDomain(url);

                                            SearchResult searchResult = new SearchResult();
                                            searchResult.setTitle(title);
                                            searchResult.setUrl(url);
                                            searchResult.setImgUrl(imageUrl);
                                            searchResult.setSnippet(snippet);
                                            if (domain != null) {
                                                searchResult.setDomain(domain);
                                            }
                                            list.add(searchResult);
                                            Log.d("success", searchResult.getTitle() + searchResult.getUrl() + searchResult.getImgUrl() + searchResult.getSnippet() + searchResult.getDomain());
                                        }
                                        SearchResultAdapter adapter = new SearchResultAdapter(list, new OnItemSearchResultClickListener() {
                                            @Override
                                            public void onCLick(String title, String url) {
                                                Intent intent = new Intent(SearchActivity.this, BrowserActivity.class);
                                                intent.putExtra("success","success");
                                                intent.putExtra("url",url);
                                                intent.putExtra("title",title);
                                                String domain = getDomain(url.trim());
                                                intent.putExtra("domain",domain);
                                                startActivity(intent);
                                            }
                                        });

                                        rc.setHasFixedSize(true);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                        rc.setLayoutManager(linearLayoutManager);
                                        rc.setAdapter(adapter);

                                        setStatusSearchBar(edtSearch, false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    // Xử lý khi gọi API thất bại
                                    Log.e("failure", "Status Code: " + statusCode, throwable);
                                }
                            });
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public static String getDomain(String urlString) {
        try {
            // Chuyển đổi đường dẫn URL thành đối tượng URL
            URL url = new URL(urlString);

            // Lấy host từ URL
            String host = url.getHost();

            // Kiểm tra xem host có bắt đầu bằng "www." không và loại bỏ nếu có
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            return host;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void networkAvailable() {
        rc.setVisibility(View.VISIBLE);
        NointernetLayout.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        rc.setVisibility(View.GONE);
        NointernetLayout.setVisibility(View.VISIBLE);
    }
}