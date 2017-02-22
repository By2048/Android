package com.am.newservce;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class NewsAdapter extends BaseAdapter{
    private Context context;
    private List<News> newsList;
    public NewsAdapter(Context context,List<News> newsList){
        this.context=context;
        this.newsList=newsList;
    }
    @Override
    public int getCount(){
        return newsList.size();
    }
    @Override
    public News getItem(int position){
        return  newsList.get(position);
    }
    @Override
    public long getItemId(int position){
        return  position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_detail, null);
        }
        TextView newsTitle= (TextView) convertView.findViewById(R.id.newsTitle);
        TextView newsContent= (TextView) convertView.findViewById(R.id.newsContent);
        TextView newsTime= (TextView) convertView.findViewById(R.id.newsTime);
        ImageView newsPic= (ImageView) convertView.findViewById(R.id.newsPic);
        News news=newsList.get(position);
        newsTitle.setText(news.getTitle());
        newsContent.setText(news.getContent());
        newsTime.setText(news.getTime());
        return  convertView;
    }
}
