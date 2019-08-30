package com.example.news2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        NewsItem mNews = (NewsItem) getIntent().getSerializableExtra("news");
//        Log.d("iiiiiiiiiiiiiiiii",mNews.getTitle());

        TextView title = findViewById(R.id.nTitle);
        ImageView image = findViewById(R.id.nImage);

        title.setText(mNews.getTitle());
        image.setImageResource(mNews.getImageId());
    }
}
