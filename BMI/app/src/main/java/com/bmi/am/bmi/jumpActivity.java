package com.bmi.am.bmi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class jumpActivity extends AppCompatActivity {

    private EditText jumpHeight,jumpWeight;
    private Button jumpCalc,jumpReturn;
    private TextView jumpTextResule;
    double bmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);
        jumpHeight=(EditText)this.findViewById(R.id.jumpHeight);
        jumpWeight=(EditText)this.findViewById(R.id.jumpWeight);
        jumpCalc=(Button)this.findViewById(R.id.jumpCalc);
        jumpReturn=(Button)this.findViewById(R.id.jumpReturn);
        jumpTextResule=(TextView)this.findViewById(R.id.jumpTextResule);

        Intent intent=getIntent();
        final double _height=intent.getDoubleExtra("_height",0);
        final double _weight=intent.getDoubleExtra("_weight",0);
        jumpHeight.setText(String.valueOf(_height));
        jumpWeight.setText(String.valueOf(_weight));
        jumpCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double h = _height/100;
                    double w = _weight;
                    bmi = w / (h * h);
                    if (bmi < 18.5) {
                        jumpTextResule.setText(R.string.str_thin);
                    } else if (bmi > 24.9) {
                        jumpTextResule.setText(R.string.str_fat);
                    } else {
                        jumpTextResule.setText(R.string.str_normal);
                    }
                } catch (Exception e) {
                    Toast.makeText(jumpActivity.this, R.string.str_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jumpReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("BMI",bmi);
                setResult(2,intent);
                finish();
            }
        });

    }
}
