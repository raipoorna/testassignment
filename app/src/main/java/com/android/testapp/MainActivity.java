package com.android.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalMessageReceiver,
                new IntentFilter("api-response"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchBooks(new Date());
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalMessageReceiver);
        super.onDestroy();
    }

    private void fetchBooks(Date date){

        final String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Constants.BASE_URL);
        urlBuilder.append(String.format("%s=%s", Constants.PUBLISHED_DATE,  dateStr));
        urlBuilder.append("&");
        urlBuilder.append(String.format("%s=%s",Constants.API_KEY, Constants.API_KEY_VALUE));

        BookHandlerIntentService.fetchBooks(this, urlBuilder.toString());
    }

    private BroadcastReceiver mLocalMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("TESTAPP", "Intent Received: "+intent);
            mBookList.clear();
            mBookList.addAll(intent.getParcelableArrayListExtra("books"));
            mBookListAdapter.notifyDataSetChanged();
        }
    };
}