package edu.umich.eecs589.myapplication1;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by nschwermann on 6/28/14.
 */
public class ListenerService extends WearableListenerService{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service====", "Create");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("test", "onMessageReceived()");
        Log.i("Wearable==1==", new String(messageEvent.getData()));
        if (messageEvent.getPath().equals("Mobile")) {
            final String message = new String(messageEvent.getData());
            Log.i("Wearable=======", message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
