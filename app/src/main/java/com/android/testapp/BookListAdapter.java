package com.android.testapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListViewHolder> {
    private List<Book> mBookList;

    public BookListAdapter(List<Book> bookList) {
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

        viewHolder.title.setText(book.getTitle());
        viewHolder.author.setText(book.getAuthor());
        viewHolder.publisher.setText(book.getPublisher());
        viewHolder.contributor.setText(book.getContributor());
        viewHolder.description.setText(book.getDescription());
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
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
