package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.BookmarkAdapter;
import com.example.myapplication.adapters.HistoryAdapter;
import com.example.myapplication.dbhandler.MyDBBookmarkHandler;
import com.example.myapplication.dialogs.ConfirmationDialog;
import com.example.myapplication.listeners.OnItemBookmarkClickListener;
import com.example.myapplication.model.Website;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity implements OnItemBookmarkClickListener {
    private MyDBBookmarkHandler myDBBookmarkHandler = new MyDBBookmarkHandler(this, null, null, 1);
    private RecyclerView recyclerView;
    private List<Website> bookmarks;
    private BookmarkAdapter adapter;
    private ImageView imgBack, imgClear;
    private TextView txtTitle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

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
//                ConfirmationDialog.showConfirmationDialog(Requer,"Delete all favorites","Are you sure you want to delete your entire favorites list?");
            }
        });
        txtTitle = findViewById(R.id.custom_actionbar_title_name);
        txtTitle.setText("Bookmarks");

        recyclerView = findViewById(R.id.rc_bookmark);

        setAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setAdapter() {
        bookmarks = myDBBookmarkHandler.databaseToString();
        adapter = new BookmarkAdapter(bookmarks, this);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onOpen(String url) {
        Intent intent = new Intent(BookmarkActivity.this, BrowserActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onDelete(String url) {
        myDBBookmarkHandler.deleteUrl(url);
        setAdapter();
        Toast.makeText(this, "Removed a page path from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShare(String url) {

    }
}