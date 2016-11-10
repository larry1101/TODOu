package com.dou.todou;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.camnter.easyslidingtabs.widget.EasySlidingTabs;
import com.dou.todou.adapters.TabsFragmentAdapter;
import com.dou.todou.fragments.FragmentDONE;
import com.dou.todou.fragments.FragmentTODO;

import java.util.LinkedList;
import java.util.List;

// TODO: 2016-11-6 连续多次滑动会把内存吃光！！！ 未解决。。。

public class TODOuEnt extends AppCompatActivity {

    private EasySlidingTabs easySlidingTabs;
    private ViewPager easyVP;
    private TabsFragmentAdapter adapter;
    List<Fragment> fragments;

    public static final String[] titles = { "TODO", "DONE" };

    private static final String APP_NAME = "TODOu";
    Context context;

    ImageButton button_more;

    PopupMenu popupMenu;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_todou_ent);

            context = this;

//            iniViews();
//            iniPopupMenu();
            button_more = (ImageButton)findViewById(R.id.imageButton_ent_more);

            popupMenu = new PopupMenu(this,button_more);
            menu = popupMenu.getMenu();

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.pop, menu);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.pop_add:
                            TODOuItem titem = new TODOuItem();
                            Intent intent = new Intent(context,ItemDetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("item",titem);
                            intent.putExtras(bundle);
                            intent.putExtra("has_parent",true);
                            startActivity(intent);
                            break;
                        case R.id.pop_help:
                            Toast.makeText(TODOuEnt.this, "Contact me ... please...", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.pop_contact_me:
                            Toast.makeText(TODOuEnt.this, "@拖延兜", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            button_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu.show();
                }
            });

            iniESViews();
            iniESData();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(TODOuEnt.this, "Error, Please Relaunch", Toast.LENGTH_SHORT).show();
            Log.e(APP_NAME, "Error");
            finish();
        }

//        Log.d(APP_NAME, "Act created, Service 1 started");

//        Intent intent = new Intent(this, TODOuAlarmService.class);
//        startService(intent);

    }

    private void iniESData() {
        this.fragments = new LinkedList<>();
        FragmentTODO t = FragmentTODO.getInstance().setContext(context);
        FragmentDONE d = FragmentDONE.getInstance().setContext(context);
        this.fragments.add(t);
        this.fragments.add(d);

        this.adapter = new TabsFragmentAdapter(this.getSupportFragmentManager(), titles,
                this.fragments);
        this.easyVP.setAdapter(this.adapter);
        this.easySlidingTabs.setViewPager(this.easyVP);
    }

    private void iniESViews() {
        this.easySlidingTabs = (EasySlidingTabs) this.findViewById(R.id.easy_sliding_tabs);
        this.easyVP = (ViewPager) this.findViewById(R.id.easy_vp);
    }


}
