package com.example.ebookreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EbookAdapter extends ArrayAdapter<Ebook> {
    public EbookAdapter(Context context, ArrayList<Ebook> ebooks){
        super(context, 0, ebooks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.ebook_tile, parent, false);
        }

        Ebook ebook = getItem(position);
        ImageView cover = view.findViewById(R.id.book_cover_image);
        TextView title = view.findViewById(R.id.book_title_text);

        //add stuff to change cover image
        title.setText(ebook.getTitle());

        return view;
    }
}
