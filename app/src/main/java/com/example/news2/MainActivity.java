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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MyListView.LoadListener {

    private static final int numOfCategories = 11;
    private static final String[] categories = {"Recommend", "Entertainment", "Military", "Education", "Culture", "Health", "Finance", "Sports", "Automotive", "Technology", "Society"};
    private static final String[] categoriesDB = {"recommend", "entertainment", "military", "education", "culture", "healthy", "finance", "sports", "cars", "technology", "society"};
    private static final String[] categoriesCN = {"", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    private static Integer[] index = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};

    private ViewPager vp;
    private PagerTabStrip pagerTabStrip;
    private MyAdapter mAdpter = new MyAdapter();
    private AlertDialog.Builder mBuilder;
    private NewsAdapter[] mNewsAdapters = new NewsAdapter[numOfCategories];
    private ArrayList<View> views = new ArrayList<>();
    private View[] mViews = new View[numOfCategories];
    private MyListView[] mListViews = new MyListView[numOfCategories];
    private ArrayList<News> news = new ArrayList<>();
    private ArrayList<News> staredNews = new ArrayList<>();
    private static ArrayList<News> historyNews = new ArrayList<>();
    private int[] layoutIds = new int[numOfCategories];
    private int[] listviewIds = new int[numOfCategories];
    private boolean[] selected = new boolean[numOfCategories];
    private String[] titles = new String[numOfCategories];
    private MyDatabaseHelper dbHelper;
    private ArrayList<ArrayList<News>> total = new ArrayList<>();
    private int currentId = 11;
    private String lastestNews;
    private SQLiteDatabase db;
    private Date time = new Date();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


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
                Log.d("mmm",""+total.get(i).size());
                mListViews[i].smoothScrollToPosition(mListViews[i].getCount());
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        dbHelper = new MyDatabaseHelper(this, "newsDB.db", null, 1);
        db = dbHelper.getWritableDatabase();
        //删除数据库中的记录
        //for(int j = 0; j < 11; j++)
        //    deleteDB(j);
        //删除数据库
//        dbHelper.deleteDatabase(this);
        //判断数据库是否为空，若为空，则加载数据
        isEmpty();


        firstLoadNews();

        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        init();
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
        vp = findViewById(R.id.vp);
        pagerTabStrip = findViewById(R.id.tap);
        vp.setAdapter(mAdpter);
        pagerTabStrip.setTabIndicatorColor(0xffc17b41);
        pagerTabStrip.setTextColor(0xffc17b41);

        for(int i=0;i<layout_array.length();i++){
            mListViews[i] = (MyListView) views.get(i).findViewById(listviewIds[i]);
            mListViews[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    Log.d("mmmmmmmmmmmmmmaxy",""+i);
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
                if(get.size()==0){
                    Toast.makeText(MainActivity.this, "Nothing New", Toast.LENGTH_SHORT).show();
                }else {
                mNewsAdapters[currentView].addBefore(get);
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


    private ArrayList<News> loadData() {
        ArrayList<News> lst = new ArrayList<>();
        Cursor cursor = db.query(true, categoriesDB[vp.getCurrentItem()], null, null, null, null, null, "publishTime desc", index[vp.getCurrentItem()].toString()+",10" );
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                temp.setImages();
                lst.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        index[vp.getCurrentItem()] += 10;
        return lst;
    }


    private ArrayList<News> refreshData() {
        ArrayList<News> lst = new ArrayList<>();
        String tempCategories = categoriesDB[vp.getCurrentItem()];
        lastestNews = "";
        Cursor cursor = db.query(tempCategories, new String[]{"publishTime"}, null, null, null, null, "publishTime desc", "1" );
        cursor.moveToFirst();
        final String startDate = cursor.getString(cursor.getColumnIndex("publishTime"));
        Log.e("***********", startDate);
        Log.e("***********", df.format(time));
        cursor.close();
        Thread refresh;
        refresh = new Thread(new Runnable() {
            @Override
            public void run() {
                String tempCategoriesCN = categoriesCN[vp.getCurrentItem()];
                lastestNews = result("100", startDate, df.format(time), "", tempCategoriesCN);
            }
        });
        refresh.start();
        try{
            refresh.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        //先添加到数据库中
        ContentValues values = new ContentValues();
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(lastestNews);
        JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();
        for (int i = 0; i < jsonObjects.size(); i++) {
            JsonObject jsonObject3 = jsonObjects.get(i).getAsJsonObject();
            String publishTime = jsonObject3.get("publishTime").getAsString();
            String str = jsonObject3.toString();
            cursor = db.query(categoriesDB[vp.getCurrentItem()], new String[]{"newsJson"}, "newsJson = ?", new String[]{str}, null, null, null);
            if(cursor.getCount() == 0){
                values.put("newsJson", str);
                values.put("publishTime", publishTime);
                db.insert(tempCategories, null, values);
            }
            cursor.close();
        }
        //从数据库中读取最新的10条
        index[vp.getCurrentItem()] = 10;
        cursor = db.query(true, categoriesDB[vp.getCurrentItem()], null, null, null, null, null, "publishTime desc", "10" );
        if (cursor.moveToFirst()) {
            do {
                String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                News temp = new Gson().fromJson(jsonStr, News.class);
                temp.setImages();
                lst.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lst;
    }

    private void firstLoadNews() {
        for(int i = 0; i < 11; i++){
            ArrayList<News> tempNews = new ArrayList<>();
            Cursor cursor = db.query(true, categoriesDB[i], null, null, null, null, null, "publishTime desc", "10" );
            if (cursor.moveToFirst()) {
                do {
                    String jsonStr = cursor.getString(cursor.getColumnIndex("newsJson"));
                    News temp = new Gson().fromJson(jsonStr, News.class);
                    temp.setImages();
                    tempNews.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
            total.add(tempNews);
        }
    }

    private void isEmpty(){
        Thread empty;
        empty = new Thread(new Runnable() {
            public void run(){
                for(int i = 0; i< 11;i++) {
                    Cursor cursor = db.query(categoriesDB[i], null, null, null, null, null, null);
                    int amount = cursor.getCount();
                    cursor.close();
                    if (amount == 0) {
                        addDataToDb(i);
                    }
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

    private void deleteDB(int index){
        db.delete(categoriesDB[index], null, null);
    }

    private void addDataToDb(int number) {
        String tempCategories = categoriesCN[number];
        String jsonText = result("100", "", df.format(time), "", tempCategories);
        ContentValues values = new ContentValues();
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonText);
        JsonArray jsonObjects = jsonObject.get("data").getAsJsonArray();
        for (int i = 0; i < jsonObjects.size(); i++) {
            JsonObject jsonObject3 = jsonObjects.get(i).getAsJsonObject();
            String publishTime = jsonObject3.get("publishTime").getAsString();
            String str = jsonObject3.toString();
            values.put("newsJson", str);
            values.put("publishTime", publishTime);
            db.insert(categoriesDB[number], null, values);
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

    public static void addToHistory(News news) {
        news.setVisited();
        historyNews.add(0,news);
    }
}
