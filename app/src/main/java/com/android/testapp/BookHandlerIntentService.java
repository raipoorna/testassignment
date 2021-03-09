package com.android.testapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class BookHandlerIntentService extends IntentService {

    public BookHandlerIntentService() {
        super("BookHandlerIntentService");
    }

    public static void fetchBooks(Context context, String url) {
        Intent intent = new Intent(context, BookHandlerIntentService.class);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        URL url;
        HttpsURLConnection urlConnection = null;
        try {

            final String urlStr = intent.getStringExtra("url");

            Log.v("TESTAPP", "Sending request to : "+urlStr);

            url = new URL(urlStr);

            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream responseInputStream = urlConnection.getInputStream();
            final String jsonStr = convertInputStreamToString(responseInputStream);

            Log.v("TESTAPP", "JSON Response: "+jsonStr);

            ArrayList<Book> bookList = parseJson(jsonStr);

            Log.v("TESTAPP", "Book List : "+bookList);

            Intent localIntent = new Intent("api-response");
            localIntent.putParcelableArrayListExtra("books", bookList);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        inputStream.close();
        return sb.toString();
    }

    private ArrayList<Book> parseJson(String jsonStr) throws JSONException {
        ArrayList<Book> mBooks = new ArrayList<>();
        JSONObject parent = new JSONObject(jsonStr);

        if(parent.has(Constants.JSON_KEY_RESULTS)){
            JSONObject results = parent.getJSONObject(Constants.JSON_KEY_RESULTS);
            if(results.has(Constants.JSON_KEY_LISTS)){
                JSONArray lists = results.getJSONArray(Constants.JSON_KEY_LISTS);

                for(int i = 0; i<lists.length(); i++){
                    JSONObject list = lists.getJSONObject(i);
                    JSONArray books = list.getJSONArray(Constants.JSON_KEY_BOOKS);

                    for(int j = 0; j < books.length(); j++){
                        JSONObject bookJson = books.getJSONObject(j);

                        Book book = new Book();
                        book.setTitle(bookJson.optString(Constants.JSON_KEY_TITLE));
                        book.setAuthor(bookJson.optString(Constants.JSON_KEY_AUTHOR));
                        book.setPublisher(bookJson.optString(Constants.JSON_KEY_PUBLISHER));
                        book.setContributor(bookJson.optString(Constants.JSON_KEY_CONTRIBUTOR));
                        book.setDescription(bookJson.optString(Constants.JSON_KEY_DESCRIPTION));

                        mBooks.add(book);
                    }
                }
            }
        }

        return mBooks;
    }


}