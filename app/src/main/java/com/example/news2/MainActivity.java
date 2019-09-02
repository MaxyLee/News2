package com.example.news2;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.*;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MyListView.LoadListener {

    private static final int numOfCategories = 11;
    private static final String[] categories = {"Recommend", "Entertainment", "Military", "Education", "Culture", "Health", "Finance", "Sports", "Automotive", "Technology", "Society"};
    private static final String[] categoriesCN = {"", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};

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
    private MyDatabaseHelper dbHelper;
    private ArrayList<News> recommend = new ArrayList<>();
    private ArrayList<News> entertainment = new ArrayList<>();
    private ArrayList<News> military = new ArrayList<>();
    private ArrayList<News> education = new ArrayList<>();
    private ArrayList<News> culture = new ArrayList<>();
    private ArrayList<News> healthy = new ArrayList<>();
    private ArrayList<News> finance = new ArrayList<>();
    private ArrayList<News> sports = new ArrayList<>();
    private ArrayList<News> cars = new ArrayList<>();
    private ArrayList<News> technology = new ArrayList<>();
    private ArrayList<News> society = new ArrayList<>();
    private int currentId = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteStudioService.instance().start(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = vp.getCurrentItem();
                Log.d("mmm", "" + i);
//                mListViews[0].smoothScrollToPosition(mListViews[0].getCount());
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        dbHelper = new MyDatabaseHelper(this, "newsDB.db", null, 1);
        loadNews();

        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        init();
        vp.setAdapter(mAdpter);
        pagerTabStrip.setTabIndicatorColor(0xffc17b41);
        pagerTabStrip.setTextColor(0xffc17b41);
        NewsAdapter[] mNewsAdapters = new NewsAdapter[numOfCategories];
        for (int i = 0; i < numOfCategories; i++) {
            mNewsAdapters[i] = new NewsAdapter(MainActivity.this, news);
            mListViews[i].setAdapter(mNewsAdapters[i]);
        }

    }

    private void loadNews() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("recommend", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                recommend.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("cars", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                cars.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("entertainment", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                entertainment.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("military", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                military.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("education", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                education.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("culture", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                culture.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("healthy", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                healthy.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("finance", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                finance.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("sports", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                sports.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("technology", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                technology.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.query("society", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                society.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addDataToDb() {
        String jsonText = result("100", "", "2019-09-01", "", "科技");
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonText);
            JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();
            for (int i = 0; i < jsonObjects.size(); i++) {
                JsonObject jsonObject3 = jsonObjects.get(i).getAsJsonObject();
                String str = jsonObject3.toString();
                values.put("newsJson", str);
                db.insert("technology", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("****************", "Done!");
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

    private void init() {
        TypedArray layout_array = this.getResources().obtainTypedArray(R.array.layout_array);
        TypedArray listview_array = this.getResources().obtainTypedArray(R.array.listview_array);
        for (int i = 0; i < numOfCategories; i++) {
            selected[i] = true;
            titles[i] = categories[i];
            layoutIds[i] = layout_array.getResourceId(i, 0);
            listviewIds[i] = listview_array.getResourceId(i, 0);
            mViews[i] = getLayoutInflater().inflate(layoutIds[i], null);
            views.add(mViews[i]);
        }


        news.add(new NewsItem("news1", R.mipmap.ic_launcher));
        news.add(new NewsItem("news2", R.mipmap.ic_launcher));
        news.add(new NewsItem("news3", R.mipmap.ic_launcher));
        news.add(new NewsItem("news4", R.mipmap.ic_launcher));
        news.add(new NewsItem("news5", R.mipmap.ic_launcher));
        news.add(new NewsItem("news6", R.mipmap.ic_launcher));
        news.add(new NewsItem("news7", R.mipmap.ic_launcher));
        news.add(new NewsItem("news8", R.mipmap.ic_launcher));
        news.add(new NewsItem("news9", R.mipmap.ic_launcher));
        news.add(new NewsItem("news10", R.mipmap.ic_launcher));
        news.add(new NewsItem("news11", R.mipmap.ic_launcher));


        for (int i = 0; i < layout_array.length(); i++) {
            mListViews[i] = (MyListView) views.get(i).findViewById(listviewIds[i]);
            mListViews[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("news", news.get(i));
                    startActivity(intent);
                }
            });
            mListViews[i].setInterface(this);
        }
        for (int i = 0; i < numOfCategories; i++) {
            mNewsAdapters[i] = new NewsAdapter(MainActivity.this, news);
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
        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeCategory() {
        final boolean[] s = new boolean[numOfCategories];
        for (int i = 0; i < numOfCategories; i++)
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
                for (int j = 0; j < numOfCategories; j++)
                    selected[j] = s[j];
            }
        });
        mBuilder.show();
    }

    private void updateView() {
        views.clear();
        int cnt = 0;
        for (int i = 0; i < numOfCategories; i++) {
            if (selected[i]) {
                views.add(mViews[i]);
                titles[cnt++] = categories[i];
            }
        }
        mAdpter.notifyDataSetChanged();
    }

    private void search() {
        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(searchIntent);
    }

    @Override
    public void PullLoad() {
        final int currentView = vp.getCurrentItem();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<NewsItem> get = refreshData();
                mNewsAdapters[currentView].addBefore(get);
                mListViews[currentView].loadComplete();
            }
        }, 1000);
    }

    @Override
    public void onLoad() {
        final int currentView = vp.getCurrentItem();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<NewsItem> get = loadData();
                mNewsAdapters[currentView].addNewsItems(get);
                mListViews[currentView].loadComplete();
            }
        }, 1000);
    }


    class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
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
        lst.add(new NewsItem("news" + (++currentId), R.mipmap.ic_launcher));
        lst.add(new NewsItem("news" + (++currentId), R.mipmap.ic_launcher));
        lst.add(new NewsItem("news" + (++currentId), R.mipmap.ic_launcher));
        return lst;
    }


    private ArrayList<News> refreshData() {
        ArrayList<News> lst = new ArrayList<>();
        String tempCategories = categoriesCN[vp.getCurrentItem()];
        LocalDate date = LocalDate.now();
        String jsonText = result("10", "", date.toString(), "", tempCategories);
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonText);
            JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();
            for (int i = 0; i < jsonObjects.size(); i++) {
                JsonObject jsonObject3 = jsonObjects.get(i).getAsJsonObject();
                String str = jsonObject3.toString();
                News temp = new Gson().fromJson(str, News.class);
                lst.add(temp);
                values.put("newsJson", str);
                db.insert(tempCategories, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("****************", "Done!");

        return lst;
    }

}