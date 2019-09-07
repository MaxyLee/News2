package com.example.news2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class NewsActivity extends Activity {

    News mNews;
    ArrayList<Keywords> keywords = new ArrayList<>();
    ArrayList<News> recNews = new ArrayList<>();
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
    String newsID;
    private Date Time = new Date();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String searchNews = "";
    String searchKey = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        mNews = (News) getIntent().getSerializableExtra("news");

        Log.e("yoooooooooooo","whats up man?");
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

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

        recstar[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNews.getStared()) {
                    recstar[0].setImageDrawable(getDrawable(R.drawable.star_border));
                    MainActivity.removeFromStared(mNews);
                    mNews.setStared(false);
                } else {
                    recstar[0].setImageDrawable(getDrawable(R.drawable.star_yellow));
                    MainActivity.addToStared(mNews);
                    mNews.setStared(true);
                }
            }
        });

        recstar[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNews.getStared()) {
                    recstar[1].setImageDrawable(getDrawable(R.drawable.star_border));
                    MainActivity.removeFromStared(mNews);
                    mNews.setStared(false);
                } else {
                    recstar[1].setImageDrawable(getDrawable(R.drawable.star_yellow));
                    MainActivity.addToStared(mNews);
                    mNews.setStared(true);
                }
            }
        });

        recstar[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNews.getStared()) {
                    recstar[2].setImageDrawable(getDrawable(R.drawable.star_border));
                    MainActivity.removeFromStared(mNews);
                    mNews.setStared(false);
                } else {
                    recstar[2].setImageDrawable(getDrawable(R.drawable.star_yellow));
                    MainActivity.addToStared(mNews);
                    mNews.setStared(true);
                }
            }
        });

        newsID = mNews.getNewsID();
        keywords = mNews.getKeywords();
        Collections.sort(keywords);

        if(keywords.size() >= 1){
            searchKey = keywords.get(0).getWord();
            Thread search;
            search = new Thread(new Runnable() {
                @Override
                public void run() {
                    searchNews = result("15", "", df.format(Time), searchKey, "");
                }
            });
            search.start();
            try{
                search.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(searchNews);
            JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();
            for(int i = 0; i < jsonObjects.size(); i++){
                News temp = new Gson().fromJson(jsonObjects.get(i).toString(), News.class);
                temp.setImages();
                if(temp.getNewsID().equals(newsID))
                    continue;
                recNews.add(temp);
                if(recNews.size() == 3)
                    break;
            }
        }
        Log.e("*********", searchKey);


        cards[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this,NewsActivity.class);
                MainActivity.addToHistory(recNews.get(0));
                intent.putExtra("news",recNews.get(0));
                startActivity(intent);
                rectext[0].setTextColor(Color.parseColor("#969696"));
            }
        });

        cards[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this,NewsActivity.class);
                MainActivity.addToHistory(recNews.get(1));
                intent.putExtra("news",recNews.get(1));
                startActivity(intent);
                rectext[1].setTextColor(Color.parseColor("#969696"));
            }
        });

        cards[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this,NewsActivity.class);
                MainActivity.addToHistory(recNews.get(2));
                intent.putExtra("news",recNews.get(2));
                startActivity(intent);
                rectext[2].setTextColor(Color.parseColor("#969696"));
            }
        });

        for(int i=0;i<recNews.size();i++){
            cards[i].setVisibility(View.VISIBLE);
            rectime[i].setText(recNews.get(i).getPublishTime());
            rectitle[i].setText(recNews.get(i).getTitle());
            rectext[i].setText(recNews.get(i).getContent());
            if (recNews.get(i).getImages() != null) {
                if (recNews.get(i).getImages().length > 0) {
                    try {
                        Picasso.with(this).load(recNews.get(i).getImages()[0]).placeholder(R.mipmap.logo3).into(recimage[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("yoooooooooooo", "Man?");
                    }
                }
            }
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
    public String httpRequest(String requestUrl, Map params) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(requestUrl + "?" + urlEncode(params));
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public String result(String size, String startDate, String endDate, String words, String categories) {
        String requestUrl = "https://api2.newsminer.net/svc/news/queryNewsList";
        Map params = new LinkedHashMap();
        params.put("size", size);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("words", words);
        params.put("categories", categories);
        String str = httpRequest(requestUrl, params);
        return str;
    }

    public String urlEncode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            sb.append(i.getKey()).append("=").append(URLEncoder.encode((String) i.getValue())).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
}
