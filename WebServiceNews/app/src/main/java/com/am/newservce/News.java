package com.am.newservce;

/**
 * Created by AM on 2017/1/2.
 */

public class News {
    private String title,time,content;
    private int newsId,cateId;
    public News(String title,String time,String content,int newsId,int cateId){
        this.title=title;
        this.time=time;
        this.content=content;
        this.newsId=newsId;
        this.cateId=cateId;
    }
    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }
}
