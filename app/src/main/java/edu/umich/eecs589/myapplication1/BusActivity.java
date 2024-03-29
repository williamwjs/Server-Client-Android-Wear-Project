package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.LinkedList;

public class BusActivity extends Activity {

    private TextView mTextView;
    private NumberPicker busPicker;
    private int selectedBus;
    private static int i = 20000;

    private final static String TAG = "BUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Intent lastIntent = getIntent();
                String busInfo = lastIntent.getStringExtra("BusInfo");
                String[] buses = busInfo.split("\\|");
                String[] BUS_NAME = new String[buses.length - 2];
                System.arraycopy(buses, 2, BUS_NAME, 0, BUS_NAME.length);

                mTextView = (TextView) stub.findViewById(R.id.text);

                busPicker = (NumberPicker) stub.findViewById(R.id.bus);

                busPicker.setMinValue(0);
                busPicker.setMaxValue(BUS_NAME.length - 1);
                busPicker.setValue(0);
                selectedBus = 0;
                busPicker.setDisplayedValues(BUS_NAME);
                busPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal,
                                              int newVal) {
                        selectedBus = newVal;
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    public void saveSelectedBus(View view) {
        String msg = ++i + "|BUS|" + selectedBus + "|";
        WearCommunicationService.sendMsg(DestinationStopActivity.mGoogleApiClient, msg);
        finishAffinity();
    }


}
