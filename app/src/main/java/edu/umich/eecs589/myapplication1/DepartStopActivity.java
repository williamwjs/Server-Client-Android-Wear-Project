package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class DepartStopActivity extends Activity {

    private String departStop;
    private String receivedMsg = "";
    private TextView mTextView;
    private TcpClient client = new TcpClient() {

        @Override
        public void onConnect(SocketTransceiver transceiver) {
            //refreshUI(true);
        }

        @Override
        public void onDisconnect(SocketTransceiver transceiver) {
            //refreshUI(false);
        }

        @Override
        public void onConnectFailed() {
                /*handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "连接失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
        }

        @Override
        public void onReceive(SocketTransceiver transceiver, final String s) {
                /*handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txReceive.append(s);
                    }
                });*/
            receivedMsg = s;
            Log.i(TAG, receivedMsg);
        }
    };

    private final static int SPEECH_REQUEST_CODE = 0;
    private final static String TAG = "CURRENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depart_stop);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.departStop);
            }
        });
        departStop = "none";
        SocketCommunication.connect(client, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null)
            client.disconnect();
    }

    public void saveDepartStop(View view) {
        Intent lastIntent = getIntent();
        int hour = lastIntent.getIntExtra("ArrivalHour", 0);
        int minute = lastIntent.getIntExtra("ArrivalMinute", 0);
        String destinationStop = lastIntent.getStringExtra("DestinationStop");

        Log.i(TAG, departStop + ", " + destinationStop + ", " + hour + ":" + minute);

        SocketCommunication.sendStr(client, "This is a test message");
    }

    public void getVoiceInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            departStop = results.get(0);
            mTextView.setText(departStop);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
