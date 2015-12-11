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
            HttpQueryClass httpQueryClass = new HttpQueryClass();
            //由于httpclient在传送数据到pc端服务器时默认使用了contentType iso-8859-1
            //所以使用字符格式转换，先在本地将gbk转为iso-8859-1
            //在pc服务器端，再将iso-8859-1转化为gbk
            byte[] b = data.getBytes("GBK");
            data = new String(b,"ISO-8859-1");
            httpQueryClass.getDataByServer(semester,id,type,data);
            Log.i("lyjserver", "CourseToServerService onHandleIntent http request key :" + semester + "," + id + "," + type + ","+data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
