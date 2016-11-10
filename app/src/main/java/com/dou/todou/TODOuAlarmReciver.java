package com.dou.todou;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.Date;

public class TODOuAlarmReciver extends BroadcastReceiver {

    Context c;

    public TODOuAlarmReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // : This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        c = context;
        if (intent.getAction().equals("com.dou.todou.alarm")){
            String act = intent.getStringExtra("act");
            if (act != null){
                String what;
                what = intent.getStringExtra("what")==null?"null":intent.getStringExtra("what");
                if (act.equals("alarm")){

                    Alarm(what);
                }else if (act.equals("notify")){

                    Notification(what);
                }
            }
        }
    }

    private void Notification(String what) {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c);

        Intent intent = new Intent(c, TODOuEnt.class);
        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, intent ,0);

        mBuilder
                .setContentTitle("TODOu")//设置通知栏标题
                .setContentText(what) //设置通知栏显示内容
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //.setDefaults(Notification.DEFAULT_SOUND)
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                // TODO: 2016-11-6 华为不起作用啊…………
                .setSmallIcon(R.mipmap.ic_todou)//设置通知小ICON
                .setContentIntent(contentIntent)
        ;

        mNotificationManager.notify(0,mBuilder.build());
    }

    private void Alarm(String what) {
        //// TODO: 2016-11-6 alarm.....
        Notification(what);
    }
}
