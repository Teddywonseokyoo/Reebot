package com.example.reebotui.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.reebotui.InitActivity;
import com.example.reebotui.MainActivity;
import com.exp.rb.reebot.R;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by freem on 2017-05-09.
 */

public class RBFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {
    private static final String TAG = "RBFirebaseInstanceIDService";
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //추가한것
        Log.d("TAG","onMessageReceived : " + remoteMessage );
        //sendNotification(remoteMessage.getData().get("message"));

        Map<String, String> data = remoteMessage.getData();
        sendNotification(data);
        //you can get your text message here.



    }
    private void sendNotification( Map<String, String> data) {

        String target_title = data.get("target_title");
        String target_body = data.get("target_body");
        String target_chnum = data.get("target_chnum");
        String target_id = data.get("target_id");
        //String target_catv = data.get("target_catv");
        //String target_tvb = data.get("target_tvb");
        //String target_email = data.get("target_email");

        System.out.println(TAG + ", sendNotification: " + target_body +"  /  "+target_chnum);
        Intent intent = new Intent(this, InitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("RbLaunchType", "booking");    //silver20170530
        intent.putExtra("BookingBody", target_body);    //silver20170530
        intent.putExtra("BookingChnum", target_chnum);    //silver20170530
        //intent.putExtra("catvb", target_catv);
        //intent.putExtra("tvb", target_tvb);
        //intent.putExtra("email", target_email);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
       // Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pushsound);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ReeBot Message")
                .setContentText("시청 알림"+target_body)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification(String messageBody) {
        System.out.println(TAG + ", sendNotification: " + messageBody);
        Log.d("TAG","sendNotification : " + messageBody );
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("RbLaunchType", "booking");    //silver20170530
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        // Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pushsound);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ReeBot Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
