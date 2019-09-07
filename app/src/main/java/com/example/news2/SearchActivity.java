package com.example.news2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class SearchActivity extends Activity {

    private SearchView mSearchView;
    private AutoCompleteTextView mAutoCompleteTextView;//搜索输入框
    private ImageView mDeleteButton;//搜索框中的删除按钮
    private ListView mListView;
    private TextView[] histories = new TextView[4];
    private NewsAdapter mNewsAdapter;
    private ArrayList<News> mNews = new ArrayList<>();
    private String searchNews;
    private Date time = new Date();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LinearLayout searchhistory_view;
    private ArrayList<String> searchHistory = new ArrayList<>();
    private LinearLayout[] his = new LinearLayout[4];
    private int[] historyIds = new int[4];
    private int[] historyviewIds = new int[4];
    private MyDatabaseHelper dbHelper;
    private static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        dbHelper = new MyDatabaseHelper(this, "newsDB.db", null, 1);
        db = dbHelper.getWritableDatabase();

        initSearchHistory();

        initView();
        initData();
        setListener();
    }

    private void initView(){
        mSearchView=(SearchView) findViewById(R.id.view_search);
        mAutoCompleteTextView=mSearchView.findViewById(R.id.search_src_text);
        mDeleteButton=mSearchView.findViewById(R.id.search_close_btn);
        mListView = findViewById(R.id.listview_search);
        mNewsAdapter = new NewsAdapter(this,mNews);
        mListView.setAdapter(mNewsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this,NewsActivity.class);
                News tNews = mNews.get(i);
                MainActivity.addToHistory(tNews);
                intent.putExtra("news",tNews);
                startActivity(intent);
            }
        });

        searchhistory_view = findViewById(R.id.history_view);
        searchhistory_view.setVisibility(View.GONE);
        if(searchHistory.size()>0){
            TypedArray searchhistory_array = this.getResources().obtainTypedArray(R.array.searchhistory_array);
            TypedArray historyview_array = this.getResources().obtainTypedArray(R.array.historyview_array);
            for(int i=0;i<4;i++){
                historyIds[i] = searchhistory_array.getResourceId(i,0);
                historyviewIds[i] = historyview_array.getResourceId(i,0);
                histories[i] = findViewById(historyIds[i]);
                his[i] = findViewById(historyviewIds[i]);
                his[i].setVisibility(View.INVISIBLE);
            }
            for(int i=0;i<searchHistory.size();i++){
                histories[i].setText(searchHistory.get(i));
                his[i].setVisibility(View.VISIBLE);
            }
            searchhistory_view.setVisibility(View.VISIBLE);
        }
    }

    private void initData(){
        mSearchView.setIconifiedByDefault(false);//设置搜索图标是否显示在搜索框内
        //1:回车
        //2:前往
        //3:搜索
        //4:发送
        //5:下一項
        //6:完成
        mSearchView.setImeOptions(2);//设置输入法搜索选项字段，默认是搜索，可以是：下一页、发送、完成等
//        mSearchView.setInputType(1);//设置输入类型
//        mSearchView.setMaxWidth(200);//设置最大宽度
        mSearchView.setQueryHint("Search here");//设置查询提示字符串
//        mSearchView.setSubmitButtonEnabled(true);//设置是否显示搜索框展开时的提交按钮
        //设置SearchView下划线透明
        setUnderLinetransparent(mSearchView);
    }

    private void setListener(){

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(searchHistory.size()>=4){
                    searchHistory.remove(3);
                }
                searchHistory.add(0,query);
                searchhistory_view.setVisibility(View.GONE);
                ArrayList<News> tmp = Search(query);
                mNews.clear();
                if(tmp.size()>0){
                    mNews.addAll(tmp);
                }
                mNewsAdapter.notifyDataSetChanged();
                return false;
            }

            //当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("bbb","=====newText="+newText);
                return false;
            }
        });
    }

    /**设置SearchView下划线透明**/
    private void setUnderLinetransparent(SearchView searchView){
        try {
            Class<?> argClass = searchView.getClass();
            // mSearchPlate是SearchView父布局的名字
            Field ownField = argClass.getDeclaredField("mSearchPlate");
            ownField.setAccessible(true);
            View mView = (View) ownField.get(searchView);
            mView.setBackgroundColor(Color.TRANSPARENT);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initSearchHistory(){
        Cursor cursor = db.query("searched", null, null, null, null, null, "id desc");
        if(cursor.moveToFirst()){
            do{
                String str = cursor.getString(cursor.getColumnIndex("searchHistory"));
                searchHistory.add(str);
                if(searchHistory.size()==4)
                    break;
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    private ArrayList<News> Search(final String str) {
        Cursor cursor = db.query("searched", null, "searchHistory=?", new String[]{str}, null, null, null);
        if(cursor.getCount() != 0){
            db.delete("searched","searchHistory=?", new String[]{str});
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("searchHistory", str);
        db.insert("searched", null, values);

        ArrayList<News> lst = new ArrayList<>();
        Thread search;
        search = new Thread(new Runnable() {
            @Override
            public void run() {
                searchNews = result("50", "", df.format(time), str, "");
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
            lst.add(temp);
        }
        return lst;
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
