package com.android.testapp;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListViewHolder> implements Filterable {
    private ArrayList<Book> mBookList, mUnfilteredBookList;

    public BookListAdapter(ArrayList<Book> bookList) {
        mUnfilteredBookList = bookList;

        mBookList = bookList;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list, null);

        return new BookListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder viewHolder, int position) {
        Book book = mBookList.get(position);

        viewHolder.title.setText("Title: " + book.getTitle());
        viewHolder.author.setText("Author: " + book.getAuthor());
        viewHolder.publisher.setText("Publisher: " + book.getPublisher());
        viewHolder.contributor.setText("Contributor: " + book.getContributor());
        viewHolder.description.setText("Description: " + book.getDescription());
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<Book> filteredBookList = new ArrayList<>();

                Log.v("TESTAPP", "Searching for: "+charSequence);

                if (TextUtils.isEmpty(charSequence)) {
                    filteredBookList.addAll(mUnfilteredBookList);
                } else {
                    final String searchTerm = charSequence.toString().toLowerCase();

                    for (Book book : mUnfilteredBookList) {
                        if (book.getAuthor().toLowerCase().contains(searchTerm) ||
                                book.getAuthor().toLowerCase().contains(searchTerm) ||
                                book.getPublisher().toLowerCase().contains(searchTerm) ||
                                book.getContributor().toLowerCase().contains(searchTerm) ||
                                book.getDescription().toLowerCase().contains(searchTerm)) {

                                filteredBookList.add(book);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredBookList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ArrayList<Book> filteredBookList = (ArrayList<Book>) filterResults.values;

                Log.v("TESTAPP", "Searching Results: "+filteredBookList.size());

                mBookList.clear();
                mBookList.addAll(filteredBookList);
                notifyDataSetChanged();
            }
        };
    }


    class BookListViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView author;
        protected TextView publisher;
        protected TextView contributor;
        protected TextView description;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            publisher = itemView.findViewById(R.id.publisher);
            contributor = itemView.findViewById(R.id.contributor);
            description = itemView.findViewById(R.id.description);
        }
    }
}
