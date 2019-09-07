package com.example.news2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MyListView.LoadListener {

    private static final int numOfCategories = 11;
    private static final String[] categories = {"Latest News", "Entertainment", "Military", "Education", "Culture", "Health", "Finance", "Sports", "Automotive", "Technology", "Society"};
    //    private static final String[] categoriesDB = {"recommend", "entertainment", "military", "education", "culture", "healthy", "finance", "sports", "cars", "technology", "society"};
    private static final String[] categoriesCN = {"", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    private static Map<String,Integer> cate = new HashMap<>();
    private static Integer lastestIndex = 10;

    private static ViewPager vp;
    private static NewsAdapter[] mNewsAdapters = new NewsAdapter[numOfCategories];
    private static MyListView[] mListViews = new MyListView[numOfCategories];
    private static ArrayList<News> staredNews = new ArrayList<>();
    private static ArrayList<News> historyNews = new ArrayList<>();
    private static SQLiteDatabase db;
    private PagerTabStrip pagerTabStrip;
    private MyAdapter mAdapter;
    private AlertDialog.Builder mBuilder;
    private ArrayList<View> views = new ArrayList<>();
    private View[] mViews = new View[numOfCategories];
    private int[] layoutIds = new int[numOfCategories];
    private int[] listviewIds = new int[numOfCategories];
    private boolean[] selected = new boolean[numOfCategories];
    private String[] titles = new String[numOfCategories];
    private MyDatabaseHelper dbHelper;
    private ArrayList<ArrayList<News>> total = new ArrayList<>();
    private String lastestNews;
    private Date time = new Date();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean night = false;
    private boolean[] flag = new boolean[11];



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
                mListViews[i].smoothScrollToPosition(mListViews[i].getCount());
//                mListViews[i].invalidate();
//                mNewsAdapters[i].updateView();
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        dbHelper = new MyDatabaseHelper(this, "newsDB.db", null, 1);
        //删除数据库
//        dbHelper.deleteDatabase(this);
        db = dbHelper.getWritableDatabase();
//        删除数据库中的记录
        db.delete("news", null, null);
        db.delete("updateNews", null, null);
        db.delete("staredID", null, null);
        db.delete("visitedID", null, null);

        //判断数据库是否为空，若为空，则加载数据
        isEmpty();
        initVisited();
        addNewsToHistory();
        addNewsToStared();
        firstLoadNews();

        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        init();
        if(night){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

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
        mAdapter = new MyAdapter(views);
        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        pagerTabStrip.setTabIndicatorColor(this.getColor(R.color.colorFirstDate));
        pagerTabStrip.setTextColor(this.getColor(R.color.colorFirstDate));
        vp.setAdapter(mAdapter);

        for(int i=1;i<numOfCategories;i++){
            cate.put(categoriesCN[i],i);
        }

        for(int i=0;i<layout_array.length();i++){
            mListViews[i] = (MyListView) views.get(i).findViewById(listviewIds[i]);
            mListViews[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("mmmmmmmmmmmmmmaxy",""+i);
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    News tNews = total.get(vp.getCurrentItem()).get(i-1);
                    addToHistory(tNews);
                    intent.putExtra("news",tNews);
                    startActivity(intent);
                    mNewsAdapters[vp.getCurrentItem()].notifyDataSetChanged();
                }
            });
            mListViews[i].setInterface(this);
        }
        for(int i=0;i<numOfCategories;i++){
            mNewsAdapters[i] = new NewsAdapter(MainActivity.this,total.get(i));
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
        } else if (id == R.id.nav_stared) {
            Stared();
        } else if (id == R.id.nav_history) {
            History();
        } else if(id == R.id.nav_night) {
            Log.e("nnnnnnnnnight",""+night);
            night = !night;
            recreate();
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
//        views.clear();
        int cnt = 0;
        for(int i=0;i<numOfCategories;i++){
            if(selected[i]){
//                views.add(mViews[i]);
                titles[cnt++] = categories[i];
                if(!views.contains(mViews[i])){
                    views.add(mViews[i]);
                }
            } else {
                if(views.contains(mViews[i])){
                    views.remove(mViews[i]);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void search() {
        Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);
        startActivity(searchIntent);
    }

    private void Stared() {
        Intent intent = new Intent(MainActivity.this,StaredActivity.class);
        int cnt = staredNews.size();
        intent.putExtra("count",cnt);
        for(int i=0;i<cnt;i++){
            intent.putExtra("starednews"+i,staredNews.get(i));
        }
        startActivity(intent);
    }

    private void History() {
        Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
        int cnt = historyNews.size();
        intent.putExtra("count",cnt);
        for(int i=0;i<cnt;i++){
            intent.putExtra("historynews"+i,historyNews.get(i));
        }
        startActivity(intent);
    }



    @Override
    public void PullLoad() {
        final int currentView = vp.getCurrentItem();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<News> get = refreshData();
                if(get==null){
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }else if(get.size()==0){
                    Toast.makeText(MainActivity.this, "Nothing New", Toast.LENGTH_SHORT).show();
                }else {
                    mNewsAdapters[currentView].addBefore(get);
                    Toast.makeText(MainActivity.this, get.size()+"piece(s) of news refreshed", Toast.LENGTH_SHORT).show();
                }
                mListViews[currentView].loadComplete();
            }
        },1000);
    }

    @Override
    public void onLoad() {
        final int currentView = vp.getCurrentItem();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<News> get = loadData();
                mNewsAdapters[currentView].addNewsItems(get);
                mListViews[currentView].loadComplete();
            }
        },1000);
    }


    class MyAdapter extends PagerAdapter {

        private ArrayList<View> views = new ArrayList<>();

        public MyAdapter(ArrayList<View> views) {
            this.views = views;
        }

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
            container.removeView((View)object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = views.get(position);
            Log.d("lllllllll", "instantiateItem: "+position);
//            int absPosition = -1;
//            int cnt = 0;
//            for(int i=0;i<numOfCategories;i++){
//                if(selected[i]){
//                    cnt++;
//                }
//                absPosition++;
//                if(cnt==position)
//                    break;
//            }
//            container.removeView(mViews[absPosition]);
            container.addView(v);
            return v;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }


    private ArrayList<News> loadData() {
        ArrayList<News> lst = new ArrayList<>();
        if(vp.getCurrentItem() == 0){
            Cursor cursor = db.query(true, "news", null, "visited=?", new String[]{"0"}, null, null, "publishTime desc", lastestIndex.toString()+", 10");
            if (cursor.moveToFirst()) {
                do {
                    String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                    News temp = new Gson().fromJson(jsonStr, News.class);
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    lst.add(temp);
                } while (cursor.moveToNext());
            }
            lastestIndex += 10;
            cursor.close();
            return lst;
        }else{
            Cursor cursor = db.query(true, "news", null, "visited=?", new String[]{"0"}, null, null, "publishTime desc", null);
            if (cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    if(lst.size() <= 10){
                        if(category.equals(categoriesCN[vp.getCurrentItem()])){
                            String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                            News temp = new Gson().fromJson(jsonStr, News.class);
                            Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                            if (cursor1.getCount() != 0)
                                temp.setVisited();
                            cursor1.close();
                            cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                            if (cursor1.getCount() != 0)
                                temp.setStared(true);
                            cursor1.close();
                            temp.setImages();
                            lst.add(temp);
                            //修改visited的值
                            String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                            ContentValues values = new ContentValues();
                            values.put("visited", 1);
                            db.update("news", values, "newsID=?", new String[]{newsID});
                        }
                    }else
                        break;
                } while (cursor.moveToNext());
            }
            cursor.close();
            return lst;
        }

    }


    private ArrayList<News> refreshData() {
        ArrayList<News> lst = new ArrayList<>();
        lastestNews = "";
        Cursor cursor = db.query("news", new String[]{"publishTime"}, null, null, null, null, "publishTime desc", "1" );
        cursor.moveToFirst();
        final String startDate = cursor.getString(cursor.getColumnIndex("publishTime"));
        Log.e("***********", startDate);
        Log.e("***********", df.format(time));
        cursor.close();
        cursor = db.query("updateNews", null, null, null, null, null, null);
        if(cursor.getCount() == 0){
            Thread refresh;
            refresh = new Thread(new Runnable() {
                @Override
                public void run() {
                    lastestNews = result("200", startDate, df.format(time), "", "");
                }
            });
            refresh.start();
            try{
                refresh.join(6000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            if(lastestNews.equals("")){
                return null;
            }

            ContentValues values = new ContentValues();
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(lastestNews);
            JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();
            int totalNews = jsonObject.get("total").getAsInt();
            Log.e("******************", totalNews+"");
            for (int i = 0; i < jsonObjects.size(); i++) {
                JsonObject jsonObject3 = jsonObjects.get(i).getAsJsonObject();
                String publishTime = jsonObject3.get("publishTime").getAsString();
                String newsID = jsonObject3.get("newsID").getAsString();
                Cursor cursor1 = db.query("news", null, "newsID=?", new String[]{newsID}, null, null, null);
                if(cursor1.getCount() != 0){
                    cursor1.close();
                    break;
                }
                cursor1.close();
                String category = jsonObject3.get("category").getAsString();
                String str = jsonObject3.toString();
                values.put("newsJson", str);
                values.put("publishTime", publishTime);
                values.put("newsID", newsID);
                values.put("category", category);
                db.insert("updateNews", null, values);
            }
        }
        cursor.close();
        if(flag[vp.getCurrentItem()])
            return lst;
        else{
            flag[vp.getCurrentItem()] = true;
            if(vp.getCurrentItem() == 0){
                cursor = db.query("updateNews", null, null, null, null, null, "publishTime desc");
                if(cursor.moveToFirst()){
                    do{
                        String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                        News temp = new Gson().fromJson(jsonStr, News.class);
                        Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                        if(cursor1.getCount() != 0)
                            temp.setVisited();
                        cursor1.close();
                        cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                        if (cursor1.getCount() != 0)
                            temp.setStared(true);
                        cursor1.close();
                        temp.setImages();
                        lst.add(temp);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                return lst;
            }else{
                cursor = db.query("updateNews", null, "category=?", new String[]{categoriesCN[vp.getCurrentItem()]}, null, null, "publishTime desc");
                if(cursor.moveToFirst()){
                    do{
                        String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                        News temp = new Gson().fromJson(jsonStr, News.class);
                        Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                        if(cursor1.getCount() != 0)
                            temp.setVisited();
                        cursor1.close();
                        cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                        if (cursor1.getCount() != 0)
                            temp.setStared(true);
                        cursor1.close();
                        temp.setImages();
                        lst.add(temp);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                return lst;
            }
        }
    }

    private void firstLoadNews() {
        ArrayList<News> lastest = new ArrayList<>();
        ArrayList<News> entertainment = new ArrayList<>();
        ArrayList<News> military = new ArrayList<>();
        ArrayList<News> education = new ArrayList<>();
        ArrayList<News> culture = new ArrayList<>();
        ArrayList<News> healthy = new ArrayList<>();
        ArrayList<News> finance = new ArrayList<>();
        ArrayList<News> sports = new ArrayList<>();
        ArrayList<News> cars = new ArrayList<>();
        ArrayList<News> technology = new ArrayList<>();
        ArrayList<News> society = new ArrayList<>();
        Cursor cursor = db.query(true, "news", null, "visited=?", new String[]{"0"}, null, null, "publishTime desc", null );
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex("category"));
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                ArrayList<Keywords> keywords = temp.getKeywords();
                if(lastest.size() <= 10){
                    Collections.sort(keywords);
                    Log.e("Keywords", keywords.size()+"");
                    for(int j = 0; j < keywords.size(); j++)
                        Log.e("Keywords", keywords.get(j).getScore()+" "+keywords.get(j).getWord());
                    Log.e("***************", "-------------------------------------------");
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    lastest.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }
                if(entertainment.size() <= 10 && category.equals("娱乐")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    entertainment.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(military.size() <= 10 && category.equals("军事")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    military.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(education.size() <= 10 && category.equals("教育")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    education.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(culture.size() <= 10 && category.equals("文化")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    culture.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(healthy.size() <= 10 && category.equals("健康")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    healthy.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }
                else if(finance.size() <= 10 && category.equals("财经")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    finance.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(sports.size() <= 10 && category.equals("体育")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    sports.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(cars.size() <= 10 && category.equals("汽车")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    cars.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(technology.size() <= 10 && category.equals("科技")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    technology.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }else if(society.size() <= 10 && category.equals("社会")){
                    Cursor cursor1 = db.query("visitedID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if(cursor1.getCount() != 0)
                        temp.setVisited();
                    cursor1.close();
                    cursor1 = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{temp.getNewsID()}, null, null, null);
                    if (cursor1.getCount() != 0)
                        temp.setStared(true);
                    cursor1.close();
                    temp.setImages();
                    society.add(temp);
                    //修改visited的值
                    String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
                    ContentValues values = new ContentValues();
                    values.put("visited", 1);
                    db.update("news", values, "newsID=?", new String[]{newsID});
                }
                int count = lastest.size()+entertainment.size()+military.size()+education.size()+culture.size()+healthy.size()+finance.size()+sports.size()+cars.size()+technology.size()+society.size();
                if(count == 100)
                    break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        total.add(lastest);
        total.add(entertainment);
        total.add(military);
        total.add(education);
        total.add(culture);
        total.add(healthy);
        total.add(finance);
        total.add(sports);
        total.add(cars);
        total.add(technology);
        total.add(society);
    }

    private void isEmpty(){
        Thread empty;
        empty = new Thread(new Runnable() {
            public void run(){
                Cursor cursor = db.query("news", null, null, null, null, null, null);
                int amount = cursor.getCount();
                cursor.close();
                if (amount == 0) {
                    addDataToDb();
                }
            }
        });
        empty.start();
        try{
            empty.join();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addDataToDb() {
        //先不取最新时间 df.format(time)
        String jsonText = result("300", "", "2019-09-07", "", "");
        Log.e("***************", jsonText);
        ContentValues values = new ContentValues();
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonText);
        JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();

        for (int i = 0; i < jsonObjects.size(); i++) {
            JsonObject jsonObject3 = jsonObjects.get(i).getAsJsonObject();
            String publishTime = jsonObject3.get("publishTime").getAsString();
            String newsID = jsonObject3.get("newsID").getAsString();
            String category = jsonObject3.get("category").getAsString();
            String str = jsonObject3.toString();
            values.put("newsJson", str);
            values.put("publishTime", publishTime);
            values.put("newsID", newsID);
            values.put("category", category);
            db.insert("news", null, values);
        }
        Log.e("****************", "Done!");
    }

    private void initVisited(){
        ContentValues values = new ContentValues();
        values.put("visited", 0);
        db.update("news", values, null, null);
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
        Map<String, String> params = new LinkedHashMap<>();
        params.put("size", size);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("words", words);
        params.put("categories", categories);
        return httpRequest(requestUrl, params);
    }

    public String urlEncode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            sb.append(i.getKey()).append("=").append(URLEncoder.encode((String) i.getValue())).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public void addNewsToStared(){
        Cursor cursor = db.query("staredID", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            do {
                Log.e("***********************", cursor.getString(cursor.getColumnIndex("newsID")));
                Cursor cursor1 = db.query("news", null, "newsID = ?", new String[]{cursor.getString(cursor.getColumnIndex("newsID"))}, null, null, null);
                if(cursor1.getCount() != 0){
                    cursor1.moveToFirst();
                    String jsonStr = cursor1.getString(cursor1.getColumnIndex("newsJson"));
                    News temp = new Gson().fromJson(jsonStr, News.class);
                    temp.setImages();
                    Log.e("***********************", temp.getTitle());
                    temp.setStared(true);
                    staredNews.add(temp);
                }
                cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void addNewsToHistory(){
        Cursor cursor = db.query("visitedID", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            do {
                Log.e("***********************", cursor.getString(cursor.getColumnIndex("newsID")));
                Cursor cursor1 = db.query("news", null, "newsID=?", new String[]{cursor.getString(cursor.getColumnIndex("newsID"))}, null, null, null);
                if(cursor1.getCount() != 0){
                    cursor1.moveToFirst();
                    String jsonStr = cursor1.getString(cursor1.getColumnIndex("newsJson"));
                    News temp = new Gson().fromJson(jsonStr, News.class);
                    Log.e("***********************", temp.getTitle());
                    temp.setImages();
                    temp.setVisited();
                    historyNews.add(temp);
                }
                cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static void addToHistory(News news) {
        news.setVisited();
        ContentValues values = new ContentValues();
        Log.e("***************", news.getNewsID());
        values.put("newsID", news.getNewsID());
        db.insert("visitedID", null, values);
        historyNews.add(0,news);
    }

    public static void addToStared(News news) {
        staredNews.add(0,news);
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("staredID", new String[]{"newsID"}, "newsID = ?", new String[]{news.getNewsID()}, null, null, null);
        if(cursor.getCount() == 0){
            Log.e("***************", news.getNewsID());
            values.put("newsID", news.getNewsID());
            db.insert("staredID", null, values);
        }
        cursor.close();
        int i = vp.getCurrentItem();
        int i2 = cate.get(news.getCategory());
        mNewsAdapters[i].notifyDataSetChanged();
        mNewsAdapters[i2].notifyDataSetChanged();
    }

    public  static  void removeFromStared(News news) {
//        staredNews.get(rank).setStared(false);
//        staredNews.remove(rank);
        db.delete("staredID", "newsID=?", new String[]{news.getNewsID()});
        for(News n:staredNews){
            if(n.getNewsID().equals(news.getNewsID())){
                n.setStared(false);
                staredNews.remove(n);
                break;
            }
        }
        int i = vp.getCurrentItem();
        mNewsAdapters[i].notifyDataSetChanged();
    }

    public static boolean getNight() {
        return night;
    }
}

