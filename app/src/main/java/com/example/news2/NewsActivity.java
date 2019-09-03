package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class NewsActivity extends Activity {

    News mNews;
    TextView title,publisher,time,text;
    ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        mNews = (News) getIntent().getSerializableExtra("news");

        Log.e("yoooooooooooo","whats up man?");

        title = (TextView) findViewById(R.id.nTitle);
        publisher = (TextView) findViewById(R.id.nPublisher);
        time = (TextView) findViewById(R.id.nTime);
        image = (ImageView) findViewById(R.id.nImage);
        text = (TextView) findViewById(R.id.nNews);

        title.setText(mNews.getTitle());
        publisher.setText(mNews.getPublisher());
        time.setText(mNews.getPublishTime());
        text.setText(mNews.getContent());
//        image.setImageResource(mNews.getImageId());
        try{
            Picasso.with(this).load(mNews.getImages()[0]).placeholder(R.mipmap.ic_launcher).into(image);
            Log.e("hiiiiiiiiii","wtffffff?"+mNews.getImages()[0]);
        }catch (Exception e){
            Log.e("yoooooooooooo","Bro?");
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = mNews.getTitle();
                if(mNews.getImages()!=null&&mNews.getImages().length>0){
                    shareBody += " "+mNews.getImages()[0];
                }
                String abs = mNews.getContent();
                shareBody += " "+(abs.length()<100?abs:abs.substring(0,100));
                String shareSubject = "Subject";
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                startActivity(Intent.createChooser(sharingIntent,"Share Using"));
            }
        });
    }

}
