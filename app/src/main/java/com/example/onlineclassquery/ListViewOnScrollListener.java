package com.example.onlineclassquery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.AbsListView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2015/12/3.
 */
public class ListViewOnScrollListener implements AbsListView.OnScrollListener {
    private int lastIndex;
    private MyBaseAdapter myBaseAdapter;
    private Context context;
    private MyDbHelper myDbHelper;

    public ListViewOnScrollListener(MyBaseAdapter myBaseAdapter, Context context) {
        this.myBaseAdapter = myBaseAdapter;
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        Log.i("lyj", "myBaseAdapter.getCount()=" + myBaseAdapter.getCount() + ",lastIndex=" + lastIndex);
        if (scrollState == SCROLL_STATE_IDLE && lastIndex == (myBaseAdapter.getCount() - 1)) {
            myDbHelper = new MyDbHelper(context, "Courses.db", null, ConstantVars.version);
            SQLiteDatabase readData = myDbHelper.getReadableDatabase();
            String startId = myToString(lastIndex);
            String endId  = myToString(lastIndex+100);
            Log.i("lyj","startId = " + startId + ",endId = "+ endId);
            String add_list = "select id,name from teachers where id > '" + startId + "' and id < '" + endId + "' order by id";
            Cursor cursor = readData.rawQuery(add_list,null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> tmp = new HashMap<String, String>();
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    tmp.put("id", id);
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.i("lyj", "name=" + name);
                    tmp.put("name", name);
                    myBaseAdapter.addView(tmp);
                } while (cursor.moveToNext());
            }
            myBaseAdapter.notifyDataSetChanged();
        }
    }

    private String myToString(int lastIndex) {
        String tmp = "";
        for(int i = 0;i < (7-String.valueOf(lastIndex).length());i++){
            tmp += "0";
        }
        tmp = tmp + String.valueOf(lastIndex);
        return tmp;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastIndex = firstVisibleItem + visibleItemCount - 1;
        Log.i("lyj", "lastIndex = " + lastIndex);
    }
}
