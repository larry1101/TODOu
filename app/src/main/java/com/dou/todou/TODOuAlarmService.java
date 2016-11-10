package com.dou.todou;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TODOuAlarmService extends Service {
    public TODOuAlarmService() {
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

//        Toast.makeText(TODOuAlarmService.this, "Service1 start", Toast.LENGTH_SHORT).show();
        Log.d("TUDOUService1", "Create");
//        stopSelf();
    }

    @Override
    public void onDestroy() {

//        Intent intent = new Intent(this, TODOuAlarmService2.class);
//        startService(intent);

        Log.d("TUDOUService1", "Stop");
        super.onDestroy();

    }
}
