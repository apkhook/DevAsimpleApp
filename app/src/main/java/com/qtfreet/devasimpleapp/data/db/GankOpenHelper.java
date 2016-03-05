package com.qtfreet.devasimpleapp.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bear on 2016/2/7.
 */
public class GankOpenHelper extends SQLiteOpenHelper {

    private final String CREATE_DB = "create table ImageInfo ("
            + "id integer primary key autoincrement, "
            + "url text, "
            + "who char(20), "
            + "time char(50), "
            + "width integer, "
            + "height integer)";


    public GankOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
