package com.dou.todou.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dou.todou.DBUtil;
import com.dou.todou.ItemDetail;
import com.dou.todou.R;
import com.dou.todou.TODOuAdapter2;
import com.dou.todou.TODOuItem;

import java.util.ArrayList;

public class FragmentDONE extends Fragment {

    private static final String APP_NAME = "TODOu";

    Context c;
    DBUtil dbUtils;

    RelativeLayout r_del;
    ImageButton imageButton_del;
    boolean DEL_MODE = false;

    ListView listView_done;
    ArrayList<TODOuItem> list_done = new ArrayList<>();
//    TODOuAdapter<TODOuItem> adapter;
    TODOuAdapter2<TODOuItem> adapter;

    private volatile View self;

    private static FragmentDONE instance;

    public FragmentDONE setContext(Context c){
        this.c = c;
        return this;
    }

    @SuppressLint("ValidFragment") private FragmentDONE() {
    }


    public static FragmentDONE getInstance() {
        if (instance == null) {
            synchronized (FragmentDONE.class) {
                if (instance == null) instance = new FragmentDONE();
            }
        }
        return instance;
    }


    @SuppressLint("InflateParams") @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(APP_NAME, "Fragment DONE created");

        if (this.self == null) {
            this.self = inflater.inflate(R.layout.activity_fragment_done, null);
        }
        if (this.self.getParent() != null) {
            ViewGroup parent = (ViewGroup) this.self.getParent();
            parent.removeView(this.self);
        }

        dbUtils = new DBUtil(c);

        iniViews(self);

        setListeners();

        iniUI();

        registerMessageReceiver();

        return this.self;
    }

    private void iniUI() {

        refreshList();
    }

    private void refreshList() {

        dbUtils.init();
        list_done.clear();
        list_done.addAll(dbUtils.getItems(true));
        dbUtils.Release();
        adapter.notifyDataSetChanged();

    }

    //被告知刷新
    //在销毁时要与广播解绑
    @Override
    public void onDestroy() {
        c.unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public MessageReceiver mMessageReceiver;

    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction("com.dou.todou.notifyParentRefresh");
        c.registerReceiver(mMessageReceiver, filter);
    }

    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.dou.todou.notifyParentRefresh")){
                refreshList();
            }
        }

    }

    private void setListeners() {

        r_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DEL_MODE){
                    DEL_MODE = false;
                    listView_done.setBackgroundResource(R.color.lv2_nor);
                }else {
                    DEL_MODE = true;
                    listView_done.setBackgroundResource(R.color.lv2_del);
                }
            }
        });

        imageButton_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DEL_MODE){
                    DEL_MODE = false;
                    listView_done.setBackgroundResource(R.color.lv2_nor);
                }else {
                    DEL_MODE = true;
                    listView_done.setBackgroundResource(R.color.lv2_del);
                }
            }
        });

        listView_done.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(c,ItemDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item",list_done.get(i));
                intent.putExtras(bundle);
                intent.putExtra("has_parent",true);
                startActivity(intent);
            }
        });

        listView_done.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (DEL_MODE){
                    onDelItem(list_done.get(i));
                }else {
                    switchdone(list_done.get(i));
                }


                return true;
            }
        });
    }

    private void onDelItem(final TODOuItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage("Delete ? ");
        builder.setTitle("DEL Item");
        builder.setIcon(android.R.drawable.ic_delete);
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (item.isNew()){
                    Toast.makeText(c, "Not Deleteable", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    deleteItem(item);
                    notifyTDRefresh();
                    return;
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

    private void deleteItem(TODOuItem item) {
        dbUtils.init();
        if (dbUtils.delete(item)){
            Log.v(APP_NAME, "Item deleted");
        }else {
            Toast.makeText(c, "del failed", Toast.LENGTH_SHORT).show();
            Log.e(APP_NAME, "Delete item failed");
        }
        dbUtils.Release();
    }

    private void switchdone(TODOuItem item) {
        if (item.isDone){
            item.isDone = false;
        }else {
            item.isDone = true;
        }
        updateItem(item);
        notifyTDRefresh();
    }

    private void updateItem(TODOuItem item) {
        dbUtils.init();
        if (dbUtils.update(item)){
//            Toast.makeText(c, "done", Toast.LENGTH_SHORT).show();
            Log.v(APP_NAME,"Item done");
        }else {
            Toast.makeText(c, "Update failed", Toast.LENGTH_SHORT).show();
            Log.e(APP_NAME, "Update failed");
        }
        dbUtils.Release();
    }

    private void notifyTDRefresh() {
        Intent b = new Intent("com.dou.todou.notifyParentRefresh");
        c.sendBroadcast(b);
    }

    private void iniViews(View self) {
        imageButton_del = (ImageButton)self.findViewById(R.id.imageButton_del);
        r_del = (RelativeLayout)self.findViewById(R.id.relativeLayout_del);

        listView_done = (ListView)self.findViewById(R.id.listView_done);
//        adapter = new TODOuAdapter<>(c,android.R.layout.simple_expandable_list_item_1, list_done);
        adapter = new TODOuAdapter2<>(c, list_done);
        listView_done.setAdapter(adapter);

    }
}
