package sql.com.am.android_sql;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button next_page=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS person");
        db.execSQL("CREATE  TABLE  person (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age SMALLINT,info VARCHAR)");
        db.execSQL("insert into person values(NULL,?,?,?)",new Object[]{"text_name",100,"text_info"});
        db.close( );
        next_page=(Button)super.findViewById(R.id.next_page);
        next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction("Main2Activity");
                startActivity(intent);
            }
        });
    }
}
