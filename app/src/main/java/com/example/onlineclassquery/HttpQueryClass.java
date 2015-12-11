package com.example.onlineclassquery;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/12/1.
 */
public class HttpQueryClass {
    private CloseableHttpClient httpClient = HttpData.getInstance();

    public String getValidateCodeImg() {

        try {
            String imgpath = "/data/data/com.example.onlineclassquery/code.jpg";
            HttpGet httpGet = new HttpGet("http://121.248.70.214/jwweb/sys/ValidateCode.aspx");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            FileOutputStream fos = new FileOutputStream("/data/data/com.example.onlineclassquery/code.jpg");
            byte[] buffer = new byte[1024];
            int m = 0;
            while ((m = is.read(buffer)) > -1) {
                fos.write(buffer, 0, m);
            }
            fos.flush();
            fos.close();
            is.close();
            response.close();
            return imgpath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Curriculum> getClass(String semester, String id, String imgCode) {
        List<Curriculum> curriculumList = getDataByServer(semester,id,"query","");

        return curriculumList;
    }

    //客户端自己从学校网站获取数据，获取的数据用来显示和发送给服务器
    public List<Curriculum> getDataByWebSite(String semester, String id, String imgCode){
        try {
            List<Curriculum> curriculumList = new ArrayList<Curriculum>();
            HttpPost httpPost = new HttpPost("http://121.248.70.214/jwweb/ZNPK/TeacherKBFB_rpt.aspx");
            httpPost.setHeader("Referer", "http://121.248.70.214/jwweb/ZNPK/TeacherKBFB.aspx");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Sel_XNXQ", semester));
            params.add(new BasicNameValuePair("Sel_JS", id));
            params.add(new BasicNameValuePair("type", "1"));
            params.add(new BasicNameValuePair("txt_yzm", imgCode));
            if(TextUtils.isEmpty(imgCode)){
                Log.i("lyjserver", "HttpQueryClass getDataByWebSite imgCode is null when you request courses by httppost to school website");
                throw new Exception("imgcode is null when you request courses by httppost to school website");
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "gb2312");
            httpPost.setEntity(urlEncodedFormEntity);
            Log.i("lyjserver", "HttpQueryClass getDataByWebSite request key:" + semester + "," + id + "," + imgCode);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            Document doc = Jsoup.parse(EntityUtils.toString(httpEntity));
            Elements elements = doc.getElementsByTag("table");
            Element element = elements.get(3);
            classShow(element, curriculumList);
            response.close();
            return curriculumList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("lyjserver", "HttpQueryClass getDataByWebSite imgCode is null");
            return null;
        }
    }

    //客户端从服务器获取或发送老师的课表数据
    //type 两种取值
    //insert 时 ，表示向服务器发送课表数据
    //query 时 ，表示向服务器请求查询课表数据
    public List<Curriculum> getDataByServer(String semester, String id, String type,String data) {
        try {
            String strreponse;
            HttpUriRequest httpUriRequest = RequestBuilder.post()
                    .setUri("http://10.0.2.2:8080/MyAndroidServer/queryClass")
                    .addParameter("Sel_XNXQ", semester)
                    .addParameter("Sel_JS",id)
                    .addParameter("type",type)
                    .addParameter("data",data).build();
            Log.i("lyjserver", "HttpQueryClass getDataByServer http request key :" + semester + "," + id + "," + type + "," + data);
            CloseableHttpResponse response = httpClient.execute(httpUriRequest);

            HttpEntity entity = response.getEntity();
            strreponse = EntityUtils.toString(entity);
            Log.i("lyjserver", "HttpQueryClass getDataByServer http response :" + strreponse);
            response.close();
            return toList(strreponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Curriculum> toList(String strreponse) {
        try {
            byte[] b = strreponse.getBytes("ISO-8859-1");
            strreponse = new String(b,"GBK");
            Log.i("lyjlast",strreponse);
            Gson gson = new Gson();
            List<Curriculum> curriculums = gson.fromJson(strreponse, new TypeToken<List<Curriculum>>(){}.getType());
            return curriculums;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void classShow(Element element, List<Curriculum> curriculumList) {
        Elements tr = element.getElementsByTag("tr");
        int index[] = {1, 2, 1, 2, 1, 1};
        int i = 0;

        while (i < 7) {
            Curriculum curriculum = new Curriculum();
            //每天的第一节课
            curriculum.setFirstLesson(getEvery(tr, index, 1, i));
            //每天的第二节课
            curriculum.setSecondLesson(getEvery(tr, index, 2, i));
            curriculum.setThirdLesson(getEvery(tr, index, 3, i));
            curriculum.setForthLesson(getEvery(tr, index, 4, i));
            curriculum.setFifthLesson(getEvery(tr, index, 5, i));
            curriculumList.add(curriculum);
            i++;
        }
    }

    private String getEvery(Elements tr, int[] index, int x, int y) {
        String xx = tr.get(x).getElementsByTag("td").get(index[x] + y).text();
        if (xx.equals("")) xx = " ";
        return xx;
    }
}
