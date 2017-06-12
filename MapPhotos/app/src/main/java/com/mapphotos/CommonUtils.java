package com.mapphotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liqiankun on 2017-5-17.
 */

public class CommonUtils {
    public static final String PICTURE_PATH; //照片保存路径
    public static final String THUMB_PATH; //缩略图保存路径
    /*
    //static 代码，在CommonUtils类首次加载时执行一次
    */
    static {
        //获取系统外置存储的目录，SD卡的路径
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        PICTURE_PATH = sdPath + "/MapPhotos/Picture/";
        THUMB_PATH = PICTURE_PATH + ".thumb/";
    }
    /*
    //生成一个以当前时间命名的照片文件
    */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public  static String getPictureNameByNowTime() {
        String filename = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        Date now = new Date();
        filename = simpleDateFormat.format(now) + ".jpg";
        return filename;
    }
    /*
    //保存照片文件
    */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String savePicture(Context context, Bitmap bitmap, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filename = getPictureNameByNowTime();
        String completePath = path + filename;
        // 调用compress() 方法将图像压缩为 JPEG 格式保存到文件
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(completePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return completePath;
    }
    /*
    //解码照片文件，返回指定尺寸的 Bitmap 文件
    */
    public static Bitmap decodeBitmapFormFile(String absolutePath, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        //获取指定照片的分辨率大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absolutePath, options);
        //计算采集倍率
        options.inSampleSize = calcInSampleSize(options, reqWidth, reqHeight);
        //按照指定倍率对照片进行解码
        //解码后得到指定大小的 Bitmap
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(absolutePath, options);

        return bitmap;
    }

    private static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // 图像原始大小
        final float height = options.outHeight;
        final float width = options.outWidth;
        //假设默认 采样比例为 1
        int inSampleSize = 1;
        //根据宽高的比例计算期望的倍率，四舍五入取整
        if (height > reqHeight || width > reqWidth) {
            //
            if (width < height) {
                //Math.round() 四舍五入
                inSampleSize = Math.round(width/reqWidth);
            } else {
                inSampleSize = Math.round(height/reqHeight);
            }
        }
        return inSampleSize;
    }
    /*
    // 将图像文件解码为 128 * 128 的 Bitmap 文件
    // 不一定得到正好 128*128 的大小，但宽高都不大于 128
    */
    public static Bitmap getPicture128(String path, String filename) {
        String imageFile = path + filename;
        return decodeBitmapFormFile(imageFile, 128, 128);
    }
    public static Bitmap getPicture128(String absolutePath) {
        return decodeBitmapFormFile(absolutePath, 128, 128);
    }
    /*
    // 将图像文件解码为 64 * 64 的 Bitmap 文件
    // 不一定得到正好 64*64 的大小，但宽高都不大于 64
    */
    public static Bitmap getPicture64(String path, String filename) {
        String imageFile = path + filename;
        return decodeBitmapFormFile(imageFile, 64, 64);
    }
    public static Bitmap getPicture64(String absolutePath) {
        return decodeBitmapFormFile(absolutePath, 64, 64);
    }
}
