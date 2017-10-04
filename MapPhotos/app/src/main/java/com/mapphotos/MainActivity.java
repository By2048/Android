package com.mapphotos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int TO_MAPVIEW = 11; //请求码
    public static final int MAPVIEW_BACK = 12; //返回码


    private ListView photoListView;
    private List<Photo> photoList = new ArrayList<Photo>();
    private PhotoAdapter photoAdapter;
    private int seledRowIndex = -1;

    //溢出菜单中的 “修改名称” 菜单项
    private MenuItem editMenu;

    private SQLiteDatabase db;

    private boolean cleardb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_photolistview);

        //初始化ListView组件
        photoListView = (ListView) findViewById(R.id.phoroListView);
        photoAdapter = new PhotoAdapter(this);
        photoListView.setAdapter(photoAdapter);
        //初始化数据库
        initDB();
        showOverflowMenu();

        //从数据库读取相册 给List View显示
        photoList.clear();
        loadAlbumFormDb();


        //长按监听事件
        photoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //相册长按监听事件
                if (seledRowIndex == i) {
                    seledRowIndex = -1;
                    //取消选中数据行时，禁用 “修改名称” 菜单项
                    editMenu.setEnabled(false);
                } else {
                    seledRowIndex = i;
                    //选中数据行时，启用 “修改名称” 菜单项
                    editMenu.setEnabled(true);
                }
                // 通知ListView 更新显示
                photoAdapter.notifyDataSetChanged();
                //返回 true 即让android 不再做后续处理
                return true;
            }
        });
    }

    //加载数据库信息
    private void loadAlbumFormDb() {
        //打开数据库
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
        //设定默认
        Drawable defaultThumb = getResources().getDrawable(R.drawable.film);
        //查询所有相册
        String sql = "select * from t_album";
        Cursor cursor = db.rawQuery(sql, null);
        //
        while (cursor.moveToNext()) {
            Photo bean = new Photo();
            bean.id = cursor.getInt(cursor.getColumnIndex("_id"));
            bean.title = cursor.getString(cursor.getColumnIndex("title"));
            //处理
            String thumb = cursor.getString(cursor.getColumnIndex("thumb"));
            // Toast.makeText(getApplicationContext(), thumb+ " " + bean.id + " " + bean.title,Toast.LENGTH_SHORT).show();
            if (thumb == null || thumb.equals("")) {
                bean.thumb = defaultThumb;
            } else {
                // 处理缩略图完整文件路径
                thumb = CommonUtils.THUMB_PATH + thumb;
                //Toast.makeText(getApplicationContext(), thumb, Toast.LENGTH_SHORT).show();
                bean.thumb = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(thumb));
            }
            photoList.add(bean);
        }
        cursor.close();
        db.close();
    }


    private void initDB() {
        //打开或创建数据库
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
        
        String sql;
        sql = "create table if not exists t_album(" +
                " _id integer primary key autoincrement," +
                " title varchar, thumb varchar)";
        db.execSQL(sql);
        sql = "create table if not exists t_album_picture(" +
                " _id integer primary key autoincrement," +
                " latitude double, longitude double," +
                " picture varchar, thumb varchar, album_id integer)";
        db.execSQL(sql);
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //禁用 “修改名称” 菜单项; editMenu 修改为全局变量
        editMenu = menu.findItem(R.id.menu_item_edit);
        editMenu.setEnabled(false);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_gallery:
                int albumId = -1;
                //如果选中对应相册id 修改albumId
                if (seledRowIndex != -1) {
                    Photo bean = photoList.get(seledRowIndex);
                    albumId = bean.id;
                }
                //启动相册浏览
                Intent galleryintent = new Intent();
                galleryintent.setClass(this, GalleryActivity.class);
                galleryintent.putExtra("album_id", albumId);
                startActivity(galleryintent);
                break;
            case R.id.menu_item_add:
                //创建输入框
                final EditText txtTitle = new EditText(this);
                txtTitle.setInputType(InputType.TYPE_CLASS_TEXT);
                //
                AlertDialog.Builder addbuilder = new AlertDialog.Builder(this);
                //设定对话框按钮
                addbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = txtTitle.getText().toString();
                        //将新增相册保存到数据库
                        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
                        String addsql = "insert into t_album(title, thumb)" +
                                "values( '" + title + "', '')";
                        db.execSQL(addsql);
                        db.close();
                        //重新加载数据库并显示
                        photoList.clear();
                        loadAlbumFormDb();
                        photoAdapter.notifyDataSetChanged();
                    }
                });
                addbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                //设定对话框并显示
                addbuilder.setTitle("新相册名称");
                addbuilder.setView(txtTitle);
                AlertDialog addlg = addbuilder.create();
                addlg.show();
                break;
            case R.id.menu_item_remove:

                //删除选中行，并更新ListView数据
                if (seledRowIndex != -1) {
                    //获取选中行
                    final Photo bbq = photoList.get(seledRowIndex);
                    photoList.remove(seledRowIndex);
                    //更新数据库
                    SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
                    String delsql = "delete from t_album where _id=" + bbq.id;

                    db.execSQL(delsql);
                    db.delete("t_album_picture", "album_id = ?", new String[]{String.valueOf(bbq.id)});
                    db.close();

                    //重置选中项
                    seledRowIndex = -1;
                    //
                    photoAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "长按选中，删除", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_item_edit:
                //获取当前选中的数据行
                final Photo bbq = photoList.get(seledRowIndex);
                //设定用来输入相册名称的输入框
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(bbq.title);
                //动态创建对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //设定对话框中的按钮 “修改” 和 “返回”
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //禁用修改条目菜单项
                        seledRowIndex = -1;
                        editMenu.setEnabled(false);
                        // 修改选中的数据行
                        bbq.title = input.getText().toString();
                        //更新数据库
                        SQLiteDatabase db = openOrCreateDatabase("maphotos.db", Context.MODE_PRIVATE, null);
                        String editsql = String.format("update t_album set title='%s' where _id=%d", bbq.title, bbq.id);
                        db.execSQL(editsql);
                        db.close();

                        // 通知 ListView 更新内容
                        photoAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //点击返回，直接关闭对话框 dialogInterface 就是当前对话框
                        dialogInterface.cancel();
                    }
                });
                //设定 对话框的标题和界面
                builder.setTitle("修改相册名称");
                builder.setView(input);
                // 创建对话框 并显示
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.menu_item_map:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Map.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //通过java 机制反射设置菜单
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

    //内部类 PhotoAdapter
    private class PhotoAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;

        public PhotoAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return photoList.size();
        }

        @Override
        public Object getItem(int i) {
            return photoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        //
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = layoutInflater.inflate(R.layout.listview, null);
            }
            //初始化 每一行 中的组件 设置内容
            ImageView thumbView = (ImageView) view.findViewById(R.id.imageViewThumb);
            TextView titleView = (TextView) view.findViewById(R.id.textViewTitle);
            ImageView imageViewMap = (ImageView) view.findViewById(R.id.imageViewMap);

            //获取单击的数据行
            final Photo bean = photoList.get(i);
            //thumbView.setBackgroundDrawable(bean.thumb);
            thumbView.setImageDrawable(bean.thumb);
            titleView.setText(bean.title);
            //选中行高亮显示
            if (seledRowIndex == i) {
                view.setBackgroundColor(Color.parseColor("#63b8ff"));
            } else {
                view.setBackgroundColor(Color.parseColor("#f0f8ff"));
            }
            //
            imageViewMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Map.class);
                    //将相册 id 和 title 传递给map
                    intent.putExtra("album_id", bean.id);
                    intent.putExtra("album_title", bean.title);
                    //Toast.makeText(getApplicationContext(),String.format("%d%s",bean.id,bean.title),Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, TO_MAPVIEW);//数据回传
                }
            });
            //返回 行 中的内容
            //在ListView 中显示
            return view;
        }
    }

    //数据回传
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 拍照后回传数据并更新ListView
        switch (resultCode) {
            case MAPVIEW_BACK:
                photoList.clear();
                loadAlbumFormDb();
                photoAdapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(),"欢迎回来",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
