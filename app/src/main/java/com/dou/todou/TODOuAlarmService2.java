package com.dou.todou;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TODOuAlarmService2 extends Service {
    public TODOuAlarmService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        Toast.makeText(TODOuAlarmService2.this, "Service2 start", Toast.LENGTH_SHORT).show();

        Log.d("TUDOUService2", "Create");
    }

    @Override
    public void onDestroy() {

//        Intent intent = new Intent(this, TODOuAlarmService.class);
//        startService(intent);

        Log.d("TUDOUService2", "Stop");
        super.onDestroy();
    }
}
