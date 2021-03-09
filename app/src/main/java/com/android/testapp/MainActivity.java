package com.android.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookListAdapter mBookListAdapter;
    private List<Book> mBookList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookList = new ArrayList<>();
        mBookListAdapter  = new BookListAdapter(mBookList);

        RecyclerView listView = findViewById(R.id.list_book);
        listView.setAdapter(mBookListAdapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listView.setLayoutManager(new LinearLayoutManager(this));



    }
}