package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by willwjs on 12/11/15.
 */
public class SocketCommunication {
    private static Handler handler = new Handler(Looper.getMainLooper());
    public static String hostIP = "IP";
    public static int port = 1234;
    private static String receivedMsg = "";
    private static String TAG = "SOCKET";

    public static void connect(final TcpClient client, final Activity activity) {
        if (client.isConnected()) {
            // 断开连接
            //client.disconnect();
        } else {
            try {
                client.connect(hostIP, port);
            } catch (Exception e) {
                Toast.makeText(activity, "端口错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.e(TAG, "Connection Failed");
                return;
            }
        }

        if (client.isConnected()) {
            Log.i(TAG, "Successfully connected");
        }
    }

    public static void sendStr(final TcpClient client, String msg) {
        try {
            client.getTransceiver().send(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Send Failure");
            return;
        }

        Log.i(TAG, "Send Successfully");
    }
}
