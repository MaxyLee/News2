package com.example.news2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MyListView.LoadListener {

    private static final int numOfCategories = 11;
    private static final String[] categories = {"Recommend", "Entertainment", "Military", "Education", "Culture", "Health", "Finance", "Sports", "Automotive", "Technology", "Society"};

    private ViewPager vp;
    private PagerTabStrip pagerTabStrip;
    private MyAdapter mAdpter = new MyAdapter();
    private AlertDialog.Builder mBuilder;
    private NewsAdapter[] mNewsAdapters = new NewsAdapter[numOfCategories];
    private ArrayList<View> views = new ArrayList<>();
    private View[] mViews = new View[numOfCategories];
    private MyListView[] mListViews = new MyListView[numOfCategories];
    private ArrayList<NewsItem> news = new ArrayList<>();
    private int[] layoutIds = new int[numOfCategories];
    private int[] listviewIds = new int[numOfCategories];
    private boolean[] selected = new boolean[numOfCategories];
    private String[] titles = new String[numOfCategories];
    public View ftView,hdView;
    public int currentId = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListViews[0].smoothScrollToPosition(mListViews[0].getCount());
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view,null);
        hdView = li.inflate(R.layout.header_view,null);
        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        init();
        vp.setAdapter(mAdpter);
        pagerTabStrip.setTabIndicatorColor(0xffc17b41);
        pagerTabStrip.setTextColor(0xffc17b41);



    }

    private void init() {
        TypedArray layout_array = this.getResources().obtainTypedArray(R.array.layout_array);
        TypedArray listview_array = this.getResources().obtainTypedArray(R.array.listview_array);
        for(int i=0;i<numOfCategories;i++){
            selected[i] = true;
            titles[i] = categories[i];
            layoutIds[i] = layout_array.getResourceId(i,0);
            listviewIds[i] = listview_array.getResourceId(i,0);
            mViews[i] = getLayoutInflater().inflate(layoutIds[i],null);
            views.add(mViews[i]);
        }


        news.add(new NewsItem("news1",R.mipmap.ic_launcher));
        news.add(new NewsItem("news2",R.mipmap.ic_launcher));
        news.add(new NewsItem("news3",R.mipmap.ic_launcher));
        news.add(new NewsItem("news4",R.mipmap.ic_launcher));
        news.add(new NewsItem("news5",R.mipmap.ic_launcher));
        news.add(new NewsItem("news6",R.mipmap.ic_launcher));
        news.add(new NewsItem("news7",R.mipmap.ic_launcher));
        news.add(new NewsItem("news8",R.mipmap.ic_launcher));
        news.add(new NewsItem("news9",R.mipmap.ic_launcher));
        news.add(new NewsItem("news10",R.mipmap.ic_launcher));
        news.add(new NewsItem("news11",R.mipmap.ic_launcher));


        for(int i=0;i<layout_array.length();i++){
            mListViews[i] = (MyListView) views.get(i).findViewById(listviewIds[i]);
            mListViews[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    intent.putExtra("news",news.get(i));
                    startActivity(intent);
                }
            });
            mListViews[i].setInterface(this);
        }
        for(int i=0;i<numOfCategories;i++){
            mNewsAdapters[i] = new NewsAdapter(MainActivity.this,news);
            mListViews[i].setAdapter(mNewsAdapters[i]);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            search();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            changeCategory();
        } else if (id == R.id.nav_search) {
            search();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeCategory() {
        final boolean[] s = new boolean[numOfCategories];
        for(int i=0;i<numOfCategories;i++)
            s[i] = selected[i];
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Category:");
        mBuilder.setMultiChoiceItems(categories, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

            }
        });
        mBuilder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateView();
            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(int j=0;j<numOfCategories;j++)
                    selected[j] = s[j];
            }
        });
        mBuilder.show();
    }

    private void updateView() {
        views.clear();
        int cnt = 0;
        for(int i=0;i<numOfCategories;i++){
            if(selected[i]){
                views.add(mViews[i]);
                titles[cnt++] = categories[i];
            }
        }
        mAdpter.notifyDataSetChanged();
    }

    private void search() {
        Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);
        startActivity(searchIntent);
    }

    @Override
    public void PullLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<NewsItem> get = refreshData();
                mNewsAdapters[0].addBefore(get);
                mListViews[0].loadComplete();
            }
        },1000);
    }

    @Override
    public void onLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<NewsItem> get = loadData();
                mNewsAdapters[0].addNewsItems(get);
                mListViews[0].loadComplete();
            }
        },1000);
    }


    class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = views.get(position);
            container.addView(v);
            return v;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


    private ArrayList<NewsItem> loadData() {
        ArrayList<NewsItem> lst = new ArrayList<>();
        lst.add(new NewsItem("news"+(++currentId),R.mipmap.ic_launcher));
        lst.add(new NewsItem("news"+(++currentId),R.mipmap.ic_launcher));
        lst.add(new NewsItem("news"+(++currentId),R.mipmap.ic_launcher));
        return lst;
    }

    private ArrayList<NewsItem> refreshData() {
        ArrayList<NewsItem> lst = new ArrayList<>();
        lst.add(new NewsItem("newsRefreshed",R.mipmap.ic_launcher));
        return lst;
    }

}
