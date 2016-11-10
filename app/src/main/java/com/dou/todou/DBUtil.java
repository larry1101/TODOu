package com.dou.todou;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Administrator on 2016-11-6.
 */
public class DBUtil {
    protected static final String DB_NAME = "todous.db";

    public String tableName = DBOpenHelper.TABLE_NAME, c_id = "_id", c_what = "whattodo", c_result = "theresult", c_dt = "datentime", c_b_hasdt = "hasdt", c_b_isdone = "isdone", c_b_need_alarm = "needalarm", c_b_need_notification = "neednotification";

    protected final Context mContext;

    protected SQLiteDatabase.CursorFactory mFactory = null;

    protected SQLiteDatabase mDataBase;

    protected DBOpenHelper dbHelper;

    private boolean mIsInit;

    protected int _DATA_CACHE_VERSION_ = 1;

    public DBUtil(Context mContext) {
        this.mContext = mContext;
        init();
    }

    public void init() {
        if (mIsInit) {
            return;
        }

        dbHelper = new DBOpenHelper(
                mContext,
                DB_NAME,
                mFactory,
                _DATA_CACHE_VERSION_);

        mDataBase = dbHelper.getWritableDatabase();

        mIsInit = true;
    }

    public boolean insert(ContentValues values) {

        if (mDataBase == null) {
            return false;
        }

        mDataBase.insert(this.tableName, null, values);

        return true;
    }

//    public boolean delete(String whereClause) {
//
//        if (mDataBase == null) {
//            return false;
//        }
//
//        mDataBase.delete(this.tableName, whereClause, null);
//        return true;
//    }
//
//    public boolean update(ContentValues values, String whereClause) {
//        if (mDataBase == null) {
//            return false;
//        }
//
//        mDataBase.update(this.tableName, values, whereClause, null);
//        return true;
//    }
//
//    private Cursor query(String columnName, int id) {
//        if (mDataBase == null) {
//            return null;
//        }
//
//        if( mColumnIdInMedia == null ||
//                mColumnIdInMedia.equalsIgnoreCase("") ||
//                mColumnIdInMedia.isEmpty())
//        {
//            mColumnIdInMedia = c_id;
//        }
//
//        Cursor cursor = mDataBase.query(
//                this.tableName
//                , new String[] {columnName, mColumnIdInMedia}
//                , null
//                , null
//                , null
//                , null
//                , c_id + " asc");
//
//        int _idIndex = cursor.getColumnIndex(mColumnIdInMedia);
//
//        if(_idIndex != -1)
//        {
//            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
//                if( id == cursor.getInt(_idIndex))
//                {
//                    return cursor;
//                }
//            }
//        }
//
//        cursor.close();
//        return null;
//
//    }
//
//    public String queryString(String columnName, int id)
//    {
//        Cursor cursor = query( columnName, id);
//
//        String value = null;
//
//        if(cursor != null)
//        {
//            value = cursor.getString(0);
//            cursor.close();
//        }
//
//        return value;
//    }
//
//    public int queryInteger(String columnName, int id)
//    {
//        Cursor cursor = query( columnName, id);
//
//        int value = -1;
//
//        if(cursor != null)
//        {
//            value = cursor.getInt(0);
//            cursor.close();
//        }
//
//        return value;
//    }
//
//    public double queryDouble(String columnName, int id)
//    {
//        Cursor cursor = query( columnName, id);
//
//        double value = -1;
//
//        if(cursor != null)
//        {
//            value = cursor.getDouble(0);
//            cursor.close();
//        }
//
//        return value;
//    }

    public void execSQL(String sql) throws SQLException {
        mDataBase.execSQL(sql);
    }

    public void execSQL(String sql, Object[] bindArgs) throws SQLException {
        mDataBase.execSQL(sql, bindArgs);
    }

    public void Release() {

        if (this.mDataBase != null) {
            this.mDataBase.close();
            this.mDataBase = null;
        }

        this.mIsInit = false;
    }

    public ArrayList<TODOuItem> getAllItems() {
        ArrayList<TODOuItem> items = new ArrayList<>();

        try {
            Cursor c = mDataBase.rawQuery("select * from " + dbHelper.TABLE_NAME, null);

            if (c != null) {
//                if (c.isAfterLast() || c.isBeforeFirst() c.){
//                    c.close();
//                    return items;
//                }
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    int _id = c.getInt(c.getColumnIndex(c_id));
                    String todo = c.getString(c.getColumnIndex(c_what));
                    String result = c.getString(c.getColumnIndex(c_result));
                    String time = c.getString(c.getColumnIndex(c_dt));
                    boolean hasDT = c.getInt(c.getColumnIndex(c_b_hasdt)) != 0;
                    boolean done = c.getInt(c.getColumnIndex(c_b_isdone)) != 0;
                    boolean needa = c.getInt(c.getColumnIndex(c_b_need_alarm)) != 0;
                    boolean needn = c.getInt(c.getColumnIndex(c_b_need_notification)) != 0;
                    items.add(new TODOuItem(_id + "", todo, result, time, hasDT, done, needa, needn));
                    if (!c.moveToNext()) {
                        break;
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return items;
    }

    public boolean insert(TODOuItem item) {
        if (mDataBase == null) {
            return false;
        }

        execSQL("insert into " + dbHelper.TABLE_NAME +
                " (" +
                c_what + "," +
                c_dt + "," +
                c_b_hasdt + "," +
                c_b_isdone + "," +
                c_b_need_alarm + "," +
                c_b_need_notification +
                ") values(" +
                "'" + item.what() + "'," +
                "'" + item.getDT_str() + "'," +
                item.hasDT_int() + "," +
                "0," +
                item.needAlarm_int() + "," +
                item.needNotification_int() +
                ")");

        return true;
    }

    public boolean update(TODOuItem item) {
        if (mDataBase == null) {
            return false;
        }
        if (item.isNew()) {
            return false;
        }

        mDataBase.execSQL(
                "UPDATE " + dbHelper.TABLE_NAME +
                        " SET " +
                        c_what + " = '" + item.what() + "' , " +
                        c_dt + " = '" + item.getDT_str() + "' , " +
                        c_result + " = '" + item.result() + "' , " +
                        c_b_hasdt + " = " + item.hasDT_int() + " , " +
                        c_b_isdone + " = " + item.isDone_int() + " , " +
                        c_b_need_alarm + " = " + item.needAlarm_int() + " , " +
                        c_b_need_notification + " = " + item.needNotification_int() +

                        " WHERE " +
                        c_id + " = " + item.id()
        );
        return true;
    }

    public boolean delete(TODOuItem item) {

        if (mDataBase == null) {
            return false;
        }
        if (item.isNew()) {
            return false;
        }

        mDataBase.execSQL(
                "DELETE FROM " + dbHelper.TABLE_NAME +
                        " WHERE " + c_id + " =  " + item.id()
        );

        return true;
    }

    public Collection<? extends TODOuItem> getItems(boolean isDone) {
        ArrayList<TODOuItem> items = new ArrayList<>();

        try {
            Cursor c = mDataBase.rawQuery("select * from " + dbHelper.TABLE_NAME, null);

            if (c != null) {
//                if (c.isAfterLast() || c.isBeforeFirst() c.){
//                    c.close();
//                    return items;
//                }
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    boolean done = c.getInt(c.getColumnIndex(c_b_isdone)) != 0;
                    if (done == isDone) {
                        int _id = c.getInt(c.getColumnIndex(c_id));
                        String todo = c.getString(c.getColumnIndex(c_what));
                        String result = c.getString(c.getColumnIndex(c_result));
                        String time = c.getString(c.getColumnIndex(c_dt));
                        boolean hasDT = c.getInt(c.getColumnIndex(c_b_hasdt)) != 0;
//                    boolean done = c.getInt(c.getColumnIndex(c_b_isdone)) != 0;
                        boolean needa = c.getInt(c.getColumnIndex(c_b_need_alarm)) != 0;
                        boolean needn = c.getInt(c.getColumnIndex(c_b_need_notification)) != 0;
                        items.add(new TODOuItem(_id + "", todo, result, time, hasDT, done, needa, needn));
                    }
                    if (!c.moveToNext()) {
                        break;
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return items;

    }

    public void reNew(){
        mDataBase.execSQL("DROP TABLE " + DBOpenHelper.TABLE_NAME);

        mDataBase.execSQL("create table if not exists "+
                DBOpenHelper.TABLE_NAME +
                " (" +
                "_id integer primary key autoincrement," +//auto
                "whattodo text not null," +//内容
                "theresult text," +
                "datentime text," +
                "hasdt integer not null default 0," +
                "isdone integer not null default 0," +//done=1?0
                "needalarm integer not null default 0," +
                "neednotification integer not null default 0" +
                ")");
    }

    public String getNewestItemID() {
        String newest_id = "0";

        try {
            Cursor c = mDataBase.rawQuery("select * from " + dbHelper.TABLE_NAME, null);

            if (c != null) {
                c.moveToLast();
                while (!c.isAfterLast()) {
                    newest_id = c.getString(c.getColumnIndex(c_id));
                    if (!c.moveToNext()) {
                        break;
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return newest_id;
    }
}
