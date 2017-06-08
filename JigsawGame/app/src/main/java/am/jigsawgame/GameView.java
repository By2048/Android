package am.jigsawgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap background;
    private Bitmap puzzImage;
    private Rect puzzRect;
    private Rect thumbRect;
    private Rect cellsRect;
    private double pw;
    private double ph;
    private Paint paint;
    public List<PuzzleCell> puzzCells = new ArrayList<PuzzleCell>();
    public List<PuzzCellState> cellStates = new ArrayList<PuzzCellState>();
    private PuzzleCell touchedCell;
    private Bitmap backDrawing;
    private Canvas backCanvas;
    private int screenW;
    private int screenH;
    private SoundPool soundPool;
    private int soundId = 0;
    private SurfaceHolder holder;
    private boolean finished;

    private am.jigsawgame.Ball ball = new am.jigsawgame.Ball();

    public GameView(Context context) {
        super(context);
        // red、无锯齿平滑、实心线
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        ScaleAnimation animScale = new ScaleAnimation(5, 1, 3, 1);
        animScale.setDuration(800);
        setAnimation(animScale);
        initSounds();
        holder = this.getHolder();
        holder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        ball.image = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
    }

    private void update() {
        int x = (int) (6 * Math.random());
        int y = (int) (4 * Math.random());
        ball.x0 = ball.x0 + ball.direction * x;
        ball.y0 = ball.y0 + ball.direction * y;
        if (ball.x0 > screenW || ball.y0 > screenH) {
            ball.direction = -1;
        }
        if (ball.x0 < 0 || ball.y0 < 0) {
            ball.direction = 1;
        }
    }

    private void initSounds() {
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundId = soundPool.load(getContext(), R.raw.win, 1);
    }

    public void playSound(int soundId) {
        AudioManager mgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        float currVol = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVol = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = currVol / maxVol;
        soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenW = (w > h) ? w : h;
        screenH = (w > h) ? h : w;
        initGame();
//        if (cellStates.size() > 0) {
//            loadPuzzCells();
//        } else {
//            makePuzzCells();
//        }
        makePuzzCells();
        drawPuzzle(backCanvas);
        super.onSizeChanged(w, h, oldw, oldh);
        // 计算拼图块大小和拼图区域 水平方向pw 垂直方向ph 3×4分割，保存到list<puzzCells>

        Collections.sort(puzzCells, new Comparator<PuzzleCell>() {
            @Override
            public int compare(PuzzleCell c0, PuzzleCell c1) {
                return c1.zOrder - c0.zOrder;
            }
        });

        sortPuzzCells();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private int getCellMaxzOrder() {
        int zOrder = -1;
        for (PuzzleCell cell : puzzCells) {
            if (cell.zOrder > zOrder)
                zOrder = cell.zOrder;
        }
        return zOrder;
    }

    private void sortPuzzCells() {
        Collections.sort(puzzCells, new Comparator<PuzzleCell>() {
            @Override
            public int compare(PuzzleCell c0, PuzzleCell c1) {
                return c1.zOrder - c0.zOrder;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (act) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < puzzCells.size(); i++) {
                    PuzzleCell cell = puzzCells.get(i);
                    if (cell.fixed) {
                        continue;
                    }
                    if (cell.isTouched(x, y)) {
                        cell.zOrder = getCellMaxzOrder() + 1;
                        sortPuzzCells();
                        drawPuzzle(backCanvas, cell);
                        touchedCell = cell;
                        touchedCell.setTouchedPoint(x, y);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchedCell != null) {
                    if (touchedCell != null) {
                        touchedCell.moveTo(x, y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchedCell != null) {
                    Point p1 = new Point(touchedCell.x0, touchedCell.y0);
                    Point p2 = new Point(touchedCell.homeX0, touchedCell.homeY0);
                    double ds = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) + (p1.y - p2.y));
                    if (ds <= dipTopx(10)) {
                        touchedCell.x0 = touchedCell.homeX0;
                        touchedCell.y0 = touchedCell.homeY0;
                        touchedCell.fixed = true;
                        playSound(soundId);
                    }
                }
                touchedCell = null;
                drawPuzzle(backCanvas);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread t = new Thread(new GameRender());
        finished = false;
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        finished = true;
    }

    private class GameRender implements Runnable {
        @Override
        public void run() {
            Canvas canvas = null;
            long tick0, tick1, frame;
            while (!finished) {
                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        tick0 = System.currentTimeMillis();
                        canvas.drawBitmap(backDrawing, 0, 0, null);
                        if (touchedCell != null) {
                            touchedCell.draw(canvas);
                        }
                        update();
                        //canvas.drawBitmap(ball.image,ball.x0,ball.y0,null);
                        tick1 = System.currentTimeMillis();
                        Paint fpsPaint=new Paint();
                        fpsPaint.setColor(Color.RED);
                        fpsPaint.setTextSize(30);
                        canvas.drawText("FPS:" + 1000 / (tick1 - tick0), 40 ,40, fpsPaint);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    private void initGame() {
        pw = (screenW - dipTopx(10) - dipTopx(10) - dipTopx(10)) / 5.5;
        ph = (screenH - dipTopx(20) - dipTopx(20)) / 3.0;
        // 拼图区域、缩略图区域、打乱拼图块的区域
        puzzRect = new Rect(dipTopx(10),
                dipTopx(20),
                dipTopx(10) + (int) (4 * pw),
                dipTopx(20) + (int) (3 * ph));
        thumbRect = new Rect(dipTopx(10) + (int) (4 * pw) + dipTopx(10),
                dipTopx(20),
                screenW - dipTopx(10),
                (int) (dipTopx(20) + ph));
        cellsRect = new Rect(dipTopx(10) + (int) (4 * pw) + dipTopx(10),
                (int) (dipTopx(20) + ph + dipTopx(5)),
                (int) (screenW - dipTopx(10) - pw),
                (int) (screenH - dipTopx(20) - ph));
        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        background = Bitmap.createScaledBitmap(bg, screenW, screenH, false);
        bg.recycle();
        Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        puzzImage = Bitmap.createScaledBitmap(pic, puzzRect.width(), puzzRect.height(), false);
        pic.recycle();
        backDrawing = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        backCanvas = new Canvas(backDrawing);

    }

    private void loadPuzzCells() {
        int row, col;
        Rect puzzR;
        for (PuzzCellState cellState : cellStates) {
            row = cellState.imgId / 4;
            col = cellState.imgId % 4;
            puzzR = new Rect((int) (col * pw),
                    (int) (row * ph),
                    (int) ((col + 1) * pw),
                    (int) ((row + 1) * ph));
            PuzzleCell cell = new PuzzleCell();
            cell.image = Bitmap.createBitmap(puzzImage,puzzR.left, puzzR.top,puzzR.width(),puzzR.height());
            cell.imgId = cellState.imgId;
            cell.x0 = cellState.posx;
            cell.y0 = cellState.posy;
            cell.width = (int) pw;
            cell.height = (int) ph;
            cell.zOrder = cellState.zOrder;
            cell.fixed = cellState.fixed;
            cell.homeX0 = puzzR.left + dipTopx(10);
            cell.homeY0 = puzzR.top + dipTopx(20);
            puzzCells.add(cell);
        }
        sortPuzzCells();
    }

    private void makePuzzCells() {
        Set<Integer> zOrders = new HashSet<Integer>();
        Rect puzzR;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                //计算第（i，j）拼图块在原拼图中的区域，是相对鱼片内部的
                puzzR = new Rect((int) (j * pw), (int) (i * ph), (int) ((j + 1) * pw), (int) ((i + 1) * ph));
                PuzzleCell cell = new PuzzleCell();
                cell.image = Bitmap.createBitmap(puzzImage, puzzR.left, puzzR.top, puzzR.width(), puzzR.height());
                cell.width = (int) pw;
                cell.height = (int) ph;
                cell.x0 = cellsRect.left + (int) (Math.random() * cellsRect.width());
                cell.y0 = cellsRect.top + (int) (Math.random() * cellsRect.height());
                int zOrder;
                do {
                    zOrder = (int) (12 * Math.random());
                } while (zOrders.contains(zOrder));
                zOrders.add(zOrder);
                cell.homeX0 = (int) (j * pw) + dipTopx(10);
                cell.homeY0 = (int) (i * ph) + dipTopx(20);
                cell.fixed = false;
                cell.zOrder = zOrder;
                puzzCells.add(cell);
            }
        }
        sortPuzzCells();
    }


    private void drawPuzzle(Canvas canvas) {
        drawPuzzle(canvas, null);
    }

    private void drawPuzzle(Canvas canvas, PuzzleCell ignorerdCell) {
        canvas.drawBitmap(background, 0, 0, null);
        Paint p = new Paint();
        p.setAlpha(150);
        canvas.drawBitmap(puzzImage, null, puzzRect, p);
        // 边框
        canvas.drawRect(puzzRect, paint);
        // 水平线
        canvas.drawLine(puzzRect.left, (int) ph + puzzRect.top,puzzRect.right, (int) ph + puzzRect.top, paint);
        canvas.drawLine(puzzRect.left, (int) (ph * 2) + puzzRect.top,puzzRect.right, (int) (ph * 2) + puzzRect.top, paint);
        // 垂直线
        canvas.drawLine((int) pw + puzzRect.left, puzzRect.top,(int) pw + puzzRect.left, puzzRect.bottom, paint);
        canvas.drawLine((int) pw * 2 + puzzRect.left, puzzRect.top,(int) pw * 2 + puzzRect.left, puzzRect.bottom, paint);
        canvas.drawLine((int) pw * 3 + puzzRect.left, puzzRect.top,(int) pw * 3 + puzzRect.left, puzzRect.bottom, paint);
        // 缩略图
        canvas.drawBitmap(puzzImage, null, thumbRect, null);

        //绘制所有拼图块
        for (int i = puzzCells.size() - 1; i >= 0; i--) {
            PuzzleCell cell = puzzCells.get(i);
            if (cell!=ignorerdCell)
                cell.draw(canvas);
            else
                continue;
        }
    }

    private int dipTopx(float dip) {
        final float scale =
                Resources.getSystem().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

}
