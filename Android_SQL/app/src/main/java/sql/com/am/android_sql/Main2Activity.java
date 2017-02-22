package sql.com.am.android_sql;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private EditText name=null;
    private  EditText age=null;
    private  EditText info=null;
    private Button add=null;
    private Button action=null;
    private  Button select=null;
    private EditText sql=null;

    private void PrintInfo()
    {
        String info="";
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("SELECT  *  FROM  person " ,  null ) ;
        while (cur.moveToNext())
        {
            String name=cur.getString(cur.getColumnIndex("name"));
            int age=cur.getInt(cur.getColumnIndex("age"));
            info+="[Name]\t\t"+name+" \t\t[Age]\t\t"+age+"\n";
        }
        cur.close(  );
        db.close( );
        Toast.makeText(Main2Activity.this, info, Toast.LENGTH_LONG).show();
    }
    private  boolean IsCreated(String name)
    {
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("SELECT  *  FROM  person WHERE name==?" , new String[]{name}) ;
        if (cur.getCount()==0)
            return  false;
        else
            return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        name=(EditText)super.findViewById(R.id.name);
        age=(EditText)super.findViewById(R.id.age);
        info=(EditText)super.findViewById(R.id.info);
        add=(Button)super.findViewById(R.id.add);
        action=(Button)super.findViewById(R.id.action);
        select=(Button)super.findViewById(R.id.select);
        sql=(EditText)super.findViewById(R.id.sql);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sql_text=sql.getText().toString();
                try{
                    if(sql_text.contains("select")){
                        String info="";
                        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                        Cursor cur = db.rawQuery(sql_text,  null ) ;
                        while (cur.moveToNext())
                        {
                            String name=cur.getString(cur.getColumnIndex("name"));
                            int age=cur.getInt(cur.getColumnIndex("age"));
                            info+="[Name]\t\t"+name+" \t\t[Age]\t\t"+age+"\n";
                        }
                        cur.close(  );
                        db.close( );
                        Toast.makeText(Main2Activity.this, info, Toast.LENGTH_SHORT).show();
                    }
                    else if (sql_text.contains("insert") || sql_text.contains("update") || sql_text.contains("delete")){
                        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                        db.beginTransaction();
                        db.execSQL(sql_text);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        db.close();
                        Toast.makeText(Main2Activity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception err){
                    Toast.makeText(Main2Activity.this, err.toString(), Toast.LENGTH_LONG).show();
                }
                finally {
                    Toast.makeText(Main2Activity.this, "查询结束", Toast.LENGTH_SHORT).show();
                }
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintInfo();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.length()==0 || age.length()==0 || info.length()==0)
                {
                    Toast.makeText(Main2Activity.this, "输入错误", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(age.getText().toString())<=0 || Integer.parseInt(age.getText().toString())>150)
                {
                    Toast.makeText(Main2Activity.this, "年龄应>0 <150", Toast.LENGTH_SHORT).show();
                }
                else if(IsCreated(name.getText().toString())==true)
                {
                    Toast.makeText(Main2Activity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                    db.execSQL("INSERT INTO person VALUES (NULL,?, ?,?)", new Object[ ]{  name.getText().toString(),Integer.parseInt( age.getText().toString()),info.getText().toString()  } );
                    db.close( );
                    PrintInfo();
                }
            }
        });

    }
}
