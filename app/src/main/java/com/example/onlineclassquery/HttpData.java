package com.example.onlineclassquery;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by admin on 2015/11/30.
 */
public class HttpData {
    private DbUtils dbUtils;
    private BasicCookieStore cookieStore;
    private static CloseableHttpClient httpclient;

    public HttpData() {
        cookieStore = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public boolean getAllTeachersToDB() {
        try {
            //从学校网站获取老师信息
            HttpGet httpGet = new HttpGet("http://121.248.70.214/jwweb/ZNPK/Private/List_JS.aspx?xnxq=20150&js=");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            int length = "<script language=javascript>parent.theJS.innerHTML='".length();
            is.read("<script language=javascript>parent.theJS.innerHTML='".getBytes(), 0, length);
            Document document = Jsoup.parse(is, "gb2312", "");
            Elements elements = document.select("select[name=Sel_JS] option[value^=000]");
            storeTeachers(elements);
            response.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //通知服务器，让服务器端保存老师数据
    public void serverStoreTeachers(){
        try {
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MyAndroidServer/queryTeacher");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            Log.i("lyj", EntityUtils.toString(httpEntity));
            response.close();
            Log.i("lyj","inform server to get teachers data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //存储老师到内存和数据库中
    //第一次使用httpdata获取老师列表时，直接存入数据库，然后从数据库中读出前100条
    private void storeTeachers(Elements elements) {
        try {
            Teacher teacher;
            SQLiteDatabase writeData = dbUtils.getWriteData();
            String add_sql = "insert into teachers(id,name) values(?,?);";
            for (Element element : elements) {
                teacher = new Teacher();
                teacher.setId(element.attr("value").trim());
                teacher.setName(element.text().trim());
                //保存到数据库中
                writeData.execSQL(add_sql, new String[]{teacher.getId(), teacher.getName()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getInstance() {
        return httpclient;
    }

    public void setDbUtils(DbUtils dbUtils) {
        this.dbUtils = dbUtils;
    }
}
