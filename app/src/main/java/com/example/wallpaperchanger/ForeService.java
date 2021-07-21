//Notification(알림바 관련): https://developer.android.com/training/notify-user/build-notification?hl=ko

package com.example.wallpaperchanger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ForeService extends Service {
    /*private Thread mThread;
    private ClipData clipData;


    public ForeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(mThread == null){
            mThread = new Thread("My Thread"){
                @Override
                public void run(){
                    intent.getClipData()
                }
            }
        }
    }*/
    private static final String TAG = "MyService_Sample3";
    private Thread mThread;
    private int mCount = 0;

    public ForeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("startForeground".equals(intent.getAction())) {
            startForegroundService();
        } else if (mThread == null) {
            mThread = new Thread("My Thread") {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        try {
                            mCount++;
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.e(TAG, "서비스 동작 중 " + mCount);
                    }
                    super.run();
                }
            };
            mThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy!");
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
            mCount = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("포그라운드 서비스");
        builder.setContentText("포그라운 서비스 실행중");
        builder.addAction()

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 이 코드가 동작한다.
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());//id를 0으로 하면안된다.
    }
}