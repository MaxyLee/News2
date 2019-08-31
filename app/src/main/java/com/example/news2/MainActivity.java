package com.example.news2;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int numOfCategories = 11;

    private ViewPager vp;
    private PagerTabStrip pagerTabStrip;
    private MyAdapter mAdpter = new MyAdapter();
    private ArrayList<View> views = new ArrayList<>();
    private View[] mViews = new View[numOfCategories];
    private String[] categories = {"Recommend", "Entertainment", "Military", "Education", "Culture", "Health", "Finance", "Sports", "Automotive", "Technology", "Society"};
    private  ListView[] mListViews = new ListView[numOfCategories];
    private ArrayList<NewsItem> news = new ArrayList<>();
    private int[] layoutIds = new int[numOfCategories];
    private int[] listviewIds = new int[numOfCategories];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        initView();
        vp.setAdapter(mAdpter);
        pagerTabStrip.setTabIndicatorColor(0xffc17b41);
        pagerTabStrip.setTextColor(0xffc17b41);
        NewsAdapter[] mNewsAdapters = new NewsAdapter[numOfCategories];
        for(int i=0;i<numOfCategories;i++){
            mNewsAdapters[i] = new NewsAdapter(MainActivity.this,news);
            mListViews[i].setAdapter(mNewsAdapters[i]);
        }
    }

    private void initView() {
        TypedArray layout_array = this.getResources().obtainTypedArray(R.array.layout_array);
        TypedArray listview_array = this.getResources().obtainTypedArray(R.array.listview_array);
        for(int i=0;i<numOfCategories;i++){
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
            mListViews[i] = (ListView) views.get(i).findViewById(listviewIds[i]);
            mListViews[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    intent.putExtra("news",news.get(i));
                    startActivity(intent);
                }
            });
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
        if (id == R.id.action_settings) {
            return true;
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeCategory() {
        views.remove(0);
        mAdpter.notifyDataSetChanged();
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
            return categories[position];
        }

    }
}
