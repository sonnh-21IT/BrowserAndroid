package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.dbhandler.MyDBBookmarkHandler;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
    private MyDBBookmarkHandler dbBookmarkHandler = new MyDBBookmarkHandler(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        final List<String> bookmarks = dbBookmarkHandler.databaseToString();
        if (bookmarks.size() > 0) {
            ArrayAdapter myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,bookmarks);
            ListView myListview = findViewById(R.id.list_bookmarks);
            myListview.setAdapter(myAdapter);

            myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long ld) {
                    String url = bookmarks.get(position);
                    Intent intent = new Intent(BookmarkActivity.this,MainActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}