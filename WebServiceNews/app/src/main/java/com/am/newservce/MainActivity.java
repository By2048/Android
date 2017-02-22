package com.am.newservce;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button cid1,cid2,cid3,cid4,cid5,cid6,cid7,cid8;
    private ListView newsListView;
    private List<String> titleList=new ArrayList<String>();
    private List<News> newsList=new ArrayList<News>();
    private  NewsAdapter newsAdapter;
    private List<News>tempList=new ArrayList<News>();

    private  List<News>temp=new ArrayList<News>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cid1 = (Button) super.findViewById(R.id.cid1);
        cid2 = (Button) super.findViewById(R.id.cid2);
        cid3 = (Button) super.findViewById(R.id.cid3);
        cid4 = (Button) super.findViewById(R.id.cid4);
        cid5 = (Button) super.findViewById(R.id.cid5);
        cid6 = (Button) super.findViewById(R.id.cid6);
        cid7 = (Button) super.findViewById(R.id.cid7);
        cid8 = (Button) super.findViewById(R.id.cid8);

        newsListView=(ListView) super.findViewById(R.id.newsListView);

        newsList=new ArrayList<News>();
        new Thread(){
            @Override
            public void run(){
                requestServer(1);
            }
        }.start();

        newsAdapter=new NewsAdapter(MainActivity.this,newsList);
        newsListView.setAdapter(newsAdapter);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> item,View view,int position ,long id){
                News news=newsList.get(position);
                Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("NewsId",news.getNewsId());
                startActivity(intent);
            }
        });
        cid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(1);
                    }
                }.start();
            }
        });
        cid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(2);
                    }
                }.start();
            }
        });
        cid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(3);
                    }
                }.start();
            }
        });
        cid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(4);
                    }
                }.start();
            }
        });
        cid5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(5);
                    }
                }.start();
            }
        });
        cid6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(6);
                    }
                }.start();
            }
        });
        cid7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(7);
                    }
                }.start();
            }
        });
        cid8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                new Thread(){
                    @Override
                    public void run(){
                        requestServer(8);
                    }
                }.start();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            newsList=tempList;
            newsAdapter =new NewsAdapter(MainActivity.this,newsList);
            newsListView.setAdapter(newsAdapter);
        }
    };

    public void requestServer(int cateId){
        final String SERVICE_NS = "http://tempuri.org/";                       //命名空间
        final String SOAP_ACTION = "http://tempuri.org/GetAllNewsByCid";            //用来定义消息请求的地址，也就是消息发送到哪个操作
        final String SERVICE_URL = "http://192.168.191.1/NewService/GoodNews.asmx";   //URL地址，这里写发布的网站的本地地址
        String methodName = "GetAllNewsByCid";
        //创建HttpTransportSE传输对象，该对象用于调用Web Service操作
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        //使用SOAP1.1协议创建Envelop对象。从名称上来看,SoapSerializationEnvelope代表一个SOAP消息封包；但ksoap2-android项目对
        //SoapSerializationEnvelope的处理比较特殊，它是HttpTransportSE调用Web Service时信息的载体--客户端需要传入的参数，需要通过
        //SoapSerializationEnvelope对象的bodyOut属性传给服务器；服务器响应生成的SOAP消息也通过该对象的bodyIn属性来获取。
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("cid", cateId);
        envelope.dotNet = true;
        envelope.bodyOut = soapObject;
        try {
            ht.call(SOAP_ACTION, envelope);
            if (envelope.getResponse() != null) {
                SoapObject so = (SoapObject) envelope.bodyIn;
                SoapObject child = (SoapObject)so.getProperty(0);
                SoapObject child2 = (SoapObject)child.getProperty(1);
                SoapObject child3=((SoapObject)child2.getProperty(0));
                tempList=new ArrayList<News>();
                for (int i = 0; i <child3.getPropertyCount() ; i++) {
                    String title=((SoapObject)child3.getProperty(i)).getProperty("title").toString();
                    String content=((SoapObject)child3.getProperty(i)).getProperty("content").toString();
                    String time=((SoapObject)child3.getProperty(i)).getProperty("ndate").toString();
                    int nid=Integer.parseInt(((SoapObject)child3.getProperty(i)).getProperty("nid").toString());
                    int cid=Integer.parseInt(((SoapObject)child3.getProperty(i)).getProperty("cid").toString());
                    News temp=new News(title,time,content,nid,cid);
                    tempList.add(temp);
                    titleList.add(((SoapObject)child3.getProperty(i)).getProperty("title").toString());
                }

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

}
