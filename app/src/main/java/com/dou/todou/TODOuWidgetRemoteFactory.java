package com.dou.todou;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-11-8.
 */
public class TODOuWidgetRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    public static List<TODOuItem> mList = new ArrayList<>();
    private int mAppWidgetId;

    public TODOuWidgetRemoteFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        dbUtils = new DBUtil(mContext);
    }

    @Override
    public void onCreate() {

        //// TODO: 2016-11-9 ???
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataSetChanged() {


        refreshList();
    }

    DBUtil dbUtils;
    private void refreshList() {



        dbUtils.init();
        mList.clear();
        mList.addAll(dbUtils.getItems(false));
        dbUtils.Release();
    }

    @Override
    public void onDestroy() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    private String cut(String s, int i) {

        if (s.contains("\n"))
            s = s.substring(0, s.indexOf("\n")) + "...";

        if (s.length() > i){
            return s.substring(0, i) + "...";
        }else {
            return s;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= mList.size())
            return null;
        TODOuItem content = mList.get(position);
        // 创建在当前索引位置要显示的View
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.tudou_widget_item);

        // 设置要显示的内容
        rv.setTextViewText(R.id.textView_widget_item_txt, cut(content.what(),20));

//      //填充Intent，填充在AppWdigetProvider中创建的PendingIntent
        // 传入点击行的数据
        Bundle extras = new Bundle();
        extras.putSerializable("item", content);

        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rv.setOnClickFillInIntent(R.id.textView_widget_item_txt, intent);

        return rv;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;//必须返回1，否则一直是正在加载。。。
    }

    @Override
    public long getItemId(int i) {
        return mList.get(i).id_int();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void add(TODOuItem item){
        mList.add(item);
    }

    public void add(int i, TODOuItem item){
        mList.add(i, item);
    }
}
