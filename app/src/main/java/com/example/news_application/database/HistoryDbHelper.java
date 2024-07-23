package com.example.news_application.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.news_application.entity.HistoryInfo;

import java.util.ArrayList;
import java.util.List;

public class HistoryDbHelper extends SQLiteOpenHelper {
    private static HistoryDbHelper sHelper;
    private static final String DB_NAME = "history.db";
    private static final int VERSION = 1;

    public HistoryDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static HistoryDbHelper getInstance(Context context) {
        if (null == sHelper) {
            sHelper = new HistoryDbHelper(context, DB_NAME, null, VERSION);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table history_table(history_id integer primary key autoincrement, " +
                "newsID text," +
                "username text," +
                "news_json text" +
                ")");

        db.execSQL("ALTER TABLE history_table ADD COLUMN is_viewed integer");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int addHistory(String username, String newsID, String news_json) {
        if (!isHistory(newsID)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("username", username);
            values.put("newsID", newsID);
            values.put("news_json", news_json);
            String nullColumnHack = "values(null,?,?,?)";

            int insert = (int) db.insert("history_table", nullColumnHack, values);
            db.close();
            return insert;
        }
        return 0;
    }

    //Determine if it is a history
    @SuppressLint("Range")
    public boolean isHistory(String newsID) {
        SQLiteDatabase db = getReadableDatabase();
        HistoryInfo userInfo = null;
        String sql = "select history_id,newsID,username,news_json  from history_table where newsID=?";
        String[] selectionArgs = {newsID};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        return cursor.moveToNext();
    }

    @SuppressLint("Range")
    public List<HistoryInfo> queryHistoryListData(String username) {
        SQLiteDatabase db = getReadableDatabase();
        List<HistoryInfo> list = new ArrayList<>();
        String sql;
        Cursor cursor;
        if (username == null){
            sql = "select history_id,newsID,username,news_json  from history_table";
            cursor = db.rawQuery(sql, null);
        }
        else{
            sql = "select history_id,newsID,username,news_json  from history_table where username=?";
            cursor = db.rawQuery(sql, new String[]{username});
        }
        while (cursor.moveToNext()) {
            int history_id = cursor.getInt(cursor.getColumnIndex("history_id"));
            String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
            String userName = cursor.getString(cursor.getColumnIndex("username"));
            String news_json = cursor.getString(cursor.getColumnIndex("news_json"));
            list.add(new HistoryInfo(history_id, newsID, userName, news_json));
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean isNewsViewed(String newsID) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select is_viewed from history_table where newsID=?";
        String[] selectionArgs = {newsID};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        boolean isViewed = false;
        if (cursor.moveToFirst()) {
            int isViewedColumnIndex = cursor.getColumnIndex("is_viewed");
            if (isViewedColumnIndex >= 0) {
                isViewed = cursor.getInt(isViewedColumnIndex) == 1;
            }
        }
        cursor.close();
        return isViewed;
    }


    public void setNewsViewed(String newsID, boolean viewed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_viewed", viewed ? 1 : 0);
        db.update("history_table", values, "newsID" + "=?", new String[]{newsID});
    }
}
