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
        db.execSQL("create table recommend(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table cars(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table culture(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table education(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table entertainment(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table finance(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table healthy(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table military(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table society(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table technology(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        db.execSQL("create table sports(newsJson TEXT, publishTime TEXT, visited INT DEFAULT 0 NOT NULL)");
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(NAME);
    }
}
