package com.example.news2;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<News> mNewsList = new ArrayList<>();
    private Context context;

    public NewsAdapter(Context context,ArrayList<News> newsList) {
        this.mNewsList = newsList;
        this.context = context;
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
        TextView time = v.findViewById(R.id.time);
        TextView title = (TextView) v.findViewById(R.id.title);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        TextView text = v.findViewById(R.id.text);


        time.setText(mNewsList.get(i).getPublishTime());
        title.setText(mNewsList.get(i).getTitle());
        text.setText(mNewsList.get(i).getContent().replaceAll(" ",""));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if(mNewsList.get(i).getImages()!=null){
            try{
                Log.d("heyyyyyyyyyyy","what's wrong?"+(mNewsList.get(i).getImages()[0]));
                Picasso.with(this.context).load(mNewsList.get(i).getImages()[0]).placeholder(R.mipmap.ic_launcher).into(image);
            }catch (Exception e){
                Log.e("yoooooooooooo","Man?");
            }
        }

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
