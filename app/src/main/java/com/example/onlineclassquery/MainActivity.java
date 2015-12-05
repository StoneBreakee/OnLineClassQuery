package com.example.onlineclassquery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private HttpData httpData;
    private SearchView searchs;
    private ListView teachersList;

    private MyDbHelper myDbHelper;
    private DbUtils dbUtils;
    private List<Map<String, String>> teacherListMap;
    private boolean getListFlag = false;

    public MainActivity() {
        teacherListMap = new ArrayList<Map<String, String>>();
        httpData = new HttpData();
        initTeachers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchs = (SearchView) findViewById(R.id.searchTeachers);
        teachersList = (ListView) findViewById(R.id.listTeachers);
        initTeachersView();
        turnToQueryClass();
    }

    //设置对ListView的 文本过滤
    private void relateToListView() {
        searchs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                    Log.i("lyj", s);
                    teachersList.setFilterText(s);
                } else {
                    teachersList.clearTextFilter();
                }
                return true;
            }
        });
    }

    //初始化加载所有老师信息
    private void initTeachers() {
        new Thread() {
            @Override
            public void run() {
                try {
                    myDbHelper = new MyDbHelper(MainActivity.this, "Courses.db", null, 3);
                    dbUtils = new DbUtils(myDbHelper);
                    SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                    boolean isCached = sharedPreferences.getBoolean("isChachedTeachers", false);
                    if (!isCached) {
                        httpData.setDbUtils(dbUtils);
                        httpData.getAllTeachers();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isChachedTeachers", true);
                        editor.commit();
                        Log.i("lyj", "httpclient");
                        while(true){
                            teacherListMap = httpData.getTeacherListMap();
                            if(teacherListMap.size() != 0){
                                break;
                            }
                        }
                        getListFlag = true;
                    } else {
                        dbUtils.queryAllTeachers();
                        Log.i("lyj", "sqlite3");
                        while(true){
                            teacherListMap = dbUtils.getTeacherMap();
                            if(teacherListMap.size() != 0){
                                break;
                            }
                        }
                        getListFlag = true;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //初始化老师LIstView列表
    private void initTeachersView() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while(!getListFlag){
                        Thread.sleep(1000);
                    }
                    Log.i("lyj","length="+teacherListMap.size());
                    final SimpleAdapter teaAdapter = new SimpleAdapter(MainActivity.this, teacherListMap, R.layout.maplist_item, new String[]{"name", "id"}, new int[]{R.id.teachersName, R.id.teachersId});
                    teachersList.post(new Runnable() {
                        @Override
                        public void run() {
                            teachersList.setAdapter(teaAdapter);
                            Log.i("lyj","setAdapter");
                            teachersList.setTextFilterEnabled(true);
                            relateToListView();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void turnToQueryClass() {
        teachersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ViewGroup viewGroup = (ViewGroup) adapterView.getChildAt(i);
                Teacher teacher = new Teacher();
                String tmp = ((TextView) viewGroup.getChildAt(0)).getText().toString();
                teacher.setName(tmp);
                tmp = ((TextView) viewGroup.getChildAt(1)).getText().toString();
                teacher.setId(tmp);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, QueryClassByTeacher.class);
                intent.putExtra("teacher", teacher);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
