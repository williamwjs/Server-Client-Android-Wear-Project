package edu.umich.eecs589.myapplication1;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
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
    private static final String UNABLE = "Unable";
    private static final String WAKE = "Wake";
    private static final int COMMANDINDEX = 1;

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
            if (BUSOPT.equals(strs[COMMANDINDEX])) {
                Log.d(TAG, "Go to BusActivity");
                Intent intent = new Intent(this, BusActivity.class);
                intent.putExtra("BusInfo1", strs[COMMANDINDEX + 1]);
                intent.putExtra("BusInfo2", strs[COMMANDINDEX + 2]);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (WAKE.equals(strs[COMMANDINDEX])) {
                Log.d(TAG, "Go to WakeActivity");
                Intent intent = new Intent(getApplicationContext(), WakeActivity.class);
                intent.putExtra("GPS", strs[COMMANDINDEX + 1]);
                intent.putExtra("Time", strs[COMMANDINDEX + 2]);
                intent.putExtra("BusName", strs[COMMANDINDEX + 3]);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                int notificationId = 001;
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Bitmap background = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_background);

                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.ic_stat_ic_notification,
                                getString(R.string.bus_coming), pendingIntent)
                                .build();
                Notification notification =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(getString(R.string.bus_coming_detail))
                                .setSound(defaultSoundUri)
                                .setVibrate(new long[]{0, 500, 50, 300})
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setContentIntent(pendingIntent)
                                .extend(new WearableExtender()
                                        .addAction(action)
                                        .setBackground(background))
                                .build();

                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(this);

                notificationManager.notify(notificationId, notification);
            } else if (UNABLE.equals(strs[COMMANDINDEX])) {
                Log.d(TAG, "Go to MainActivity");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                int notificationId = 002;
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Bitmap background = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_background);

                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.ic_stat_ic_notification,
                                getString(R.string.bus_unable), pendingIntent)
                                .build();
                Notification notification =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(getString(R.string.bus_unable_detail))
                                .setSound(defaultSoundUri)
                                .setVibrate(new long[]{0, 500, 50, 300})
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setContentIntent(pendingIntent)
                                .extend(new WearableExtender()
                                        .addAction(action)
                                        .setBackground(background))
                                .build();

                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(this);

                notificationManager.notify(notificationId, notification);
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
