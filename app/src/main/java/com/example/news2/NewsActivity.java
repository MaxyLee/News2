package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class NewsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        News mNews = (News) getIntent().getSerializableExtra("news");

        TextView title = findViewById(R.id.nTitle);
        ImageView image = findViewById(R.id.nImage);
        TextView text = findViewById(R.id.nNews);

        title.setText(mNews.getTitle());
        text.setText(mNews.getContent());
//        image.setImageResource(mNews.getImageId());
        try{
            Picasso.with(this).load(mNews.getImages()[0]).placeholder(R.mipmap.ic_launcher).into(image);
            Log.e("hiiiiiiiiii","wtffffff?"+mNews.getImages()[0]);
        }catch (Exception e){
            Log.e("yoooooooooooo","Bro?");
        }

        FloatingActionButton fab = findViewById(R.id.fab_share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Body";
                String shareSubject = "Subject";
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                startActivity(Intent.createChooser(sharingIntent,"Share Using"));
            }
        });
    }
}
