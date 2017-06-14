package com.mapphotos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GalleryActivity extends AppCompatActivity {

    private ImageView pictureView;
    private LinearLayout gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //初始化相册组件
        pictureView = (ImageView) findViewById(R.id.imageview_picture);
        gallery = (LinearLayout) findViewById(R.id.gallery);

        //获取Intent 传入的相册Id
        int albumId = getIntent().getIntExtra("album_id", -1);
        if (albumId == -1) {
            //如果没有接到Id 显示所有相册照片
            getAllPicture();
        } else {
            // 显示 相册id = albumId 的所有图片
            getAllPictureById(albumId);
        }
    }

    private void getAllPictureById(int albumId) {
        //检查 储存卡 是否有效
        if (!(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))) {
            return;
        }
        //从数据库中获取所有拍照的照片文件名
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
        String sql = "select * from t_album_picture where album_id=? order by _id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(albumId)});
        while (cursor.moveToNext()) {
            String picture = cursor.getString(cursor.getColumnIndex("picture"));
            String path = CommonUtils.PICTURE_PATH + picture;
            // 根据照片文件创建缩略图
            View view = getImageView(path);
            gallery.addView(view);
        }
        cursor.close();
        db.close();
    }

    private void getAllPicture() {
        //检查 储存卡 是否有效
        if (!(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))) {
            return;
        }
        //从数据库中获取所有拍照的照片文件名
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
        String sql = "select * from t_album_picture order by _id desc";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String picture = cursor.getString(cursor.getColumnIndex("picture"));
            String path = CommonUtils.PICTURE_PATH + picture;
            // 根据照片文件创建缩略图
            View view = getImageView(path);
            gallery.addView(view);
        }
        cursor.close();
        db.close();
    }

    private View getImageView(final String path) {
        int width = dipTopx(80);
        int height = dipTopx(80);
        //获取80*80的图片
        Bitmap bitmap = CommonUtils.decodeBitmapFormFile(path, width, height);
        //
        ImageView imageView = new ImageView(this);
        // 设定组件大小
        imageView.setLayoutParams(new ActionBar.LayoutParams(width, height));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        //将imageview加入gallery中
        final LinearLayout layout = new LinearLayout(this);
        //设定 布局大小
        layout.setLayoutParams(new ActionBar.LayoutParams(width, height));
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(0, 0, dipTopx(5), 0);
        layout.addView(imageView);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int w = pictureView.getWidth();
                int h = pictureView.getHeight();
                //Toast.makeText(getApplicationContext(),path,Toast.LENGTH_LONG).show();
                Bitmap pic = CommonUtils.decodeBitmapFormFile(path, w, h);
                pictureView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                pictureView.setImageBitmap(pic);
            }
        });

        return layout;
    }

    private int dipTopx(float dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
