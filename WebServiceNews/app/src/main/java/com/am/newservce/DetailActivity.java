package com.am.newservce;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private String newsTitle,newsContent;
    private TextView newsDetailTitle,newsDetailContent;
    private Button beforNews,nextNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        newsDetailTitle=(TextView)super.findViewById(R.id.newsDetailTitle);
        newsDetailContent=(TextView)super.findViewById(R.id.newsDetailContent);
        beforNews=(Button)super.findViewById(R.id.beforNews);
        nextNews=(Button)super.findViewById(R.id.nextNews);
        beforNews.setOnClickListener(new beforNewsListener ());
        nextNews.setOnClickListener(new nextNewsListener ());
        final int NewsId=getIntent().getIntExtra("NewsId",0);
        new Thread(){
            @Override
            public void run(){
                GetNewsInfoByNewsId(NewsId);
            }
        }.start();
    }

    private  class beforNewsListener implements OnClickListener{
        public void onClick(View view){
            Toast.makeText(getApplicationContext(),	"beforNews",Toast.LENGTH_LONG).show();
        }
    }
    private  class nextNewsListener implements OnClickListener{
        public void onClick(View view){
            Toast.makeText(getApplicationContext(),	"nextNews",Toast.LENGTH_LONG).show();
        }
    }

    Handler getNewsInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            newsDetailTitle.setText(newsTitle);
            newsDetailContent.setText(newsContent);
        }
    };

    public void GetNewsInfoByNewsId(int NewsId) {
        final String SERVICE_NS = "http://tempuri.org/";                       //命名空间
        final String SOAP_ACTION = "http://tempuri.org/GetOneNewsByNid";            //用来定义消息请求的地址，也就是消息发送到哪个操作
        final String SERVICE_URL = "http://192.168.191.1/NewService/GoodNews.asmx";   //URL地址，这里写发布的网站的本地地址
        String methodName = "GetOneNewsByNid";
        //创建HttpTransportSE传输对象，该对象用于调用Web Service操作
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        //使用SOAP1.1协议创建Envelop对象。从名称上来看,SoapSerializationEnvelope代表一个SOAP消息封包；但ksoap2-android项目对
        //SoapSerializationEnvelope的处理比较特殊，它是HttpTransportSE调用Web Service时信息的载体--客户端需要传入的参数，需要通过
        //SoapSerializationEnvelope对象的bodyOut属性传给服务器；服务器响应生成的SOAP消息也通过该对象的bodyIn属性来获取。
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("nid", NewsId);
        envelope.dotNet = true;
        envelope.bodyOut = soapObject;
        try {
            ht.call(SOAP_ACTION, envelope);
            if (envelope.getResponse() != null) {
                SoapObject so = (SoapObject) envelope.bodyIn;
                SoapObject child = (SoapObject)so.getProperty(0);
                newsTitle=child.getProperty("title").toString();
                newsContent=child.getProperty("content").toString();
                Message msg = new Message();
                msg.what = 1;
                getNewsInfo.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 0;
                getNewsInfo.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

}
