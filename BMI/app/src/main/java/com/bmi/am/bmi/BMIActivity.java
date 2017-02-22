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
                try {
                    double _height = Double.parseDouble(height.getText().toString()) / 100;
                    double _weight = Double.parseDouble(weight.getText().toString());
                    double bmi = _weight / (_height * _height);
                    if (bmi < 18.5) {
                        textResult.setText(R.string.str_thin);
                    } else if (bmi > 24.9) {
                        textResult.setText(R.string.str_fat);
                    } else {
                        textResult.setText(R.string.str_normal);
                    }
                    //Toast.makeText(BMIActivity.this,_height+" "+_weight+" "+bmi,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(BMIActivity.this, R.string.str_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Intent intent=new Intent();
                intent.setClass(BMIActivity.this,InfoActivity.class);
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
