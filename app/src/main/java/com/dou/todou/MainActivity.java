package com.dou.todou;

// TODO: 2016-10-29 floder;swip;forcetouch;alarm;editable;lineHeight;details;refreshTable(_id);...

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TabHost tabHost;

    EditText editText;
    ImageButton imageButton_ok,imageButton_add,imageButton_del;
    boolean DEL_MODE = false;

    ListView listView1,listView2;
    ArrayList<String> l1=new ArrayList<>(),l2=new ArrayList<>();
    ArrayAdapter<String> a1,a2;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("1").setIndicator("TODO").setContent(R.id.LL_TODO));
        tabHost.addTab(tabHost.newTabSpec("2").setIndicator("DONE").setContent(R.id.LL_DONE));

        editText = (EditText)findViewById(R.id.editText);
        imageButton_ok = (ImageButton)findViewById(R.id.imageButton_go);
        imageButton_add = (ImageButton)findViewById(R.id.imageButton_add);
        imageButton_del = (ImageButton)findViewById(R.id.imageButton_del);

        listView1 = (ListView)findViewById(R.id.listView);
        listView2 = (ListView)findViewById(R.id.listView2);

        a1 = new ArrayAdapter< >(context,android.R.layout.simple_expandable_list_item_1,l1);
        a2 = new ArrayAdapter< >(context,android.R.layout.simple_expandable_list_item_1,l2);
        listView1.setAdapter(a1);
        listView2.setAdapter(a2);

        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateDB(getItemID(l1.get(i)), true);// TODO: 2016-10-29
                getData();
                //l2.add(l1.get(i));
                //l1.remove(i);
                //refreshLV();
                return true;
            }
        });
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DEL_MODE){
                    delfromDB(getItemID(l2.get(i)));// TODO: 2016-10-29
                    getData();
                    //l2.remove(i);
                    //refreshLV();
                    return true;
                }else {
                    updateDB(getItemID(l2.get(i)), false);// TODO: 2016-10-29
                    getData();
                    //l1.add(l2.get(i));
                    //l2.remove(i);
                    //refreshLV();
                    return true;
                }
            }
        });
        imageButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton_add.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                editText.setText("");
                editText.requestFocus();
                showKB(editText);
                imageButton_ok.setVisibility(View.VISIBLE);
            }
        });

        imageButton_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addTODO();

                editText.setText("");

                //refreshLV();

                hideKB(null);

                imageButton_ok.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                imageButton_add.setVisibility(View.VISIBLE);
            }
        });

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText.setText("");
                return true;
            }
        });

        imageButton_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DEL_MODE){
                    DEL_MODE = false;
                    listView2.setBackgroundColor(getResources().getColor(R.color.lv2_nor));
                }else {
                    DEL_MODE = true;
                    listView2.setBackgroundColor(getResources().getColor(R.color.lv2_del));
                }
            }
        });

        editText.setVisibility(View.GONE);
        imageButton_ok.setVisibility(View.GONE);
        //hideKB();

        getDataini();
    }

    private void updateDB(String _id, boolean isdone) {
        if (isdone){

            db.execSQL("UPDATE " + TB_TODO_NAME +
                    " SET isdone = 1 " +
                    "WHERE _id = " + _id);

        }else {

            db.execSQL("UPDATE " + TB_TODO_NAME +
                    " SET isdone = 0 " +
                    "WHERE _id = " + _id);
        }
    }

    private void delfromDB(String _id) {

        db.execSQL("DELETE FROM " + TB_TODO_NAME +
                " WHERE _id = " + _id);

    }

    private void add2DB(String ttd) {
        db.execSQL("insert into " + TB_TODO_NAME + " (whattodo,isdone,altime) values('" + ttd + "',0,'null')");
    }

    private void showKB(EditText editText) {
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        if (editText != null){
            im.showSoftInput(editText, 0);
        }
    }

    private void refreshLV() {
        a1.notifyDataSetChanged();
        a2.notifyDataSetChanged();
    }

    private void addTODO() {
        if (!editText.getText().toString().isEmpty()){
            add2DB(editText.getText().toString());
            getData();
            //l1.add(editText.getText().toString());
        }
        else {
            Toast.makeText(MainActivity.this, "no txt", Toast.LENGTH_SHORT).show();
        }
    }

    //delay
    private void hideKB() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                hideKB(null);
            }
        };
        timer.schedule(timerTask,200);
    }

    private void hideKB(@Nullable EditText object) {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (object!=null){
            imm.hideSoftInputFromWindow(object.getWindowToken(), 0);
            return;
        }
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    SQLiteDatabase db;
    final String DB_NAME = "todous", TB_TODO_NAME = "tbtodo";
    public void getDataini() {
        db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL("create table if not exists " + TB_TODO_NAME +
                " (_id integer primary key autoincrement," +//auto
                "whattodo text not null," +//内容
                "isdone integer not null," +//done=1?0
                "altime text)");//time=time?null

        getData();
    }

    final String TIME_DIVIDER = " $@ ";
    public void getData() {
        l1.clear();
        l2.clear();
        Cursor c = db.rawQuery("select * from "+TB_TODO_NAME, null);
        if (c != null) {
            c.moveToFirst();
            while (c.moveToNext()) {
                int _id = c.getInt(c.getColumnIndex("_id"));
                String todo = c.getString(c.getColumnIndex("whattodo"));
                int done = c.getInt(c.getColumnIndex("isdone"));
                String time = c.getString(c.getColumnIndex("altime"));
                if (done==0){
                    l1.add(_id+ID_DIVIDER+todo+TIME_DIVIDER+time);
                }else {
                    l2.add(_id+ID_DIVIDER+todo+TIME_DIVIDER+time);
                }
            }
            c.close();
        }
        refreshLV();
    }

    final String ID_DIVIDER = "# ";
    public String getItemID(String s) {
        return s.substring(0,s.indexOf(ID_DIVIDER));
    }

    @Override
    protected void onStop() {
        db.close();

        super.onStop();
    }
}
