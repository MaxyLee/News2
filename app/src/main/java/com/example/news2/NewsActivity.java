package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class NewsActivity extends Activity {

    News mNews;
    TextView title,publisher,time,text;
    ImageView image;
    VideoView video;

    ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        mNews = (News) getIntent().getSerializableExtra("news");

        Log.e("yoooooooooooo","whats up man?");

        title = findViewById(R.id.nTitle);
        publisher = findViewById(R.id.nPublisher);
        time = findViewById(R.id.nTime);
        image = findViewById(R.id.nImage);
        text = findViewById(R.id.nNews);
        video = findViewById(R.id.nVideo);
        mProgressBar = findViewById(R.id.nprogressBar);
        video.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        title.setText(mNews.getTitle());
        publisher.setText(mNews.getPublisher());
        time.setText(mNews.getPublishTime());
        text.setText(mNews.getContent());

        final String vurl = mNews.getVideo();
        if(vurl.equals("")){
            Log.d("mannnnnnnnnn","there's no video "+vurl);
            try{
                Picasso.with(this).load(mNews.getImages()[0]).placeholder(R.mipmap.logo).into(image);
                Log.e("hiiiiiiiiii","wtffffff?"+mNews.getImages()[0]);
            }catch (Exception e){
                Log.e("yoooooooooooo","Bro?");
            }
        } else {
            Log.d("mannnnnnnnnn","there's a video "+vurl);
            image.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            MediaController localMediaController = new MediaController(this);

            video.setMediaController(localMediaController);
            video.setVideoPath(vurl);
            video.requestFocus();
            video.start();

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    return true;
                }
            });
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
