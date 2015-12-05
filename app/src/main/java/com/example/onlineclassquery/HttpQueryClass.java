package com.example.onlineclassquery;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.InputStream;
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
        try {
            List<Curriculum> curriculumList = new ArrayList<Curriculum>();
            HttpPost httpPost = new HttpPost("http://121.248.70.214/jwweb/ZNPK/TeacherKBFB_rpt.aspx");
            httpPost.setHeader("Referer", "http://121.248.70.214/jwweb/ZNPK/TeacherKBFB.aspx");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Sel_XNXQ", semester));
            params.add(new BasicNameValuePair("Sel_JS", id));
            params.add(new BasicNameValuePair("type", "1"));
            params.add(new BasicNameValuePair("txt_yzm", imgCode));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "gb2312");
            httpPost.setEntity(urlEncodedFormEntity);
            Log.i("lyj", "------" + semester + "," + id + "," + imgCode);
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
