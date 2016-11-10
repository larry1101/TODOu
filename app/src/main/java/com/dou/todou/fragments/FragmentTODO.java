package com.dou.todou.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dou.todou.DBUtil;
import com.dou.todou.ItemDetail;
import com.dou.todou.R;
import com.dou.todou.TODOuAdapter;
import com.dou.todou.TODOuAdapter2;
import com.dou.todou.TODOuItem;

import java.util.ArrayList;
import java.util.Date;

public class FragmentTODO extends Fragment {

    private static final String APP_NAME = "TODOu";

    private volatile View self;

    private Context c;
    DBUtil dbUtils;

    ListView listView_todo;
    ArrayList<TODOuItem> list_todo = new ArrayList<>();
    TODOuAdapter2<TODOuItem> adapter;

    EditText editText_new_item;
    ImageButton imageButton_ok,imageButton_add;
    RelativeLayout relativeLayout_new_item;

    private static FragmentTODO instance;

    public FragmentTODO setContext(Context c){
        this.c = c;
        return this;
    }

    @SuppressLint("ValidFragment") private FragmentTODO() {
    }

    public static FragmentTODO getInstance() {
        if (instance == null) {
            synchronized (FragmentTODO.class) {
                if (instance == null) instance = new FragmentTODO();
            }
        }
        return instance;
    }

    @SuppressLint("InflateParams") @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(APP_NAME, "Fragment TODO created");

        if (this.self == null) {
            this.self = inflater.inflate(R.layout.activity_fragment_todo, null);
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

        hide(relativeLayout_new_item);

        refreshList();
    }

    private void refreshList() {
        dbUtils.init();
        list_todo.clear();
        list_todo.addAll(dbUtils.getItems(false));
        dbUtils.Release();
        adapter.notifyDataSetChanged();
    }

    //被告知刷新
    //在销毁时要与广播解绑
    @Override
    public void onDestroy() {
        c.unregisterReceiver(mMessageReceiver);
        c = null;
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

        editText_new_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText_new_item.setText("");
                return true;
            }
        });

        imageButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(imageButton_add);
                show(relativeLayout_new_item);
                showKB(editText_new_item);
                editText_new_item.requestFocus();
            }
        });
        
        imageButton_add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TODOuItem item = new TODOuItem();
                Intent intent = new Intent(c,ItemDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item",item);
                intent.putExtras(bundle);
                intent.putExtra("has_parent",true);
                startActivity(intent);
                return true;
            }
        });

        imageButton_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuackAddIten();
                hide(relativeLayout_new_item);
                hideKB(editText_new_item);
                show(imageButton_add);
                editText_new_item.setText("");
            }
        });

        listView_todo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(c,ItemDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item",list_todo.get(i));
                intent.putExtras(bundle);
                intent.putExtra("has_parent",true);
                startActivity(intent);
            }
        });

        listView_todo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                switchdone(list_todo.get(i));
                return true;
            }
        });
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

    private void QuackAddIten() {
        if (editText_new_item.getText().toString().isEmpty()){
            return;
        }
        TODOuItem item = new TODOuItem(editText_new_item.getText().toString());
        dbUtils.init();
        dbUtils.insert(item);
        dbUtils.Release();
        notifyTDRefresh();
    }

    private void showKB(EditText editText) {
        InputMethodManager im = ((InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE));
        if (editText != null && im != null){
            editText.requestFocus();
            im.showSoftInput(editText, 0);
        }
    }

    private void show(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void iniViews(View self) {
        editText_new_item = (EditText)self.findViewById(R.id.editText_new_item);

        imageButton_ok = (ImageButton)self.findViewById(R.id.imageButton_go);
        imageButton_add = (ImageButton)self.findViewById(R.id.imageButton_add);
        show(imageButton_add);

        relativeLayout_new_item = (RelativeLayout)self.findViewById(R.id.relativeLayout_new_items);

        listView_todo = (ListView)self.findViewById(R.id.listView_todo);
        adapter = new TODOuAdapter2<>(c, list_todo);
        listView_todo.setAdapter(adapter);

    }

    private void hide(View view) {
        view.setVisibility(View.GONE);
    }

    private void hideKB(@Nullable EditText object) {
        InputMethodManager imm =  (InputMethodManager)c.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (object!=null){
            if(imm != null) {
                imm.hideSoftInputFromWindow(object.getWindowToken(), 0);
                object.clearFocus();
            }
            return;
        }
//        if(imm != null) {
//            imm.hideSoftInputFromWindow(c.getWindow().getDecorView().getWindowToken(), 0);
//        }
    }

    private void notifyTDRefresh() {
        Intent b = new Intent("com.dou.todou.notifyParentRefresh");
        c.sendBroadcast(b);
    }
}
