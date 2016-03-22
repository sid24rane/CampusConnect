package com.example.sid.campusconnect.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sid.campusconnect.DiscussionRoom.DiscussionRoom;
import com.example.sid.campusconnect.R;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sid on 17-Jan-16.
 */
public class CustomNotificationReceiver extends ParsePushBroadcastReceiver {

    String disid,fullmsg,usern,text,title,body,type;

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        Log.d("TAG", "Inside Push Open");
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        Intent ic = null;

        if (intent==null)
            return;
        //JSONObject data;
        try {
            JSONObject json =new JSONObject (intent.getExtras().getString("com.parse.Data"));
            type=json.getString("type");
            //data=json.getJSONObject("data");

            if(type.equals("question"))
            {
                usern=json.getString("username");
                text=json.getString("text");
                body=json.getString("qsTitle");

                title=usern;
                fullmsg = text + ":\n"+ body;

                ic= new Intent(context, Notifications.class);
                ic.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            if(type.equals("answer"))
            {
                usern=json.getString("username");
                text=json.getString("text");
                body=json.getString("ans");

                title=usern;
                fullmsg = text + ":\n"+ body;

                ic= new Intent(context, Notifications.class);
            }
            if(type.equals("comment")) {
                usern = json.getString("username");
                text = json.getString("text");
                body = json.getString("comment");

                title = usern;
                fullmsg = text + ":\n" + body;

                ic = new Intent(context, Notifications.class);
            }
            if(type.equals("chat"))
            {
                title=json.getString("title");

                usern=json.getString("username");
                body=json.getString("msg");
                disid=json.getString("disid");

                fullmsg = usern + " : "+body;

                ic= new Intent(context, DiscussionRoom.class);
                ic.putExtra("discussion_id", disid);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        int icon= R.drawable.campuslogo1;

        ic.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent result = PendingIntent.getActivity(context, 0, ic, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle Style = new NotificationCompat.InboxStyle();
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context);

        Notification notification = mbuilder
                .setTicker(title)
                .setSmallIcon(icon)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(result)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),icon))
                .setContentText(fullmsg)
                .setStyle(Style)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, notification);


    }
}

