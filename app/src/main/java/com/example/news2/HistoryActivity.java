package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class HistoryActivity extends Activity {

    private ArrayList<News> historyNews = new ArrayList<>();
    private NewsAdapter mNewsAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        init();
    }

    private void init() {
        int count = getIntent().getIntExtra("count",0);
        for(int i=0;i<count;i++){
            historyNews.add((News) getIntent().getSerializableExtra("historynews"+i));
        }
        mListView = (ListView)this.findViewById(R.id.listview_history);
        mNewsAdapter = new NewsAdapter(this,historyNews);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HistoryActivity.this,NewsActivity.class);
                intent.putExtra("news",historyNews.get(i));
                startActivity(intent);
            }
        });
        mListView.setAdapter(mNewsAdapter);
    }
}
