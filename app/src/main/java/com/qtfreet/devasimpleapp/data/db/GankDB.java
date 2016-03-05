package com.qtfreet.devasimpleapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qtfreet.devasimpleapp.data.bean.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bear on 2016/2/7.
 */
public class GankDB {
    private String DB_NAME = "GankIO";
    private final int VERSION = 1;

    private static GankDB gankDB;
    private SQLiteDatabase db;

    private GankDB(Context context) {
        GankOpenHelper gankOpenHelper = new GankOpenHelper(context, DB_NAME, null, VERSION);
        db = gankOpenHelper.getWritableDatabase();
    }

    public synchronized static GankDB getInstance(Context context) {
        if (gankDB == null) {
            gankDB = new GankDB(context);
        }
        return gankDB;
    }

    public void saveImageInfo(ImageInfo info) {
        if (info != null) {
            ContentValues values = new ContentValues();
            values.put("url", info.getUrl());
            values.put("who", info.getWho());
            values.put("time", info.getTime());
            values.put("width", info.getWidth());
            values.put("height", info.getHeight());
            db.insert("ImageInfo", null, values);
        }
    }

    public List<ImageInfo> findImageInfoAll() {
        List<ImageInfo> imageInfos = new ArrayList<>();
        Cursor cursor = db.query("ImageInfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ImageInfo info = new ImageInfo();
                info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                info.setWho(cursor.getString(cursor.getColumnIndex("who")));
                info.setTime(cursor.getString(cursor.getColumnIndex("time")));
                info.setWidth(cursor.getInt(cursor.getColumnIndex("width")));
                info.setHeight(cursor.getInt(cursor.getColumnIndex("height")));

                imageInfos.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return imageInfos;
    }


    public boolean contain(ImageInfo info) {
        boolean result = false;
        if (info != null) {
            Cursor cursor = db.query("ImageInfo", null, "url=?", new String[]{info.getUrl()}, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
        }
        return result;
    }
}
