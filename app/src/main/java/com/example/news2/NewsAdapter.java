package com.example.news2;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<News> mNewsList;
    private Context context;
    private boolean isStarAct = false;

    public NewsAdapter(Context context, ArrayList<News> newsList) {
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
    public View getView(final int i, final View convertView, ViewGroup parent) {
        final int rank = i;
        View v = mInflater.inflate(R.layout.simple_list_item_1, null);
        TextView time = v.findViewById(R.id.time);
        TextView title = (TextView) v.findViewById(R.id.title);
        final ImageView image = (ImageView) v.findViewById(R.id.image);
        TextView text = v.findViewById(R.id.text);
        final ImageButton star = v.findViewById(R.id.btn_star);
        final News mNews = mNewsList.get(i);


        time.setText(mNews.getPublishTime());
        title.setText(mNews.getTitle());
        text.setText(mNews.getContent().replaceAll("\\s", ""));
        if (mNews.getStared()) {
            star.setImageDrawable(context.getDrawable(R.drawable.star_yellow));
        } else {
            star.setImageDrawable(context.getDrawable(R.drawable.star_border));
        }
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNews.getStared()) {
                    star.setImageDrawable(context.getDrawable(R.drawable.star_border));
                    MainActivity.removeFromStared(mNews);
                    if (isStarAct) {
                        mNewsList.remove(mNews);
                    }
                    mNews.setStared(false);
                } else {
                    star.setImageDrawable(context.getDrawable(R.drawable.star_yellow));
                    MainActivity.addToStared(mNews);
                    mNews.setStared(true);
                }
                notifyDataSetChanged();
            }
        });

        if (mNewsList.get(i).getImages() != null) {
            if (mNewsList.get(i).getImages().length > 0) {
                try {
                    Picasso.with(this.context).load(mNewsList.get(i).getImages()[0]).placeholder(R.mipmap.logo3).into(image);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("yoooooooooooo", "Man?");
                }
            }
        }
        if (mNews.getVisited()) {
            text.setTextColor(Color.parseColor("#969696"));
        }

        return v;
    }

    public void addNewsItems(ArrayList<News> newsList) {
        mNewsList.addAll(newsList);
        this.notifyDataSetChanged();
    }

    public void addBefore(ArrayList<News> newsList) {
        mNewsList.addAll(0, newsList);
        this.notifyDataSetChanged();
    }

    public void setStarAct() {
        isStarAct = true;
    }
}

