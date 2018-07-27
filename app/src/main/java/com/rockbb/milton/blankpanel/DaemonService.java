package com.rockbb.milton.blankpanel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class DaemonService extends Service implements HttpAsyncCallback {
    private static final String TAG = DaemonService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "13800138000");
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.formDataToString(map, "UTF-8"), HttpAsyncTask.METHOD_POST, "UTF-8", 0, this);
        task.execute("https://www.toutiao.com/api/pc/realtime_news/");
        //new DaemonThread("").start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 5 * 1000;

        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
        super.onDestroy();
    }

    @Override
    public void completionHandler(Boolean success, int type, Object object) {
        Log.d(TAG, "completionHandler");
        Intent intent = new Intent(MainActivityFragment.class.getName() + ".TextView");
        if (success) {
            intent.putExtra("msg", (String)object);
        } else {
            intent.putExtra("msg", String.valueOf(object));
        }
        sendBroadcast(intent);
    }
}
