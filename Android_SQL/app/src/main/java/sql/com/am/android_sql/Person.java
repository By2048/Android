package sql.com.am.android_sql;

/**
 * Created by AM on 2016/11/18.
 */

public class Person {
    public int _id;
    public String name;
    public int age;
    public  String info;
    public Person(){
    }
    public Person(String name,int age,String info){
        this.name=name;
        this.age=age;
        this.info=info;
    }
    public Person(int id,String name,int age,String info){
        this._id=id;
        this.name=name;
        this.age=age;
        this.info=info;
    }
}