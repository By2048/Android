package am.jigsawgame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GameActivity extends Activity {

    private MediaPlayer player;
    private GameView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // hide title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Play bg.mp3
        myView = new GameView(this);
        //l　oadGameProgress();
        player = MediaPlayer.create(this, R.raw.bg);
        setContentView(myView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveGameProgress();
        if (player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setLooping(true);
        player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }


    private void saveGameProgress() {
        SharedPreferences settings = getSharedPreferences("JG_States", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String progress ="";
        for (PuzzleCell cell : myView.puzzCells) {
            String str = String.format("%d|%d|%d|%d|%s", cell.imgId, cell.x0, cell.y0, cell.zOrder, Boolean.toString(cell.fixed));
            progress = str + "#";
        }
        editor.putString("States", progress);
        editor.commit();
    }

    private void loadGameProgress() {
        myView.cellStates.clear();
        try {
            SharedPreferences setting = getSharedPreferences("JG_States", MODE_PRIVATE);
            String progress = setting.getString("States", "");
            String[] states = progress.split("#");
            for (String state : states) {
                String[] props = state.split("|");
                PuzzCellState pcs = new PuzzCellState();
                pcs.imgId = Integer.parseInt(props[0]);
                pcs.posx = Integer.parseInt(props[1]);
                pcs.posy = Integer.parseInt(props[2]);
                pcs.zOrder = Integer.parseInt(props[3]);
                pcs.fixed = Boolean.parseBoolean(props[4]);
                myView.cellStates.add(pcs);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
        }
    }


}
