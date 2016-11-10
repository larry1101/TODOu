package com.dou.todou;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bigkoo.pickerview.TimePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class ItemDetail extends AppCompatActivity {

    private static final String APP_NAME = "TODOu";
    EditText editText_w, editText_r;

    TimePickerView timePickerView, timePickerView_t;

    TextView tv_d,tv_t;

    ToggleButton toggleButton;
    LinearLayout linearLayout;
    boolean
            //needAlarm = false,
            alarm = false,
            notification = false
                    ;
    Switch s_a, s_n;

    boolean what_in_edit = false, result_in_edit = false;

    DBUtil dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(APP_NAME, "Creating");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbUtils = new DBUtil(this);

        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout_alarms);

        s_a = (Switch)findViewById(R.id.switch1);
        s_n = (Switch)findViewById(R.id.switch2);

        editText_w = (EditText)findViewById(R.id.editText_what);
        editText_r = (EditText)findViewById(R.id.editText_results);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            item = (TODOuItem) bundle.get("item");
        }else {
            item  = new TODOuItem();
        }

        iniDT();
        iniUI();


        setListeners();

        setTimePickers();

//         : 2016-11-4 上面两个方法似乎会改变item的时间

        itemori.copy(item);

        if (item.isDone){
            if (editText_r.getText().toString().isEmpty()){
                showKBini(editText_r);
//                showKB(editText_w);
            }else {
                hideKB(null);
            }
        }else {
            if (editText_w.getText().toString().isEmpty()){
                showKBini(editText_w);
            }else {
                hideKB(null);
            }
        }


    }

    private void showKBini(final EditText editText) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            showKB(editText);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 234);
    }

    private void iniDT() {
        if (item!=null){

            if (item.isNew()){
//                setTitle("New TODOu");
                item.setDT(new Date());
//
//                if (item.hasDT){
//                    toggleButton.setChecked(true);
//                }else {
//                    toggleButton.setChecked(false);
//                    linearLayout.setVisibility(View.GONE);
//                }
//
//                editText_r.setVisibility(View.GONE);

//                s_a.setChecked(false);
//                s_n.setChecked(false);
//                onAlarmNNotificationChanged();

            }else {
//                setTitle(" # " + item.cont.get("id"));
//
//                editText_w.setText(item.cont.get("what"));
//
//                if (item.isDone){
//                    editText_r.setVisibility(View.VISIBLE);
//                    if (item.hasResult()){
//                        editText_r.setText(item.cont.get("result"));
//                    }
//                }else {
//                    editText_r.setVisibility(View.GONE);
//                }

                if (item.hasDT){//// : 2016-11-4 这里会改变item的日期
//                    toggleButton.setChecked(true);
                    setDT();
                }else {
                    item.setDT(new Date());
//                    toggleButton.setChecked(false);
//                    linearLayout.setVisibility(View.GONE);
                }

//                s_a.setChecked(item.needAlarm);
//                s_n.setChecked(item.needNotification);
//                onAlarmNNotificationChanged();
            }

        }else {
//            item = new TODOuItem();
//            item.setDT(new Date());
//            setTitle("New TODOu");
//            editText_r.setVisibility(View.GONE);
//            s_a.setChecked(false);
//            s_n.setChecked(false);
//            onAlarmNNotificationChanged();
//            toggleButton.setChecked(false);
//            linearLayout.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        s_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.needAlarm = b;
                onAlarmNNotificationChanged();
            }
        });

        s_n.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.needNotification = b;
                onAlarmNNotificationChanged();
            }
        });

        editText_w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (what_in_edit){
                    hideKB(editText_w);
                    what_in_edit = false;
                }else{
                    showKB(editText_w);
                    what_in_edit = true;
                }
            }
        });
        editText_w.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText_w.setText("");
                showKB(editText_w);
                return true;
            }
        });
        editText_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result_in_edit){
                    hideKB(editText_r);
                    result_in_edit = false;
                }else{
                    showKB(editText_r);
                    result_in_edit = true;
                }
            }
        });
        editText_r.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText_r.setText("");
                showKB(editText_r);
                return true;
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.hasDT = b;
                if (b){
                    linearLayout.setVisibility(View.VISIBLE);
                }else {
                    linearLayout.setVisibility(View.GONE);
//                    item.cancleAlarm(ItemDetail.this);
                }
            }
        });
    }

    private void onAlarmNNotificationChanged() {
        if (item.needAlarm||item.needNotification){
            findViewById(R.id.tableRow_time).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.tableRow_time).setVisibility(View.GONE);
        }
    }

    private void setTimePickers() {
        tv_d = (TextView)findViewById(R.id.textView_d);
        tv_t = (TextView)findViewById(R.id.textView_t);

        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);

        timePickerView.setTime(date);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);

        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                tv_d.setText(getDate(date));
                SimpleDateFormat format = new SimpleDateFormat("yyyy - MM - dd HH : mm");
                try {
                    item.setDT(format.parse(tv_d.getText().toString() + " " + tv_t.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        tv_d.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePickerView.show();
            }
        });


        timePickerView_t = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);

        timePickerView_t.setTime(date);
        timePickerView_t.setCyclic(true);
        timePickerView_t.setCancelable(true);

        //时间选择后回调
        timePickerView_t.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                tv_t.setText(getTime(date));
                SimpleDateFormat format = new SimpleDateFormat("yyyy - MM - dd HH : mm");
                try {
                    item.setDT(format.parse(tv_d.getText().toString() + " " + tv_t.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        //弹出时间选择器
        tv_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePickerView_t.show();
            }
        });

        tv_d.setText(getDate(date));
        tv_t.setText(getTime(date));
//        setContentView(R.layout.item_detail);
//        timePicker = (TimePicker)findViewById(R.id.timePicker);
//        timePicker.setIs24HourView(true);
//        TimeListener timeListener = new TimeListener();
//        timePicker.setOnTimeChangedListener(timeListener);
    }

    public static String getTime(Date date) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format = new SimpleDateFormat("HH : mm");
        return format.format(date);
    }

    public static String getDate(Date date) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format = new SimpleDateFormat("yyyy - MM - dd");
        return format.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_act_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (item.isNew()){
            menu.findItem(R.id.action_bar_delete).setVisible(false);
        }else {
            menu.findItem(R.id.action_bar_delete).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_bar_save:
                //保存
                onSaveItem();
                return true;
            case R.id.action_bar_cancel:
                //重置
                this.item.copy(itemori);
                iniUI();
                hideKB(null);
                return true;
            case R.id.action_bar_delete:
                //删除
//                Toast.makeText(ItemDetail.this, "Delete", Toast.LENGTH_SHORT).show();
                onDelItem();
                return true;
            case 16908332://R.id.home:
                //返回，可弹出保存提示框
                //item+change？
                wannaChange();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //// TODO: 2016-11-4 优化
    private void wannaChange() {

        if (!editText_w.getText().toString().isEmpty()){
            item.setWhat(editText_w.getText().toString());
        }else {
            item.setWhat(item.NULL);
        }

        if (item.isDone){
            if (!editText_r.getText().toString().isEmpty()){
                item.setResult(editText_r.getText().toString());
            }
        }


        if (item.equals(itemori)){
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetail.this);
        builder.setMessage("Unsaved, back ? ");
        builder.setTitle("Back");
        builder.setIcon(android.R.drawable.presence_busy);
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

//    protected void dialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetail.this);
//        builder.setMessage("Delete ? ");
//        builder.setTitle("DEL Item");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//             @Override
//             public void onClick(DialogInterface dialog, int which) {
//                  dialog.dismiss();
//                 }
//            });
//        builder.setNegativeButton("False", new DialogInterface.OnClickListener() {
//             @Override
//             public void onClick(DialogInterface dialog, int which) {
//                  dialog.dismiss();
//                 }
//            });
//        builder.create().show();
//        }

    private void onDelItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetail.this);
        builder.setMessage("Delete ? ");
        builder.setTitle("DEL Item");
        builder.setIcon(android.R.drawable.ic_delete);
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (item.isNew()){
                    finish();
                }else {
                    deleteItem();
                    finish();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void deleteItem() {
        if (!dbUtils.delete(item))
            Toast.makeText(ItemDetail.this, "del failed", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ItemDetail.this, "del success", Toast.LENGTH_SHORT).show();
    }

    TODOuItem item, itemori = new TODOuItem();
    private void iniUI() {

        editText_w.setText("");

        if (item!=null){

            if (item.isNew()){
                setTitle("New TODOu");
//                item.setDT(new Date());

                if (item.hasDT){
                    toggleButton.setChecked(true);
                }else {
                    toggleButton.setChecked(false);
                    linearLayout.setVisibility(View.GONE);
                }

                editText_r.setVisibility(View.GONE);

                s_a.setChecked(false);
                s_n.setChecked(false);
                onAlarmNNotificationChanged();

            }else {
                setTitle(" # " + item.cont.get("id"));

                editText_w.setText(item.cont.get("what"));

                if (item.isDone){
                    editText_r.setVisibility(View.VISIBLE);
                    if (item.hasResult()){
                        editText_r.setText(item.cont.get("result"));
                    }
                }else {
                    editText_r.setVisibility(View.GONE);
                }

                if (item.hasDT){//// : 2016-11-4 这里会改变item的日期
                    toggleButton.setChecked(true);
                    setDT();
                }else {
//                    item.setDT(new Date());
                    toggleButton.setChecked(false);
                    linearLayout.setVisibility(View.GONE);
                }

                s_a.setChecked(item.needAlarm);
                s_n.setChecked(item.needNotification);
                onAlarmNNotificationChanged();
            }

        }else {
            item = new TODOuItem();
            item.setDT(new Date());
            setTitle("New TODOu");
            editText_r.setVisibility(View.GONE);
            s_a.setChecked(false);
            s_n.setChecked(false);
            onAlarmNNotificationChanged();
            toggleButton.setChecked(false);
            linearLayout.setVisibility(View.GONE);
        }

    }

    Date date= new Date();// // TODO: 2016-11-2 重复
    //Date time = new Date();
    private void setDT(/*TODOuItem item*/) {

        date = item.getDT();

    }

    @Override
    protected void onStop() {
        dbUtils.Release();

//        if (getIntent().getBooleanExtra("has_parent", false)){
//            notifyParentRefresh();
//        }
        notifyParentRefresh();//为了刷新widget

        super.onStop();
    }

    private void notifyParentRefresh() {

        Intent b = new Intent("com.dou.todou.notifyParentRefresh");
        sendBroadcast(b);
    }

    private void onSaveItem() {

        if (editText_w.getText().toString().isEmpty()) {
            Toast.makeText(ItemDetail.this, "No inputs...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (item.isDone) {
            if (!editText_r.getText().toString().isEmpty()) {
                item.setResult(editText_r.getText().toString());
            } else {
                item.setResult(TODOuItem.NULL);
            }
        }

        //// : 2016-11-2 check new
        if (item.isNew()) {
            //Toast.makeText(ItemDetail.this, "Saving new item", Toast.LENGTH_SHORT).show();
            item.setWhat(editText_w.getText().toString());

            insertItem();

            item.setID(getID());
        } else {
            item.setWhat(editText_w.getText().toString());
            updateItem();
        }
        //// : 2016-11-2 check done

        ////  2016-11-2 save what

        if (toggleButton.isChecked()){
            //俩都有或者只有闹钟，设闹钟，闹钟自带notification
            if (item.needAlarm) {
                if (item.isPassed()) {
                    Toast.makeText(ItemDetail.this, "TODOU passed, no alarm", Toast.LENGTH_SHORT).show();
                } else {
//                Toast.makeText(ItemDetail.this, "TODOU alarm setting", Toast.LENGTH_SHORT).show();
                    setAlarm();
                }
            } else if (item.needNotification) {
                if (item.isPassed()) {
                    Toast.makeText(ItemDetail.this, "TODOU passed, no notification", Toast.LENGTH_SHORT).show();
                } else {
//                Toast.makeText(ItemDetail.this, "TODOU notification setting", Toast.LENGTH_SHORT).show();
                    setNotification();
                }
            } else {
                item.cancleAlarm(this);
            }
        }


        finish();
    }

    private void setNotification() {
//        Toast.makeText(ItemDetail.this, "Notification Setter ", Toast.LENGTH_SHORT).show();
        //// : 2016-11-2

        item.setNotification(this);
        Toast.makeText(ItemDetail.this, "Notification sat", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        //// : 2016-11-2
//        Toast.makeText(ItemDetail.this, "Alarm Setter ", Toast.LENGTH_SHORT).show();

        item.setAlarm(this);
        Toast.makeText(ItemDetail.this, "Alarm sat", Toast.LENGTH_SHORT).show();
    }

    private void insertItem() {
        if (dbUtils.insert(item)){
//            Toast.makeText(ItemDetail.this, "save success", Toast.LENGTH_SHORT).show();
            Log.v(APP_NAME, "Save success");
        }
        else{
            Toast.makeText(ItemDetail.this, "save failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItem() {
        if (dbUtils.update(item)){
            Log.v(APP_NAME, "Update success");
//            Toast.makeText(ItemDetail.this, "Update succeed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(ItemDetail.this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKB(@Nullable EditText editText) {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            if (editText == null){
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }else {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.clearFocus();
            }

        }
    }

    private void showKB(EditText editText) {
        if (editText == null) return;
        editText.requestFocus();
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(editText, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            wannaChange();
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getID() {

        try {
            return dbUtils.getNewestItemID();
        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return "0";
    }
}
