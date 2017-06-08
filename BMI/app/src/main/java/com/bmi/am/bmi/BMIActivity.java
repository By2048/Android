package com.bmi.am.bmi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BMIActivity extends AppCompatActivity {
    private EditText height, weight;
    private Button calcBmi;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        calcBmi = (Button) findViewById(R.id.calcBmi);
        textResult = (TextView) findViewById(R.id.textResule);
        calcBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BMIActivity.this, jumpActivity.class);
                double _height = Double.parseDouble(height.getText().toString());
                double _weight = Double.parseDouble(weight.getText().toString());
                intent.putExtra("_height", _height);
                intent.putExtra("_weight", _weight);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            double bmi = data.getDoubleExtra("BMI", 0);
            if (bmi < 18.5) {
                textResult.setText(R.string.str_thin);
            } else if (bmi > 24.9) {
                textResult.setText(R.string.str_fat);
            } else {
                textResult.setText(R.string.str_normal);
            }

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.bmi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menuInfo:
                Intent intent = new Intent();
                intent.setClass(BMIActivity.this, InfoActivity.class);
                startActivity(intent);
                //Toast.makeText(this, "信息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuQuit:
                finish();
                break;

            default:
                break;
        }
        //return super.onOptionsItemSelected(item);
        return true;
    }
}
