package am.colorcard;

/**
 * Created by AM on 2017-3-20.
 */


import android.graphics.Color;

public class SampleColor {
    public final String rgb;
    public final String name;
    public final String category;
    public final int val;

    public final int r;
    public final int g;
    public final int b;
    public final float h;
    public final float s;
    public final float v;

    public SampleColor(String rgb, String name, String category)
    {
        this.rgb = rgb;
        this.name = name;
        this.category = category;

        val = Color.parseColor(rgb);
        r = Color.red(val);
        g = Color.green(val);
        b = Color.blue(val);

        float [] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        h = hsv[0];
        s = hsv[1];
        v = hsv[2];


    }
    //计算HSV颜色空间中的两个颜色的色差值
    public static double distHSV(double h1, double s1, double v1, double h2, double s2, double v2)
    {
        return Math.sqrt((h1-h2)*(h1-h2)+(s1-s2)*(s1-s2)+(v1-v2)*(v1-v2));
    }
    //计算RGB颜色空间中的两个颜色的色差值
    public static double distRGB(int r1, int g1, int b1, int r2, int g2, int b2)
    {
        return Math.sqrt((r1-r2)*(r1-r2)+(g1-g2)*(g1-g2)+(b1-b2)*(b1-b2));
    }

}
