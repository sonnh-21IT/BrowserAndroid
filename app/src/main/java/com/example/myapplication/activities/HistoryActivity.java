package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.dbhandler.MyDBSiteHandler;
import com.example.myapplication.model.Website;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private MyDBSiteHandler myDBSiteHandler = new MyDBSiteHandler(this, null, null, 1);
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        final List<String> histories = myDBSiteHandler.databaseToString();
        if (histories.size() > 0) {
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, histories);
            ListView listView = findViewById(R.id.list_history);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String url = histories.get(i);
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
        }
    }
}