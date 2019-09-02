package com.example.news2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<News> mNewsList = new ArrayList<>();


    public NewsAdapter(Context context,ArrayList<News> newsList) {
        this.mNewsList = newsList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mNewsList.size();
    }

    @Override
    public Object getItem(int i) {
        return mNewsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.simple_list_item_1,null);
        TextView title = (TextView) v.findViewById(R.id.title);
        ImageView image = (ImageView) v.findViewById(R.id.image);

        title.setText(mNewsList.get(i).getTitle());
//        image.setImageResource(mNewsList.get(i).getImageId());
        return v;
    }

    public void addNewsItems(ArrayList<News> newsList) {
        mNewsList.addAll(newsList);
        this.notifyDataSetChanged();
    }

    public void addBefore(ArrayList<News> newsList) {
        mNewsList.addAll(0,newsList);
        this.notifyDataSetChanged();
    }
}
