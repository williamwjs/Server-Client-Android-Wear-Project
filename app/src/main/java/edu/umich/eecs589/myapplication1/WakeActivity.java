package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

public class WakeActivity extends Activity {

    private final static String TAG = "WAKE";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Intent lastIntent = getIntent();
                Log.i(TAG, lastIntent.getStringExtra("GPS"));
                Log.i(TAG, lastIntent.getStringExtra("Time"));
                Log.i(TAG, lastIntent.getStringExtra("BusName"));

                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }
}
