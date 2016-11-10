package com.dou.todou;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016-11-6.
 */
public class TODOuAdapter <T> extends ArrayAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<T> data;


    public TODOuAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

//    public TODOuAdapter(Context context, List objects) {
////        super(context, R.layout.todou_list_item, objects);
//        this.data = objects;
//        this.context=context;
//        this.layoutInflater=LayoutInflater.from(context);
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TODOuItem item = (TODOuItem)getItem(position);

        if (item.needAlarm && item.needNotification){
            view.setBackgroundResource(R.color.colorANItem);
        }else if(item.needAlarm) {
            view.setBackgroundResource(R.color.colorAlarmItem);
        }else if(item.needNotification){
            view.setBackgroundResource(R.color.colorNotifyItem);
        }else if(item.hasDT){
            view.setBackgroundResource(R.color.colorDTItem);
        }else {
            view.setBackgroundResource(R.color.TRANS);
        }

        if (item.isDone){
//            view.setBackgroundResource(R.color.colorDoneItem);
        }

        return view;
    }
}
