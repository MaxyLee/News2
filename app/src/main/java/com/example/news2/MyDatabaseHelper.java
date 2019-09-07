package com.example.news2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String NAME;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
        this.NAME = name;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table news(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL, newsID STRING, category STRING)");
        db.execSQL("create table updateNews(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL, newsID STRING, category STRING)");
        db.execSQL("create table staredID(newsID STRING, id INTEGER PRIMARY KEY AUTOINCREMENT)");
        db.execSQL("create table visitedID(newsID STRING, id INTEGER PRIMARY KEY AUTOINCREMENT)");
        db.execSQL("create table searched(searchHistory STRING, id INTEGER PRIMARY KEY AUTOINCREMENT)");

        Toast.makeText(mContext, "Database create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(NAME);
    }
}
