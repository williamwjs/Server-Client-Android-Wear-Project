package edu.umich.eecs589.myapplication1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

/**
 * Created by willwjs on 12/13/15.
 */
public class MobileCommunicationService extends WearableListenerService {
    private static final String TAG = "MOBILCOM";
    private static final String WEAR = "Wear";
    private static final String MOBILE = "Mobile";
    private static GoogleCloudMessaging gcm;
    private static final String SENDER_ID = "1029481912999";
    private static int id = 10000;
    private static String sourceId = "";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("test", "onMessageReceived()");
        if(messageEvent.getPath().equals("Wear")) {
            final String message = new String(messageEvent.getData());
            Log.i(TAG, "Received msg: " + message);
            Log.i(TAG, "Src Id: " + messageEvent.getSourceNodeId());
            sourceId = messageEvent.getSourceNodeId();
            sendMsgToServer(message);
            /*googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            String msg = ++i + " Hello Mobile";
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, messageEvent.getSourceNodeId(), "Wearable", msg.getBytes()).await();
            //mTextView.setText(msg);
            if(!result.getStatus().isSuccess()){
                Log.e("test", "error");
            } else {
                Log.i("test", "success!! sent to: " + messageEvent.getSourceNodeId());
                //mGoogleApiClient.disconnect();
            }*/
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    public static void SendMsgToWear(final GoogleApiClient googleApiClient,
                                     final String msg) {
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, sourceId, MOBILE, msg.getBytes()).await();
        //mTextView.setText(msg);
        if(!result.getStatus().isSuccess()){
            Log.e(TAG, "error");
        } else {
            Log.i(TAG, "success!! sent to: " + sourceId);
            //mGoogleApiClient.disconnect();
        }
    }

    public void sendMsgToServer(final String msg) {
        gcm = GoogleCloudMessaging.getInstance(this);
        try {
            String regid = gcm.register(SENDER_ID);
        } catch (Exception e) {
            Log.e(TAG, "Can't register GCM");
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Bundle data = new Bundle();
                    data.putString("my_message", msg);
                    //data.putString("my_action", "SAY_HELLO");
                    //String id = Integer.toString(msgId.incrementAndGet());
                    if (gcm == null)
                        Log.e(TAG, "null pointer here");
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", ++id + "", data);
                    Log.i(TAG, "Send");
                    return null;
                } catch (IOException ex) {
                    return "Error sending upstream message:" + ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg != null) {
                    Log.i(TAG, "onPostExecute");
                }
            }
        }.execute(null, null, null);
    }
}
