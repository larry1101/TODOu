package com.dou.todou;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2016-11-6.
 */
public class TODOuItem implements Serializable {

    public final static String NULL = "DNULL";
    private static final String APP_NAME = "TODOu";

    HashMap<String,String> cont = new HashMap<>();

    public boolean isDone = false,
            hasDT = false,
            needAlarm = false,
            needNotification = false;

    public void setID(String id) {
        cont.put(ID, id);
    }
    //            hasResult = false,

    public void copy(TODOuItem item_src){
        if (item_src == null){
            return;
        }
        this.cont.clear();
        this.cont.putAll(item_src.cont);
        isDone = item_src.isDone;
        hasDT = item_src.hasDT;
        needAlarm = item_src.needAlarm;
        needNotification = item_src.needNotification;
    }

    public TODOuItem(TODOuItem item_src) {
        this.cont.putAll(item_src.cont);
        isDone = item_src.isDone;
        hasDT = item_src.hasDT;
        needAlarm = item_src.needAlarm;
        needNotification = item_src.needNotification;

    }

    private boolean contEqual(TODOuItem item){

        if (isNew()){
            if (item.what().equals(what()) && item.getDT_str().equals(getDT_str()) && item.result().equals(result())){
                return true;
            }else {
                return false;
            }
        }else {
            if (item.id().equals(id()) && item.what().equals(what()) && item.getDT_str().equals(getDT_str()) && item.result().equals(result())){
                return true;
            }else {
                return false;
            }
        }
        
    }

    //    @Override
    public boolean equals(TODOuItem item_cmp) {

        if (item_cmp == null){
            return false;
        }

        if (!contEqual(item_cmp)){
            return false;
        }

        if (!(item_cmp.hasDT == hasDT)){
            return false;
        }

        if (!(item_cmp.isDone == isDone)){
            return false;
        }

        if (!(item_cmp.needAlarm == needAlarm)){
            return false;
        }

        if (!(item_cmp.needNotification == needNotification)){
            return false;
        }

        return true;

//        if ((item_cmp.hasDT == hasDT) && (item_cmp.isDone == isDone) && (item_cmp.needAlarm == needAlarm) && (item_cmp.needNotification == needNotification)){
//            return true;
//        }
//        else
//        {
//            return false;
//        }
    }

    // TODO: 2016-11-2 四个bool用一个int来存，以FLAG的形式&出bool，这样在数据库里面应该相对节省空间吧…… :: 1, 2, 4, 8
    public void done(boolean b){
        // TODO: 2016-11-1 update db
        isDone = b;
    }
    public void done(){
        // TODO: 2016-11-1 update db
        isDone = true;
    }

    //// TODO: 2016-11-2 become private and make interfaces
    public final static String
              ID = "id"
            , WHAT = "what"
            , TIME = "time"
            , RESULT = "result";

    public TODOuItem(String id, String what, String result, String time,boolean hasDT, boolean is_Done, boolean needAlarm, boolean needNotification){
        this.hasDT = hasDT;
        this.isDone = is_Done;
        this.needAlarm = needAlarm;
        this.needNotification = needNotification;
        cont.put(ID,id);
        cont.put(WHAT,what);
        cont.put(TIME,time);
        cont.put(RESULT,result);
    }


    public TODOuItem(boolean hasDT){
        cont.put(ID,NULL);
        cont.put(WHAT,NULL);
        cont.put(TIME,NULL);
        this.hasDT = hasDT;
    }

    public TODOuItem(String what){
        cont.put(ID,NULL);
        cont.put(WHAT,what);
        cont.put(TIME,NULL);
        setDT(new Date());
    }

    public TODOuItem(){
        cont.put(ID,NULL);
        cont.put(WHAT,NULL);
        cont.put(TIME,NULL);
    }

    public boolean hasResult(){
        if (cont.get(RESULT) == null){
            return false;
        }
        if (cont.get(RESULT).equals(NULL)){
            return false;
        }else {
            return true;
        }
    }

    public boolean isNew(){
        if (cont.get(ID) == null){
            return false;
        }
        if (cont.get(ID).equals(NULL)){
            return true;
        }else {
            return false;
        }
    }

    public void setDT(Date date){
        SimpleDateFormat format_t = new SimpleDateFormat("yyyyMMddHHmm");
        cont.put(TIME,format_t.format(date));
    }

    //有日日期返回日期，有精确时间返回精确时间，没有则返回现在的时间
    public Date getDT(){
        Date date = new Date();

        if (!hasDT) {
            return date;
        }

        String d = cont.get(TIME);

        try{
            SimpleDateFormat format_t = new SimpleDateFormat("yyyyMMddHHmm");
            date = format_t.parse(d);

        }catch (Exception e){
            e.printStackTrace();
        }

        return date;
    }
    public String getDT_str() {
        return cont.get(TIME);
    }

    public int DEF_LEN = 25;
    @Override
    public String toString() {
        String what = cont.get(WHAT);

        if (hasResult()){
            what = "(" + result().substring(0, 10>result().length()?result().length():10) + ")" + what;
        }
        // TODO: 2016-11-6 用自定义item代替

        if (what == null){
            return "null";
        }
        if (what.length()>DEF_LEN){
            what = what.substring(0, DEF_LEN>what.length()?what.length():DEF_LEN) + " ...";
        }
        if (what.contains("\n")){
            what = what.substring(0, what.indexOf("\n")) + " ...";
        }



        return what;
    }

    public void setWhat(String what) {
        this.cont.put(WHAT,what);
    }

    public String what() {
        return cont.get(WHAT);
    }
    
    public boolean isPassed() {
        return getDT().before(new Date());
    }

    public int hasDT_int() {
        if (hasDT){
            return 1;
        }else {
            return 0;
        }
    }

    public int needNotification_int() {
        if (needNotification){
            return 1;
        }else {
            return 0;
        }
    }

    public int needAlarm_int() {
        if (needAlarm){
            return 1;
        }else {
            return 0;
        }
    }

    public int isDone_int() {
        if (isDone){
            return 1;
        }else {
            return 0;
        }
    }
    
    public String id() {
        return cont.get(ID);
    }

    public String result() {
        if (hasResult()){
            return cont.get(RESULT);
        }
        else {
            return NULL;
        }
    }

    public void setResult(String result) {
        this.cont.put(RESULT, result);
    }

    public boolean setAlarm(Context c){

        Date date = getDT();

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.dou.todou.alarm");

        intent.putExtra("what", toString());
        intent.putExtra("act", "alarm");

        intent.setClass(c, TODOuAlarmReciver.class);

//        PendingIntent sender = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent sender = PendingIntent.getBroadcast(c, id_int(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);

        return true;
    }

    public int id_int() {

        if (isNew()){
            return 0;
        }

        try {
            return Integer.parseInt(cont.get(ID));
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public void setNotification(Context c) {

        Date date = getDT();

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.dou.todou.alarm");

        intent.putExtra("what", toString());
        intent.putExtra("act", "notify");

        intent.setClass(c, TODOuAlarmReciver.class);

//        PendingIntent sender = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent sender = PendingIntent.getBroadcast(c, id_int(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);
    }

    public void cancleAlarm(Context c) {
        //// TODO: 2016-11-6 cancle alarm
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.dou.todou.alarm");
        intent.setClass(c, TODOuAlarmReciver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_NO_CREATE);
        PendingIntent sender = PendingIntent.getBroadcast(c, id_int(), intent, PendingIntent.FLAG_NO_CREATE);
        if (sender != null){
            Log.i(APP_NAME,"cancel alarm");
            am.cancel(sender);
        }else{
            Log.i(APP_NAME,"sender == null");
        }
    }
}
