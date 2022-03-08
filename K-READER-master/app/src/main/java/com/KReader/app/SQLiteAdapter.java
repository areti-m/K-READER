package com.KReader.app;

/*
import android.annotation.SuppressLint;String SCRIPT_CREATE_DATABASE =
        "CREATE TABLE IF NOT EXISTS 'news' ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' TEXT, 'content' TEXT, 'slag' DATETIME DEFAULT CURRENT_TIMESTAMP)";

public int deleteAllNews() {
        return sqLiteDatabase.delete("news", null, null);
        }

public boolean  deleteNewsBySlag(String slag){
        return sqLiteDatabase.delete("news", "slag" + "=" + slag, null) > 0;
        }


public ArrayList<Article> queueAllNews() {
        ArrayList<Article> itemsList = new ArrayList<Article>();
        String[] columns = new String[] {"id", "title", "content", "slag"};
        Cursor cursor = sqLiteDatabase.query("news", columns,
        null, null, null, null, null);

        if (cursor.moveToFirst()) {
        while (!cursor.isAfterLast()) {
@SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
@SuppressLint("Range") String link = cursor.getString(cursor.getColumnIndex("slag"));
        Article item = new Article(title, link, "","","");
        // adding item to list
        itemsList.add(item);
        cursor.moveToNext();
        }
        return itemsList;
        }
        return new ArrayList<Article>();
        }
*/


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class SQLiteAdapter {
    myDbHelper myhelper;
    public SQLiteAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public int deleteAllNews() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        return db.delete("news", null, null);
    }

    public boolean  deleteNewsBySlag(String slag){

            SQLiteDatabase db = myhelper.getWritableDatabase();
        //Toast.makeText(myhelper.context, db.rawQuery("DELETE FROM news WHERE slag = ? ", new String[]{slag}).getCount(), Toast.LENGTH_SHORT).show();
        try
        { return db.rawQuery("DELETE FROM news WHERE slag = ? ", new String[]{slag}).getCount()>=0;
    }catch (Exception e){
        return  false;
    }
    }

    public boolean  newsBySlagIsExits(String slag){
        try (SQLiteDatabase db = myhelper.getWritableDatabase()) {
            return db.rawQuery("SELECT * FROM news WHERE slag = ? ", new String[]{slag}).getCount() > 0;
        }catch (Exception e){
            return  false;
        }

    }
    @SuppressLint("Range")
    public int getIdBySlag(String slag){
        try (SQLiteDatabase db = myhelper.getWritableDatabase()) {
           Cursor cursor = db.rawQuery("SELECT * FROM news WHERE slag = ? ", new String[]{slag});
           if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("id"));
            }
            else {
                return 0;
            }
        }catch (Exception e){
            return  0;
        }

    }

    public long insertInterestedCategory(ArrayList<String> data) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        db.execSQL("delete from "+ "interested_category");

        long res=0;
        for(String item : data){
            contentValues.put("title",item);
            res= db.insert("interested_category",null ,contentValues);
        }



        return res;
    }

    public ArrayList<String> queueAllInterestedCategory() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ArrayList<String> itemsList = new ArrayList<String>();

        String[] columns = new String[] {"id", "title"};
        Cursor cursor = db.query("interested_category", columns,
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));

                // adding item to list
                itemsList.add(title);
                cursor.moveToNext();
            }
            return itemsList;
        }
        return new ArrayList<String>();
    }


    public ArrayList<Article> queueAllNews() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ArrayList<Article> itemsList = new ArrayList<Article>();
        String[] columns = new String[] {"id", "title", "content", "slag", "content"};
        Cursor cursor = db.query("news", columns,
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String link = cursor.getString(cursor.getColumnIndex("slag"));
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("content"));
                Article item = new Article(title, link, description,"","",id, "");
                // adding item to list
                itemsList.add(item);
                cursor.moveToNext();
            }
            return itemsList;
        }
        return new ArrayList<Article>();
    }

    public long insertNews(String title, String link, String description) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("slag",link);
        contentValues.put("content",description);

        return db.insert("news",null ,contentValues);
    }


    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 1;   // Database Version

        private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS 'news' ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' TEXT, 'content' TEXT, 'slag' TEXT, DATETIME DEFAULT CURRENT_TIMESTAMP);";
        private static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS 'interested_category' ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' TEXT, DATETIME DEFAULT CURRENT_TIMESTAMP);";


        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
                db.execSQL(CREATE_TABLE2);
            } catch (Exception e) {
              //  Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
               // Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
               // Message.message(context,""+e);
            }
        }
    }
}