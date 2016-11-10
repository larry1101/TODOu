package com.dou.todou;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Administrator on 2016-11-8.
 */
public class TODOuWidgetRemoteService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TODOuWidgetRemoteFactory(this.getApplicationContext(), intent);
    }
}
