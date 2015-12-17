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

    private TextView mTextView;
    private String departStop;

    private static int i = 30000;
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
    }

    public void saveDepartStop(View view) {
        Intent lastIntent = getIntent();
        int hour = lastIntent.getIntExtra("ArrivalHour", 0);
        int minute = lastIntent.getIntExtra("ArrivalMinute", 0);
        String destinationStop = lastIntent.getStringExtra("DestinationStop");

        String msg = ++i + "|GPS|" + departStop + "|" + destinationStop
                + "|" + hour + "|" + minute + "|";
        Log.i(TAG, msg);
        WearCommunicationService.sendMsg(DestinationStopActivity.mGoogleApiClient, msg);
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
