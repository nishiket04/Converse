package com.nishiket.converse.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Helper extends SQLiteOpenHelper {
    private static Helper instance;
    private SQLiteDatabase database;

    public Helper(@Nullable Context context) {
        super(context, "chat_rooms", null, 1);
    }

    public static synchronized Helper getInstance(Context context) {
        if (instance == null) {
            instance = new Helper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS chat_rooms(id INTEGER PRIMARY KEY AUTOINCREMENT,user STRING,room STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public synchronized SQLiteDatabase openDatabase() {
        if (database == null || !database.isOpen()) {
            database = getWritableDatabase();
        }
        return database;
    }

    public synchronized void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
