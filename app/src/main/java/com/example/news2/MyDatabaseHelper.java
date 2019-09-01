package com.example.news2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table recommend(newsJson text)");
        db.execSQL("create table cars(newsJson text)");
        db.execSQL("create table culture(newsJson text)");
        db.execSQL("create table education(newsJson text)");
        db.execSQL("create table entertainment(newsJson text)");
        db.execSQL("create table finance(newsJson text)");
        db.execSQL("create table healthy(newsJson text)");
        db.execSQL("create table military(newsJson text)");
        db.execSQL("create table society(newsJson text)");
        db.execSQL("create table technology(newsJson text)");
        db.execSQL("create table sports(newsJson text)");
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
