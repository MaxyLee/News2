package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsActivity extends Activity {

    News mNews;
    ArrayList<News> recNews;
    TextView title,publisher,time,text;
    ImageView image;
    VideoView video;
    CardView[] cards = new CardView[3];
    TextView[] rectime = new TextView[3];
    TextView[] rectitle = new TextView[3];
    TextView[] rectext = new TextView[3];
    ImageView[] recimage = new ImageView[3];
    ImageButton[] recstar = new ImageButton[3];
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

        cards[0] = findViewById(R.id.rec1);
        cards[1] = findViewById(R.id.rec2);
        cards[2] = findViewById(R.id.rec3);
        rectime[0] = findViewById(R.id.time1);
        rectime[1] = findViewById(R.id.time2);
        rectime[2] = findViewById(R.id.time3);
        rectitle[0] = findViewById(R.id.title1);
        rectitle[1] = findViewById(R.id.title2);
        rectitle[2] = findViewById(R.id.title3);
        rectext[0] = findViewById(R.id.text1);
        rectext[1] = findViewById(R.id.text2);
        rectext[2] = findViewById(R.id.text3);
        recimage[0] = findViewById(R.id.image1);
        recimage[1] = findViewById(R.id.image2);
        recimage[2] = findViewById(R.id.image3);
        recstar[0] = findViewById(R.id.btn_star1);
        recstar[1] = findViewById(R.id.btn_star2);
        recstar[2] = findViewById(R.id.btn_star3);

        for(int i=0;i<3;i++){
            cards[i].setVisibility(View.GONE);
        }

//        recNews.add(mNews);
        for(int i=0;i<recNews.size();i++){
            rectime[i].setText(recNews.get(i).getPublishTime());
            rectitle[i].setText(recNews.get(i).getTitle());
            rectext[i].setText(recNews.get(i).getContent());
            if (recNews.get(i).getImages() != null) {
                if (recNews.get(i).getImages().length > 0) {
                    try {
                        Picasso.with(this).load(recNews.get(i).getImages()[0]).placeholder(R.mipmap.logo3).into(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("yoooooooooooo", "Man?");
                    }
                }
            }
            recstar[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNews.getStared()) {
                        recstar[i].setImageDrawable(getDrawable(R.drawable.star_border));
                        MainActivity.removeFromStared(mNews);
                        mNews.setStared(false);
                    } else {
                        recstar[i].setImageDrawable(getDrawable(R.drawable.star_yellow));
                        MainActivity.addToStared(mNews);
                        mNews.setStared(true);
                    }
                }
            });
            if (recNews.get(i).getVisited()) {
                rectext[i].setTextColor(Color.parseColor("#969696"));
            }
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
