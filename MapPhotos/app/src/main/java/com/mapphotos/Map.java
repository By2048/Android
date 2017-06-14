package com.mapphotos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Map extends AppCompatActivity {
    private MapView mMapView;
    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private boolean firstLocation;
    private BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration config;

    //设置默认坐标
    private double myLatitude = 0;
    private double myLongitude = 0;

    private ImageView popCamera;
    private LinearLayout cameraBar;
    private LinearLayout previewArea;
    private LinearLayout snapArea;
    private ImageView snap;

    private Camera camera;
    private CameraSurfaceView cameraSurfaceView;
    private Bitmap picture;

    private int albumId; // 相册 Id
    private String albumTitle; // 相册标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        showOverflowMenu();

        //获取 MainActivity 传过来的相册信息
        Intent intent = getIntent();
        albumId = intent.getIntExtra("album_id", -1);
        albumTitle = intent.getStringExtra("album_title");

        //初始化地图控件
        mMapView = (MapView) findViewById(R.id.baiDuMv);
        baiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15f);
        baiduMap.setMapStatus(msu);

        // 定位初始化
        initLocation();

        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 一般地图


        // 从数据库加载相册中的照片
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
        String sql = "select * from t_album_picture " +
                " where album_id= " + albumId;
        if (albumId == -1) {
            sql = "select *from t_album_picture";
        }
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            //提取经纬度信息，缩略图保存路径
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String thumb = CommonUtils.THUMB_PATH + cursor.getString(cursor.getColumnIndex("thumb"));
            String picture = cursor.getString(cursor.getColumnIndex("picture"));

            //获取缩略图
            Bitmap bmp = BitmapFactory.decodeFile(thumb);
            if (bmp == null) {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
            }
            //循环添加坐标
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title(picture);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
            baiduMap.addOverlay(markerOptions);
        }
        //关闭 游标 数据库
        cursor.close();
        db.close();
        //Toast.makeText(getApplication(),myLatitude +""+ myLongitude,Toast.LENGTH_SHORT).show();
        //设置监听绑定
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                // map view 销毁后不在处理新接收的位置
                if (location == null || mMapView == null)
                    return;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                // 设置定位数据
                baiduMap.setMyLocationData(locData);
                Toast.makeText(getApplication(), myLatitude + "" + myLongitude, Toast.LENGTH_SHORT).show();
                //当前经纬度赋值
                myLatitude = location.getLatitude();
                myLongitude = location.getLongitude();
                Toast.makeText(getApplication(), myLatitude + "" + myLongitude, Toast.LENGTH_LONG).show();

                // 第一次定位时，将地图位置移动到当前位置
                if (firstLocation) {
                    firstLocation = false;
                    LatLng xy = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    baiduMap.animateMapStatus(status);
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });

        // 单击查看大图
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //从地标 title 获取图片名
                String picture = marker.getTitle();
                String path = CommonUtils.PICTURE_PATH + picture;
                File file = new File(path);

                //启动系统自带相册 显示图片
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent1);
                return false;
            }
        });


        // 初始化有关拍照的各个组件
        popCamera = (ImageView) findViewById(R.id.popCamera);
        cameraBar = (LinearLayout) findViewById(R.id.cameraBar);
        previewArea = (LinearLayout) findViewById(R.id.previewArea);//预览界面
        snapArea = (LinearLayout) findViewById(R.id.snapArea);
        snap = (ImageView) findViewById(R.id.snap);
        //默认隐藏相机拍照界面
        cameraBar.setVisibility(View.INVISIBLE);
        //动态切换显示拍照预览界面
        popCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraBar.getVisibility() == View.VISIBLE) {
                    //清除拍照界面中的组件并隐藏
                    cameraBar.removeAllViews();
                    cameraBar.setVisibility(View.INVISIBLE);
                } else if (cameraBar.getVisibility() == View.INVISIBLE) {
                    //如果还没有 CameraSurfaceView 组件， 则创建
                    if (cameraSurfaceView == null) {
                        cameraSurfaceView = new CameraSurfaceView(getApplicationContext());
                        //设置置顶显示，避免被挡住
                        cameraSurfaceView.setZOrderOnTop(true);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        previewArea.addView(cameraSurfaceView, params);
                    }
                    //动态构建相机预览界面并显示
                    cameraBar.removeAllViews();
                    cameraBar.addView(previewArea);
                    cameraBar.addView(snapArea) ;
                    cameraBar.setVisibility(View.VISIBLE);
                }
            }
        });
        //拍照处理
        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera == null) return;
                //启动相机聚焦，拍照前先聚焦
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        if (!b) {
                            Toast.makeText(getApplicationContext(), "没有聚焦！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //对焦成功则拍照
                        camera.takePicture(null, null, new PictureTakenCallback());
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        // 如果要显示位置图标,必须先开启图层定位
        baiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        // 关闭图层定位
        baiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    public void onBackPressed() {
        // 按 back 键时返回给 MainActivity 一个结果码
        setResult(MainActivity.MAPVIEW_BACK);
        finish();
    }

    public void initLocation() {
        locationClient = new LocationClient(this);
        firstLocation = true;
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //设置定位进度 高精度
        option.setOpenGps(true); //打开GPS
        option.setCoorType("bd09ll"); // 设置坐标类型

        option.setScanSpan(1000); //每隔 1秒 发送一次定位请求
        locationClient.setLocOption(option); //设置定位参数
    }


    //实现相机预览和拍照界面
    private class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder surfaceHolder = null;

        public CameraSurfaceView(Context context) {
            super(context);
            //保存 surfaceHolder ,设定回调对象
            surfaceHolder = this.getHolder();
            surfaceHolder.addCallback(this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (camera == null) {
                //打开并连接相机
                camera = Camera.open();
            }
            try {
                //设置预览画面
                camera.setPreviewDisplay(surfaceHolder);
            } catch (Exception e) {
                //连接相机失败，释放资源
                camera.release();
                camera = null;
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int i, int w, int h) {

            //当界面变化时，暂停预览
            camera.stopPreview();
            surfaceHolder = holder;
            //指定相机参数： 图片分辨率，横竖屏切换，自动聚焦
            Camera.Parameters parameters = camera.getParameters();
            //parameters.setPreviewSize(200,200);
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Collections.sort(sizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size size, Camera.Size t1) {
                    //倒排序，确保大的预览分辨率在前面
                    return t1.width - size.width;
                }
            });
            for (Camera.Size size : sizes) {
                if (size.width <= 1600) {
                    //parameters.setPreviewSize(size.width,size.height);
                    parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
            //横竖屏镜头自动调整
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                //设置为竖屏方向
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
            } else {
                //设置为横屏方向
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
            }
            //设置相机自动对焦
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            //使设置参数生效
            camera.setParameters(parameters);
            int imgformat = parameters.getPreviewFormat();
            int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
            Camera.Size camerasize = parameters.getPictureSize();
            int frame_size = ((camerasize.width * camerasize.height) * bitsperpixel) / 8;
            byte[] frame = new byte[frame_size];
            camera.addCallbackBuffer(frame);
            camera.setPreviewCallbackWithBuffer(previewCallback);
            //启动相机取景预览
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            //停止预览，释放系统相机服务
            //必须先调用 setPreviewCallback(null) 方法
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        // Camera 取景回调接口
        private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                //准备将下一帧预览动画保存到 data 缓冲区中，
                camera.addCallbackBuffer(bytes);
            }
        };

    }


    //拍照回调接口
    private class PictureTakenCallback implements Camera.PictureCallback {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            //视情况释放照片内存
            if (picture != null && !picture.isRecycled()) {
                // 经纬度信息在 locationClient 位置监听中获取 赋值
                picture.recycle();
                camera.startPreview();
            }
            //暂停相机预览
            camera.stopPreview();
            picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                int w = picture.getWidth();
                int h = picture.getHeight();
                //将照片旋转90°
                try {
                    Bitmap bitmap = Bitmap.createBitmap(picture, 0, 0, w, h, matrix, true);
                    picture.recycle();
                    picture = bitmap;
                } catch (OutOfMemoryError outOfMemoryError) {
                    //旋转照片失败
                    Toast.makeText(getApplicationContext(), "旋转失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                //默认横屏则不做任何处理
                Toast.makeText(getApplicationContext(), "已经是最终形态了-。-", Toast.LENGTH_SHORT).show();
            }
            if (picture != null) {
                //保存照片到 SD 卡
                String picPath = CommonUtils.savePicture(getApplicationContext(), picture, CommonUtils.PICTURE_PATH);

                Toast.makeText(getApplicationContext(), CommonUtils.THUMB_PATH, Toast.LENGTH_SHORT).show();

                Bitmap thumb64 = CommonUtils.getPicture64(picPath);
                //保存缩略图到 SD卡
                String thumb64Path = CommonUtils.savePicture(getApplicationContext(), thumb64, CommonUtils.THUMB_PATH);
                // 获取照片，缩略图的文件名
                String picname = new File(picPath).getName();
                String thumb64name = new File(thumb64Path).getName();

                // 保存照片数据到数据库
                SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
                String savesql = String.format("insert into t_album_picture(latitude, " +
                        " longitude, picture, thumb, album_id)" +
                        " values(%f, %f, '%s', '%s', %d)", myLatitude, myLongitude, picname, thumb64name, albumId);
                db.execSQL(savesql);
                //修改相册条目为最近一次拍照的缩略图
                savesql = String.format("update t_album set thumb='%s' where _id=%d", thumb64name, albumId);
                db.execSQL(savesql);

                db.close();

                //在地图上显示照片缩略图图标
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(albumTitle);
                markerOptions.position(new LatLng(myLatitude, myLongitude));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(thumb64));
                baiduMap.addOverlay(markerOptions);
                //
                picture.recycle();
                picture = null;
                Toast.makeText(getApplicationContext(), "已拍照", Toast.LENGTH_SHORT).show();
                //返回 回传
                onBackPressed();
            }
            //拍照完毕 继续预览
            camera.startPreview();
        }
    }

    private void showOverflowMenu() {
        try {

            ViewConfiguration config = ViewConfiguration.get(this);

            java.lang.reflect.Field menuKeyField =
                    ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map_none:

                baiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                break;
            case R.id.menu_map_normal:

                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.menu_map_satellite:

                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
