package com.anand.taskinterview;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Anand
 */

public class Database {
    // db version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "image_save";
    private static final String DATABASE_TABLE_IMAGE = "image";
    private Database.DBHelper dbhelper;
    private final Context context;
    private SQLiteDatabase database;

    //table row
    public static final String KEY_ROWID = "id";
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_DATE = "date";
    static long dbInsert ;


    private static class DBHelper extends SQLiteOpenHelper {

        @SuppressLint("NewApi")
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // create table to store msgs
            db.execSQL(" CREATE TABLE " + DATABASE_TABLE_IMAGE + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                    + KEY_IMAGE_PATH + " TEXT, "

                    + KEY_DATE + " TEXT );");


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_IMAGE);


            onCreate(db);
        }

    }
    // constructor
    public Database(Context c) {
        context = c;
    }

    // open db
    public Database open() {
        dbhelper = new  DBHelper(context);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    // close db
    public void close() {
        dbhelper.close();
    }


    public long saveImagePath(String imagePath){
        ContentValues cv = new ContentValues();
        cv.put(KEY_IMAGE_PATH, imagePath);
      //  cv.put(KEY_DATE, date);

           database.delete(DATABASE_TABLE_IMAGE,null,null);
           dbInsert = database.insert(DATABASE_TABLE_IMAGE, null, cv);


        if(dbInsert != -1) {

            Toast.makeText(context, "profile update successfully" , Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
        }



        return dbInsert;
    }

    public String getImagePath(){
        String imagePath = "";

        String select_query = "SELECT  image_path FROM " + DATABASE_TABLE_IMAGE +
                " WHERE " + KEY_ROWID + " = " + String.valueOf(dbInsert) ;
        Cursor cursor = database.rawQuery(select_query,null);
        int iPicPath = cursor.getColumnIndex(KEY_IMAGE_PATH);

        for (cursor.moveToLast(); ! cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            imagePath = cursor.getString(iPicPath);

        }

        return imagePath;


    }
}