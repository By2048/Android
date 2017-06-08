package am.jigsawgame;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;


public class PuzzleCell {
    public Bitmap image;
    public int imgId;
    public int width;
    public int height;
    public int x0;
    public int y0;
    public int zOrder;
    public Point touchedPoint;
    public int homeX0;
    public int homeY0;
    public boolean fixed;

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x0, y0, null);
    }

    public boolean isTouched(int x, int y) {
        if (x >= x0 && x <= x0 + width && y >= y0 && y <= y0 + height)
            return true;
        else
            return false;
    }

    public void setTouchedPoint(int x, int y) {
        if (touchedPoint == null) {
            touchedPoint = new Point(x, y);
        }
        touchedPoint.set(x, y);
    }

    public void moveTo(int x, int y) {
        int dx = x - touchedPoint.x;
        int dy = y - touchedPoint.y;
        x0 = x0 + dx;
        y0 = y0 + dy;
        setTouchedPoint(x, y);
    }
}

