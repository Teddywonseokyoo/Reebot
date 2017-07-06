package com.example.reebotui.ir;

/**
 * Created by freem on 2017-03-09.
 */

import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by JW on 16. 10. 3..
 */
public class VIRService extends android.accessibilityservice.AccessibilityService implements VIRCommand.VIRCommandListener{
    private static final String TAG = "ReeBot(VIRService)";

    private String preText;
    private int vflag = 0;
    private  VIRCommand task;
    final String surl="http://reebot.io:8085";
    private String sid = "y31y3y81";

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.e(TAG, "s=========================================================================");
        //Log.e(TAG, "Catch Event : " + event.toString());
        //Log.e(TAG, "Catch Event Package Name : " + event.getPackageName());
        //Log.e(TAG, "Catch Event TEXT : " + event.getText());
        //Log.e(TAG, "Catch Event ContentDescription  : " + event.getContentDescription());
        //Log.e(TAG, "Catch Event getSource : " + event.getSource());
        //Log.e(TAG, "e=========================================================================");
        if(event.getEventType() == 4096)
        {
            if(event.getSource().getContentDescription() == null ) {
                //    Intent intent = new Intent(this, MainActivity.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //  getApplicationContext().startActivity(intent);

            }
            else
            {
                Log.e(TAG, "Catch Event ContentDescription  : "+event.getSource().getContentDescription());
                //check condition (구문 분석 및 행위 파악)
                if(event.getSource().getContentDescription().toString().equals("tv 켜 줘 - Google 검색")|| event.getSource().getContentDescription().toString().equals("tv 꺼 줘 - Google 검색")|| event.getSource().getContentDescription().toString().equals("티비 꺼 줘 - Google 검색")|| event.getSource().getContentDescription().toString().equals("티비 켜 줘 - Google 검색")) {
                    Log.e(TAG, "Action  : TV ON");
                    new VIRCommandMaker(getApplicationContext(),surl,"TSSL","CONTROL","PW",sid);
                    //명령 생성
                    //command (명령 전달)
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","samsung","0xE0E040BF"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0310EF"));
                    //Intent mainintent = new Intent(getBaseContext(), InitActivity.class);
                    //startActivity(mainintent);
                }
                else if(event.getSource().getContentDescription().toString().equals("jtbc - Google Search")||event.getSource().getContentDescription().toString().equals("jtbc - Google 검색")||event.getSource().getContentDescription().toString().equals("jtbc 틀어 줘 - Google 검색"))
                {
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B038877"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B03A857"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0322DD"));
                }
                else if(event.getSource().getContentDescription().toString().equals("올리브 tv 틀어 줘 - Google 검색"))
                {
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0318E7"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0348B7"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0322DD"));
                }
                else if(event.getSource().getContentDescription().toString().equals("mbc - Google 검색")||event.getSource().getContentDescription().toString().equals("엠비씨 틀어 줘 - Google 검색"))
                {
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B038877"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B038877"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0322DD"));

                }
                else if(event.getSource().getContentDescription().toString().equals("kbs1 - Google 검색")||event.getSource().getContentDescription().toString().equals("kbs1 틀어 줘 - Google 검색")||event.getSource().getContentDescription().toString().equals("kbs 틀어 줘 - Google 검색"))
                {
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B039867"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0322DD"));

                }
                else if(event.getSource().getContentDescription().toString().equals("kbs2 - Google 검색")||event.getSource().getContentDescription().toString().equals("kbs2 틀어 줘 - Google 검색"))
                {
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B03E817"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0322DD"));

                }
                else if(event.getSource().getContentDescription().toString().equals("SBS - Google Search")||event.getSource().getContentDescription().toString().equals("sbs - Google 검색")||event.getSource().getContentDescription().toString().equals("sbs 틀어 줘 - Google 검색"))
                {
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B03A857"));
//                    task = new VIRCommand(this);
//                    task.execute(new VIRDataValue("http://192.168.1.92","nec","0x5B0322DD"));

                }
            }


        }
    }
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK; // 전체 이벤트 가져오기
        //info.eventTypes = AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT;
        //info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_HAPTIC;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        info.notificationTimeout = 1000; // millisecond
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub
        Log.e("TEST", "OnInterrupt");
    }

    @Override
    public void VIRCommandListener(String msg) {

    }
}
/*
public class VIRService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {



        public ServiceHandler(Looper looper) {
            super(looper);
        }


        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    public VIRService() {
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
*/