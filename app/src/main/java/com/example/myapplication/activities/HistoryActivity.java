package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.HistoryAdapter;
import com.example.myapplication.dbhandler.MyDBBookmarkHandler;
import com.example.myapplication.dbhandler.MyDBSiteHandler;
import com.example.myapplication.listeners.OnItemHistoryClickListener;
import com.example.myapplication.model.Website;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements OnItemHistoryClickListener {
    private MyDBSiteHandler myDBSiteHandler = new MyDBSiteHandler(this, null, null, 1);
    private RecyclerView recyclerView;
    private List<Website> histories;
    private HistoryAdapter adapter;
    private ImageView imgBack,imgClear;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
    }

    private void initView() {
        imgBack = findViewById(R.id.custom_actionbar_title_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgClear = findViewById(R.id.custom_actionbar_title_clear);
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDBSiteHandler.clearHistory();
                setAdapter();
                Toast.makeText(HistoryActivity.this, "Deleted all history", Toast.LENGTH_SHORT).show();
            }
        });
        txtTitle = findViewById(R.id.custom_actionbar_title_name);
        txtTitle.setText("Histories");
        recyclerView = findViewById(R.id.rc_history);
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setAdapter() {
        histories = myDBSiteHandler.databaseToString();
        adapter = new HistoryAdapter(histories, this);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onOpen(String url) {
        Intent intent = new Intent(HistoryActivity.this, BrowserActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onDelete(String url) {
        myDBSiteHandler.deleteUrl(url);
        setAdapter();
        Toast.makeText(this, "Removed a page path from history", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShare(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String body = "Sharing";
        String sub = url;
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_TEXT, sub);
        startActivity(Intent.createChooser(intent, "share using"));
    }

    @Override
    public void onBookmark(String url, String title) {
        MyDBBookmarkHandler myDBBookmarkHandler = new MyDBBookmarkHandler(this, null, null, 1);
        Website website = new Website(url, url);
        myDBBookmarkHandler.addUrl(website);
        Toast.makeText(HistoryActivity.this, "Added a page path to favorites", Toast.LENGTH_SHORT).show();
    }
}