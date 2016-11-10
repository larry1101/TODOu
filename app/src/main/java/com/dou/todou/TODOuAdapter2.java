package com.dou.todou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016-11-6.
 */
public class TODOuAdapter2<T> extends BaseAdapter {

    public final class ListItem{
        public TextView w,r;
        public LinearLayout l;
        ImageView i;
    }

    private LayoutInflater inflater;
    private List data;

    public TODOuAdapter2(Context context, List data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ListItem listitem = null;
        TODOuItem item = (TODOuItem) data.get(position);
        if (view == null){
            listitem = new ListItem();

            view = inflater.inflate(R.layout.list_item_done, null);

            listitem.w = (TextView)view.findViewById(R.id.textView_done_w);
            listitem.r = (TextView)view.findViewById(R.id.textView_done_r);
            listitem.l = (LinearLayout)view.findViewById(R.id.linearLayout_done_bg);

            listitem.i = (ImageView)view.findViewById(R.id.imageView_alarm_status);

            view.setTag(listitem);

        }else {
            listitem = (ListItem)view.getTag();
        }

        listitem.w.setText(cut(item.what(),15));

        if (item.isDone && item.hasResult()){
            listitem.r.setVisibility(View.VISIBLE);
            listitem.r.setText(cut(item.result(),20));
        }else {
            listitem.r.setVisibility(View.GONE);
        }

//        if (item.needAlarm && item.needNotification){
//            view.setBackgroundResource(R.color.colorANItem);
//        }else if(item.needAlarm) {
//            view.setBackgroundResource(R.color.colorAlarmItem);
//        }else if(item.needNotification){
//            view.setBackgroundResource(R.color.colorNotifyItem);
//        }else if(item.hasDT){
//            view.setBackgroundResource(R.color.colorDTItem);
//        }else {
//            view.setBackgroundResource(R.color.TRANS);
//        }


        if (item.needAlarm && item.needNotification){
            listitem.l.setBackgroundResource(R.color.colorANItem);

            listitem.i.setVisibility(View.VISIBLE);
            listitem.i.setBackgroundResource(android.R.drawable.ic_popup_reminder);

        }else if(item.needAlarm) {
            listitem.l.setBackgroundResource(R.color.colorAlarmItem);

            listitem.i.setVisibility(View.VISIBLE);
            listitem.i.setBackgroundResource(android.R.drawable.ic_popup_reminder);

        }else if(item.needNotification){
            listitem.l.setBackgroundResource(R.color.colorNotifyItem);

            listitem.i.setVisibility(View.VISIBLE);
            listitem.i.setBackgroundResource(android.R.drawable.ic_menu_info_details);

        }else if(item.hasDT){
            listitem.l.setBackgroundResource(R.color.colorDTItem);

            listitem.i.setVisibility(View.VISIBLE);
            listitem.i.setBackgroundResource(android.R.drawable.ic_menu_recent_history);

        }else {
            listitem.l.setBackgroundResource(R.color.TRANS);

            listitem.i.setVisibility(View.GONE);
        }

        return view;
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
}
