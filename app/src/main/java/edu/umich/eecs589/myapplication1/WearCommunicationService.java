package edu.umich.eecs589.myapplication1;

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
            Log.i(TAG, message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
