package edu.umich.eecs589.myapplication1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by willwjs on 12/13/15.
 */
public class WearCommunicationService extends WearableListenerService {
    private static final String TAG = "WEARCOM";
    private static final String WEAR = "Wear";
    private static final String MOBILE = "Mobile";
    private static final String BUSOPT = "BusOpt";
    private static final String WAKE = "Wake";

    public static void sendMsg(final GoogleApiClient mGoogleApiClient,
                               final String msg) {
        if(mGoogleApiClient.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    for(Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), WEAR, msg.getBytes()).await();
                        if(!result.getStatus().isSuccess()){
                            Log.e(TAG, "error");
                        } else {
                            Log.i(TAG, "success!! sent to: " + node.getDisplayName());
                        }
                    }
                }
            }).start();

        } else {
            Log.e(TAG, "not connected");
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "onMessageReceived()");
        if(messageEvent.getPath().equals(MOBILE)) {
            final String message = new String(messageEvent.getData());
            Log.i(TAG, "Received msg: " + message);
            String[] strs = message.split("\\|");
            if (BUSOPT.equals(strs[1])) {
                Log.d(TAG, "Go to BusActivity");
                Intent intent = new Intent(this, BusActivity.class);
                intent.putExtra("BusInfo", message);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (WAKE.equals(strs[1])) {
                Log.d(TAG, "Go to WakeActivity");
                Intent intent = new Intent(getApplicationContext(), WakeActivity.class);
                intent.putExtra("Wake", message);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle("Bus Tracker")
                        .setContentText("Bus Coming")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
