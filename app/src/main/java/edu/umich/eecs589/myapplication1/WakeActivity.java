package edu.umich.eecs589.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

public class WakeActivity extends WearableActivity {

    private final static String TAG = "WAKE";
    private TextView mTextView1;
    private TextView mTextView2;

    private String gps;
    private String busTime;
    private String busName;

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
                gps = lastIntent.getStringExtra("GPS");
                busTime = "Bus Time: " + lastIntent.getStringExtra("Time");
                busName = "Bus: " + lastIntent.getStringExtra("BusName");

                mTextView1 = (TextView) stub.findViewById(R.id.text_name);
                mTextView2 = (TextView) stub.findViewById(R.id.text_time);

                mTextView1.setText(busName);
                mTextView2.setText(busTime);
            }
        });
    }
}
