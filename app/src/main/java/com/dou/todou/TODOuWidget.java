package com.dou.todou;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class TODOuWidget extends AppWidgetProvider {

    public static final String BUTTON_ADD = "com.dou.tudou.wiget.button.add";
    public static final String BUTTON_MAIN = "com.dou.tudou.wiget.button.main";
    public static final String BUTTON_REFRESH = "com.dou.tudou.wiget.button.refresh";
    public static final String LIST_ITEM = "com.dou.tudou.wiget.list.item";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todou_widget);

        //btn_add
        Intent i = new Intent(BUTTON_ADD);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, i, 0);
        views.setOnClickPendingIntent(R.id.button_widget_add, p);

        //btn_refresh
        Intent ir = new Intent(BUTTON_REFRESH);
        PendingIntent pr = PendingIntent.getBroadcast(context, 0, ir, 0);
        views.setOnClickPendingIntent(R.id.imageButton_widget_refresh, pr);

        //btn_home
        Intent ih = new Intent(BUTTON_MAIN);
        PendingIntent ph = PendingIntent.getBroadcast(context, 0, ih, 0);
        views.setOnClickPendingIntent(R.id.imageButton_widget_to_main, ph);



//        views.setTextViewText(R.id.appwidget_text, widgetText);

        //bindservice
        Intent intent_bind2service = new Intent(context, TODOuWidgetRemoteService.class);
        intent_bind2service.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent_bind2service.setData(Uri.parse(intent_bind2service.toUri(Intent.URI_INTENT_SCHEME)));//???????/ : 2016-11-10 好像是为了比较,,,,,,
        views.setRemoteAdapter(R.id.listView_widget_todo, intent_bind2service);
        views.setEmptyView(R.id.listView_widget_todo, R.layout.none_data);


//as list_item_click
        Intent ili = new Intent(context, TODOuWidget.class);
        ili.setAction(LIST_ITEM);
        ili.setData(Uri.parse(ili.toUri(Intent.URI_INTENT_SCHEME)));//??????????// : 2016-11-10 好像是为了比较,,,,,,
//        PendingIntent pli = PendingIntent.getBroadcast(context, 0, ili, 0);
        PendingIntent pli = PendingIntent.getBroadcast(context, 0, ili, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.listView_widget_todo, pli);
        views.setPendingIntentTemplate(R.id.listView_widget_todo, pli);//!!!
//        Intent intent_on_list_click = new Intent(context, TODOuWidget.class);//????????????????


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent b = new Intent("com.dou.todou.notifyParentRefresh");
        context.sendBroadcast(b);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BUTTON_ADD)) {
//            Toast.makeText(context, "add item", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context, ItemDetail.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // TODO: 2016-11-7 这样不好，听说容易 ANR
            context.startActivity(intent1);
        }else if (intent.getAction().equals(BUTTON_MAIN)) {
//            Toast.makeText(context, "add item", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context, TODOuEnt.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // TODO: 2016-11-7 这样不好，听说容易 ANR
            context.startActivity(intent1);
        }else if (intent.getAction().equals(LIST_ITEM)) {
//            Toast.makeText(context, "add item", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context, ItemDetail.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtras(intent.getExtras());
            // TODO: 2016-11-7 这样不好，听说容易 ANR
            context.startActivity(intent1);
        }else if (intent.getAction().equals("com.dou.todou.notifyParentRefresh")){
//            refreshList(context);
            try {
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.todou_widget);
//                TODOuWidgetRemoteFactory factory = new TODOuWidgetRemoteFactory(context, intent);
                final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                final ComponentName cn = new ComponentName(context,
                        TODOuWidget.class);

//                factory.add(new TODOuItem("1","test","t","201611082347",false,false,false,false));

                // 这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                        R.id.listView_widget_todo);
                mgr.updateAppWidget(mgr.getAppWidgetIds(cn), rv);
            }catch (Exception e){e.printStackTrace();}

        }else if (intent.getAction().equals(BUTTON_REFRESH)){
            try {
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.todou_widget);
                AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context,
                        TODOuWidget.class);
                // 这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listView_widget_todo);
                mgr.updateAppWidget(mgr.getAppWidgetIds(cn), rv);
            }catch (Exception e){e.printStackTrace();}
        }

        super.onReceive(context, intent);
    }

}

