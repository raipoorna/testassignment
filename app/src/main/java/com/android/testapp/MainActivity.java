package com.android.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private BookListAdapter mBookListAdapter;
    private ArrayList<Book> mBookList;
    private ContentLoadingProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookList = new ArrayList<>();
        mBookListAdapter = new BookListAdapter(mBookList);

        mProgressBar = findViewById(R.id.progress_bar);

        RecyclerView listView = findViewById(R.id.list_book);
        listView.setAdapter(mBookListAdapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = findViewById(R.id.edit_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.hint_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBookListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mBookListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalMessageReceiver,
                new IntentFilter("api-response"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchBooks(new Date());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_calendar) {
            onCalendarClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalMessageReceiver);
        super.onDestroy();
    }

    private void onCalendarClick() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        calendar.set(year, month, day);
                        Date chosenDate = calendar.getTime();
                        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, ''yy", Locale.getDefault());
                        String chosenDateStr = format.format(chosenDate);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,
                                R.style.AppTheme_AlertDialog));
                        builder.setMessage(String.format(getString(R.string.dialog_message), chosenDateStr));
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fetchBooks(chosenDate);
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(Color.parseColor("purple"));

                        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(Color.parseColor("purple"));

                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private void fetchBooks(Date date) {

        mProgressBar.setVisibility(View.VISIBLE);

        final String dateStr = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).format(date);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Constants.BASE_URL);
        urlBuilder.append(String.format("%s=%s", Constants.PUBLISHED_DATE, dateStr));
        urlBuilder.append("&");
        urlBuilder.append(String.format("%s=%s", Constants.API_KEY, Constants.API_KEY_VALUE));

        BookHandlerIntentService.fetchBooks(this, urlBuilder.toString());
    }

    private final BroadcastReceiver mLocalMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("TESTAPP", "Intent Received: " + intent);

            mProgressBar.setVisibility(View.GONE);

            ArrayList<Book> bookList = intent.getParcelableArrayListExtra("books");

            if (bookList != null) {
                mBookList.clear();
                mBookList.addAll(bookList);
                mBookListAdapter.setBookList(bookList);
                mBookListAdapter.notifyDataSetChanged();
            }
        }
    };
}