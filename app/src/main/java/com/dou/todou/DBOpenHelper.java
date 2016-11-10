package com.dou.todou;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016-11-2.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

//    private static final int DB_VERSION = 1;

    public static final String
            TABLE_NAME = "todouitems";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_create_db =
                "create table if not exists "+
                        TABLE_NAME +
                        " (" +
                        "_id integer primary key autoincrement," +//auto
                        "whattodo text not null," +//内容
                        "theresult text," +
                        "datentime text," +
                        "hasdt integer not null default 0," +
                        "isdone integer not null default 0," +//done=1?0
                        "needalarm integer not null default 0," +
                        "neednotification integer not null default 0" +
                        ")";


        sqLiteDatabase.execSQL( sql_create_db );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        this.onCreate(sqLiteDatabase);
    }


}
