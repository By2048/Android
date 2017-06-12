package com.mapphotos;

import android.graphics.drawable.Drawable;



public class RowInfoBean extends Object {
    public int id; // 相册id
    public Drawable thumb;//相册图标
    public String title;//相册标题
    public RowInfoBean(Drawable thumb, String title) {
        this.thumb = thumb;
        this.title = title;
    }
    public RowInfoBean() {
        //构造函数
    }
}
