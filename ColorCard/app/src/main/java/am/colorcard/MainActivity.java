package am.colorcard;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private LinearLayout sampleTab = null;
    private LinearLayout searchTab = null;
    private LinearLayout identfyTab = null;

    private LinearLayout[] tabs;

    private View sampleTabView = null;
    private View searchTabView = null;
    private View identifyTabView = null;
    private LinearLayout content = null;

    private List<SampleColor> sampleList = new ArrayList<SampleColor>();
    private SampleColor[] showColor = new SampleColor[255];
    private double[] no255 = new double[255];

    public static final int PHOTO_CAPTURE = 100;//拍照
    public static final int PHOTO_CROP = 200;//剪裁

    private View imageview;//显示图片的控件
    private Bitmap pickbmp;//十色图片

    private EditText searchKey = null;

    private double diff, diffv;
    private SampleColor diffb, diffc;

    int i, j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sampleTab = (LinearLayout) super.findViewById(R.id.sampleTab);
        searchTab = (LinearLayout) super.findViewById(R.id.searchTab);
        identfyTab = (LinearLayout) super.findViewById(R.id.identifyTab);

        tabs = new LinearLayout[]{sampleTab, searchTab, identfyTab};

        LayoutInflater factory = LayoutInflater.from(this);
        sampleTabView = factory.inflate(R.layout.tab_sample, null);
        searchTabView = factory.inflate(R.layout.tab_search, null);
        identifyTabView = factory.inflate(R.layout.tab_identify, null);

        searchKey = (EditText) searchTabView.findViewById(R.id.searchText);

        content = (LinearLayout) super.findViewById(R.id.centent);
        content.addView(sampleTabView);

        loadColorCard();
        initSampleTabView();
        initSearchTabView();
        initIdentifyTabView();

        sampleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabChecked(sampleTab);
                content.removeAllViews();
                content.addView(sampleTabView);
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.myanim);
                sampleTabView.startAnimation(animation);
            }
        });

        searchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabChecked(searchTab);
                content.removeAllViews();
                content.addView(searchTabView);
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.myanim);
                searchTabView.startAnimation(animation);
            }
        });

        identfyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabChecked(identfyTab);
                content.removeAllViews();
                content.addView(identifyTabView);
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.myanim);
                identifyTabView.startAnimation(animation);
            }
        });
    }

    private void loadColorCard() {
        sampleList.clear();
        String sampleColors = getResources().getString(R.string.sample_color_list);
        String[] ss = sampleColors.split("\n");
        String rgb, name, category;
        int i, j;
        for (String s : ss) {
            rgb = name = category = null;
            if (s.trim().length() > 0) {
                i = s.indexOf('[');
                j = s.indexOf(']');
                if (j > i && i >= 0) {
                    rgb = s.substring(i + 1, j);
                }
                i = s.indexOf('[', j);
                j = s.indexOf(']', i);
                if (j > i && i >= 0) {
                    name = s.substring(i + 1, j);
                }
                i = s.indexOf('[', j);
                j = s.indexOf(']', i);
                if (j > i && i >= 0) {
                    category = s.substring(i + 1, j);
                }
                if (rgb != null && name != null && category != null) {
                    sampleList.add(new SampleColor(rgb, name, category));
                }
            }
        }
    }

    private void initSampleTabView() {
        final float scale = getResources().getDisplayMetrics().density;
        TableLayout sampleTable = (TableLayout) sampleTabView.findViewById(R.id.sampleTable);
        sampleTable.removeAllViews();
        for (final SampleColor sample : sampleList) {
            TableRow row = new TableRow(this);
            row.setPadding(0, (int) (20 * scale + 0.5f), 0, 0);
            View col00 = new View(this);
            col00.setBackgroundColor(sample.val);
            col00.setMinimumHeight((int) (50 * scale + 0.5f));
            TextView col01 = new TextView(this);
            col01.setText(sample.name);
            col01.setGravity(Gravity.CENTER);
            col01.setHeight((int) (50 * scale + 0.5f));
            row.addView(col00);
            row.addView(col01);
            sampleTable.addView(row);
        }
    }

    private void initIdentifyTabView() {
        final TextView sample01 = (TextView) identifyTabView.findViewById(R.id.sample01);
        final TextView sample02 = (TextView) identifyTabView.findViewById(R.id.sample02);
        final TextView sample03 = (TextView) identifyTabView.findViewById(R.id.sample03);
        final TextView textColorDiff = (TextView) identifyTabView.findViewById(R.id.textColorDiff);
        final TextView textColorInfo = (TextView) identifyTabView.findViewById(R.id.textColorInfo);
        final View viewPickedColor = identifyTabView.findViewById(R.id.viewPickedColor);
        Button btnCamera = (Button) identifyTabView.findViewById(R.id.btnCamera);
        //获取资源文件图片
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.color_card);
        imageview = identifyTabView.findViewById(R.id.viewPicture);
        imageview.setBackgroundDrawable(new BitmapDrawable(bmp));

        imageview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();
                int w = imageview.getWidth();
                int h = imageview.getHeight();

                if (x < 0 || y < 0 || x >= w || y >= h) {
                    textColorInfo.setText("超出范围");
                    return true;
                }
                if (pickbmp == null) {
                    Bitmap bgBmp = ((BitmapDrawable) imageview.getBackground()).getBitmap();
                    pickbmp = Bitmap.createScaledBitmap(bgBmp, w, h, false);
                }
                int pixel = pickbmp.getPixel(x, y);
                viewPickedColor.setBackgroundColor(pixel);

                String rgb = '#' + Integer.toHexString(pixel).substring(2).toUpperCase();
                textColorInfo.setText("当前颜色：" + rgb);

                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                float[] hsv = new float[3];
                Color.RGBToHSV(r, g, b, hsv);

                for (int cc = 0; cc < no255.length - 1; cc++) {
                    no255[cc] = 2 * 255 * 255;
                }

                i = 0;
                for (final SampleColor sample : sampleList) {

                    diff = distHSV(sample.h, hsv[0], sample.s, hsv[1], sample.v, hsv[2]);
                    diffb = sample;
                    if (diff < no255[0] || diff < no255[1] || diff < no255[2]) {
                        if (diff < no255[0]) {
                            diffv = no255[0];
                            no255[0] = diff;
                            diff = diffv;

                            diffc = showColor[0];
                            showColor[0] = diffb;
                            diffb = diffc;
                        }
                        if (diff < no255[1]) {
                            diffv = no255[1];
                            no255[1] = diff;
                            diff = diffv;

                            diffc = showColor[1];
                            showColor[1] = diffb;
                            diffb = diffc;
                        }
                        if (diff < no255[2]) {
                            diffv = no255[2];
                            no255[2] = diff;
                            diff = diffv;

                            diffc = showColor[2];
                            showColor[2] = diffb;
                            diffb = diffc;
                        }
                    }
                }


                sample01.setText(showColor[0].name);
                sample01.setBackgroundColor(showColor[0].val);

                sample02.setText(showColor[1].name);
                sample02.setBackgroundColor(showColor[1].val);

                sample03.setText(showColor[2].name);
                sample03.setBackgroundColor(showColor[2].val);

                return true;

            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PHOTO_CAPTURE);
            }
        });

    }

    private void initSearchTabView() {
        Button btnSearch = (Button) searchTabView.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断色卡中是否含有对应颜色

                String serach = searchKey.getText().toString();

                final float scale = getResources().getDisplayMetrics().density;
                TableLayout searchTable = (TableLayout) searchTabView.findViewById(R.id.searchTable);

                searchTable.removeAllViews();

                for (final SampleColor sample : sampleList) {
                    if (sample.name.indexOf(serach) != -1) {
                        TableRow row = new TableRow(MainActivity.this);
                        row.setPadding(0, (int) (20 * scale + 0.5f), 0, 0);

                        View col00 = new View(MainActivity.this);
                        col00.setBackgroundColor(sample.val);
                        col00.setMinimumHeight((int) (50 * scale + 0.5f));

                        TextView col01 = new TextView(MainActivity.this);
                        col01.setText(sample.name);
                        col01.setGravity(Gravity.CENTER);
                        col01.setHeight((int) (50 * scale + 0.5f));

                        row.addView(col00);
                        row.addView(col01);

                        searchTable.addView(row);
                    }
                }

            }
        });
    }

    public void setTabChecked(LinearLayout tab) {
        for (int i = 0; i < 3; i++) {
            tabs[i].setBackgroundDrawable(null);
            TextView text = (TextView) tabs[i].getChildAt(0);
            text.setTextColor(getResources().getColor(R.color.black));
        }

        tab.setBackgroundResource(R.drawable.tab_selected);
        TextView txt = (TextView) tab.getChildAt(0);
        txt.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Activity.RESULT_CANCELED) {
            return;
        } else if (requestCode == PHOTO_CAPTURE) {
            Bitmap photo = data.getParcelableExtra("data");
            if (photo != null) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setType("image/*");

                intent.putExtra("data", photo);
                intent.putExtra("crop", true);

                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);

                intent.putExtra("outputX", imageview.getWidth());
                intent.putExtra("outputY", imageview.getHeight());

                intent.putExtra("return-data", true);
                startActivityForResult(intent, PHOTO_CROP);


            }
        } else if (requestCode == PHOTO_CROP) {
            Bitmap photo = data.getParcelableExtra("data");
            if (photo != null) {
                imageview.setBackgroundDrawable(new BitmapDrawable(photo));
            }
            pickbmp = null;
        }
        super.onActivityResult(requestCode, requestCode, data);
    }

    //计算HSV颜色空间中的两个颜色的色差值
    public static double distHSV(double h1, double s1, double v1, double h2, double s2, double v2) {
        return Math.sqrt((h1 - h2) * (h1 - h2) + (s1 - s2) * (s1 - s2) + (v1 - v2) * (v1 - v2));
    }

    //计算RGB颜色空间中的两个颜色的色差值
    public static double distRGB(int r1, int g1, int b1, int r2, int g2, int b2) {
        return Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
    }
}