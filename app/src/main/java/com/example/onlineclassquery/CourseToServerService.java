package com.example.onlineclassquery;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CourseToServerService extends IntentService {
    public CourseToServerService() {
        super("MyIntentService");
    }

    //在这个方法中可以去处理一些具体的逻辑
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String type = "insert";
            String data = intent.getStringExtra("data");
            String semester = intent.getStringExtra("semester");
            String id = intent.getStringExtra("id");
//            HttpData httpData = new HttpData();
//            CloseableHttpClient httpClient = httpData.getInstance();
//            HttpPost httpPost = new HttpPost("http://10.0.2.2:8080/MyAndroidServer/queryClass");
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("Sel_XNXQ",semester));
//            params.add(new BasicNameValuePair("Sel_JS",id));
//            params.add(new BasicNameValuePair("type", type));
//            params.add(new BasicNameValuePair("data", data));
//            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params,"GBK");
//            httpPost.setEntity(urlEncodedFormEntity);
            HttpQueryClass httpQueryClass = new HttpQueryClass();
            byte[] b = data.getBytes("GBK");
            data = new String(b,"ISO-8859-1");
            httpQueryClass.getDataByServer(semester,id,type,data);
            Log.i("lyjserver", "CourseToServerService onHandleIntent http request key :" + semester + "," + id + "," + type + ","+data);
//            CloseableHttpResponse response = httpClient.execute(httpPost);
//            HttpEntity httpEntity = response.getEntity();
//            Log.i("lyjserver", EntityUtils.toString(httpEntity));
//            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
