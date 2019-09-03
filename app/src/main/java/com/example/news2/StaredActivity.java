package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class StaredActivity extends Activity {

    private ArrayList<News> staredNews = new ArrayList<>();
    private NewsAdapter mNewsAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stared);
        init();
    }

    private void init() {
        int count = getIntent().getIntExtra("count",0);
        for(int i=0;i<count;i++){
            staredNews.add((News) getIntent().getSerializableExtra("starednews"+i));
        }
        mListView = (ListView)this.findViewById(R.id.listview_stared);
        mNewsAdapter = new NewsAdapter(this,staredNews);
        mNewsAdapter.setStarAct();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(StaredActivity.this,NewsActivity.class);
                intent.putExtra("news",staredNews.get(i));
                startActivity(intent);
            }
        });
        mListView.setAdapter(mNewsAdapter);
    }
}
