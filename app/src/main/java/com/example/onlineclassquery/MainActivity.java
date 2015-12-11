package com.example.onlineclassquery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private HttpData httpData;
    private SearchView searchs;
    private static ListView teachersList;
    private MyBaseAdapter myBaseAdapter;
    private MyDbHelper myDbHelper;
    private DbUtils dbUtils;
    private List<Map<String, String>> teacherListMap;

    public MainActivity() {
        teacherListMap = new ArrayList<Map<String, String>>();
        httpData = new HttpData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //查询框
        searchs = (SearchView) findViewById(R.id.searchTeachers);
        //显示老师的listview，由initTeacherView()完成初始化
        teachersList = (ListView) findViewById(R.id.listTeachers);

        myDbHelper = new MyDbHelper(MainActivity.this, "Courses.db", null, ConstantVars.version);
        dbUtils = new DbUtils(myDbHelper);

        //初始化教师列表
        initTeachersView();
        //点击老师时，跳转到该老师的课表查询界面 activity_queryclassbyteacher.xml(QueryClassByTeacher)
        turnToQueryClass();
    }

    //初始化老师ListView列表
    private void initTeachersView() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                initTeachers();
                dbUtils.queryAllTeachers();
                teacherListMap = dbUtils.getTeacherMap();
                myBaseAdapter = new MyBaseAdapter(MainActivity.this, teacherListMap);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                myBaseAdapter.setMyBaseAdapter(myBaseAdapter);
                teachersList.setAdapter(myBaseAdapter);
                Log.i("lyj", "myBaseAdapter.getCount()=" + myBaseAdapter.getCount());
                teachersList.setTextFilterEnabled(true);
                teachersList.setOnScrollListener(new ListViewOnScrollListener(myBaseAdapter, MainActivity.this));
                relateToListView();
            }
        }.execute();
    }

    private void turnToQueryClass() {
        teachersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("lyj", "adapterView.count=" + adapterView.getCount() + ",i=" + i);

                //获取选中的老师数据
                HashMap<String, String> teacherMap = (HashMap<String, String>) adapterView.getAdapter().getItem(i);
                String id = teacherMap.get("id");
                String name = teacherMap.get("name");
                Log.i("lyj", "id = " + id + ",name = " + name);

                //封装为Teacher对象
                Teacher teacher = new Teacher();
                teacher.setName(name);
                teacher.setId(id);

                //并发送给跳转到的Activity
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

    //初始化加载所有老师信息
    private void initTeachers() {
        //如果已经将网络上的老师数据存入本地数据库，则根据 isCached 跳过网络爬取数据
        //第一次使用httpdata获取老师列表时，直接存入数据库，然后从数据库中读出前100条
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        boolean isCached = sharedPreferences.getBoolean("isChachedTeachers", false);
        if (!isCached) {
            httpData.setDbUtils(dbUtils);
            //通知服务器
            httpData.serverStoreTeachers();
            //存入数据库的过程是否执行完成
            boolean flag = httpData.getAllTeachersToDB();
            if (flag) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isChachedTeachers", true);
                editor.commit();
                Log.i("lyj", "httpclient");
            }
        }
    }

    //设置对ListView的 文本过滤
    private void relateToListView() {
        searchs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            //当用户正在输入时，每输入一个字符就整体遍历一次
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

}
