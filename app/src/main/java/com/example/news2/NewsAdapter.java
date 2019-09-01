package com.example.news2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<NewsItem> newsList = new ArrayList<>();


    public NewsAdapter(Context context,ArrayList<NewsItem> newsList) {
        this.newsList = newsList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.simple_list_item_1,null);
        TextView title = (TextView) v.findViewById(R.id.title);
//        ImageView image = (ImageView) v.findViewById(R.id.image);

        title.setText(newsList.get(i).getTitle());
//        image.setImageResource(newsList.get(i).getImageId());
        return v;
    }
}
