package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

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
                String[] BUS_NAME = null;
                String busInfo1 = lastIntent.getStringExtra("BusInfo1");
                String busInfo2 = lastIntent.getStringExtra("BusInfo2");
                if ("none".equals(busInfo2)) {
                    BUS_NAME = new String[] {busInfo1};
                } else {
                    BUS_NAME = new String[] {busInfo1, busInfo2};
                }

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
        String msg = ++i + "|BUS|" + selectedBus;
        WearCommunicationService.sendMsg(DestinationStopActivity.mGoogleApiClient, msg);
        finishAffinity();
    }


}
