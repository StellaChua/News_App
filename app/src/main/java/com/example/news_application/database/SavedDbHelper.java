package com.example.news_application.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.news_application.entity.SavedInfo;

import java.util.ArrayList;
import java.util.List;

public class SavedDbHelper extends SQLiteOpenHelper {
    private static SavedDbHelper sHelper;
    private static final String DB_NAME = "saved.db";
    private static final int VERSION = 1;

    public SavedDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static SavedDbHelper getInstance(Context context) {
        if (null == sHelper) {
            sHelper = new SavedDbHelper(context, DB_NAME, null, VERSION);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table saved_table(saved_id integer primary key autoincrement, " +
                "newsID text," +
                "username text," +
                "news_json text" +
                ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int addSaved(String username, String newsID, String news_json, boolean isBookmarkClicked) {
        if (!isSaved(newsID) && isBookmarkClicked){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("username", username);
            values.put("newsID", newsID);
            values.put("news_json", news_json);
            String nullColumnHack = "values(null,?,?,?)";

            int insert = (int) db.insert("saved_table", nullColumnHack, values);
 //           db.close();
            return insert;
        }
        return 0;
    }

    //Determine if it is a saved item
    @SuppressLint("Range")
    public boolean isSaved(String newsID) {
        SQLiteDatabase db = getReadableDatabase();
        SavedInfo savedInfo = null;
        String sql = "select saved_id,newsID,username,news_json  from saved_table where newsID=?";
        String[] selectionArgs = {newsID};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        return cursor.moveToNext();
    }

    @SuppressLint("Range")
    public List<SavedInfo> querySavedListData(String username) {
        SQLiteDatabase db = getReadableDatabase();
        List<SavedInfo> list = new ArrayList<>();
        String sql;
        Cursor cursor;
        if (username == null){
            sql = "select saved_id,newsID,username,news_json  from saved_table";
            cursor = db.rawQuery(sql, null);
        }
        else{
            sql = "select saved_id,newsID,username,news_json  from saved_table where username=?";
            cursor = db.rawQuery(sql, new String[]{username});
        }
        while (cursor.moveToNext()) {
            int saved_id = cursor.getInt(cursor.getColumnIndex("saved_id"));
            String newsID = cursor.getString(cursor.getColumnIndex("newsID"));
            String userName = cursor.getString(cursor.getColumnIndex("username"));
            String news_json = cursor.getString(cursor.getColumnIndex("news_json"));
            list.add(new SavedInfo(saved_id, newsID, userName, news_json));
        }
        cursor.close();
        db.close();
        return list;
    }

    public int deleteSaved(String saved_id) {
        SQLiteDatabase db = getWritableDatabase();

        int delete = db.delete("saved_table", " saved_id=?", new String[]{saved_id});

        db.close();
        return delete;
    }
}