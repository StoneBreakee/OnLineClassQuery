package com.example.onlineclassquery;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by admin on 2015/11/30.
 */
public class HttpData {
    private DbUtils dbUtils;
    private List<Teacher> teacherList;
    private List<Map<String,String>> teacherMap;
    private BasicCookieStore cookieStore;
    private static CloseableHttpClient httpclient;

    public HttpData(){
        teacherList = new ArrayList<Teacher>();
        teacherMap = new ArrayList<Map<String, String>>();
        cookieStore = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public void getAllTeachers() {
        try {
            HttpGet httpGet = new HttpGet("http://121.248.70.214/jwweb/ZNPK/Private/List_JS.aspx?xnxq=20150&js=");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();

            int length = "<script language=javascript>parent.theJS.innerHTML='".length();
            is.read("<script language=javascript>parent.theJS.innerHTML='".getBytes(),0,length);
            Document document = Jsoup.parse(is,"gb2312","");
            Elements elements = document.select("select[name=Sel_JS] option[value^=000]");
            storeTeachers(elements);
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public List<Teacher> getTeacherList(){
    //        return teacherList;
    //    }

    public List<Map<String,String>> getTeacherListMap(){
        return teacherMap;
    }

    //存储老师到内存和数据库中
    private void storeTeachers(Elements elements) {

        try {
            //存储到内存中
            Teacher teacher = null;
            SQLiteDatabase writeData = dbUtils.getWriteData();
            String add_sql = "insert into teachers(id,name) values(?,?);";
            for (Element element : elements) {
                Map<String,String> tmp = new HashMap<String, String>();
                teacher = new Teacher();
                teacher.setId(element.attr("value").trim());
                tmp.put("id", teacher.getId());
                teacher.setName(element.text().trim());
                tmp.put("name", teacher.getName());
                //teacherList.add(teacher);
                //插入到数据库，有中文乱码问题
//                byte[] tmpname = teacher.getName().getBytes("GBK");
//                String strname = new String(tmpname,"UTF-8");
                writeData.execSQL(add_sql,new String[]{teacher.getId(),teacher.getName()});
                teacherMap.add(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getInstance(){
        return httpclient;
    }

    public void setDbUtils(DbUtils dbUtils) {
        this.dbUtils = dbUtils;
    }
}
