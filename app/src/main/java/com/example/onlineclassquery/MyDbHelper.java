package com.example.onlineclassquery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2015/12/2.
 * 全局使用的MyDbHelper
 */
public class MyDbHelper extends SQLiteOpenHelper {
    private Context context;
    private String teacher_table = "create table teachers(id text primary key,name text)";
    private String course_table = "create table courses(teacher_id text,course_seq text,course_day text,course_content text,primary key(teacher_id,course_seq,course_day))";

    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(teacher_table);
        sqLiteDatabase.execSQL(course_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
