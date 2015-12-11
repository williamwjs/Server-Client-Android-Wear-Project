package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.logging.Logger;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private NumberPicker hourPicker;
    private NumberPicker minuPicker;
    private int arrivalHour;
    private int arrivalMinute;
    private final int MIN_HOUR = 0;
    private final int MAX_HOUR = 23;
    private final int MIN_MINU = 0;
    private final int MAX_HINU = 59;

    private final static String TAG = "TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                hourPicker = (NumberPicker) stub.findViewById(R.id.hour);
                minuPicker = (NumberPicker) stub.findViewById(R.id.minu);

                hourPicker.setMinValue(MIN_HOUR);
                hourPicker.setMaxValue(MAX_HOUR);
                arrivalHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                hourPicker.setValue(arrivalHour);
                hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal,
                                              int newVal) {
                        arrivalHour = newVal;
                    }
                });

                minuPicker.setMinValue(MIN_MINU);
                minuPicker.setMaxValue(MAX_HINU);
                arrivalMinute = Calendar.getInstance().get(Calendar.MINUTE);
                minuPicker.setValue(arrivalMinute);
                minuPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal,
                                              int newVal) {
                        arrivalMinute = newVal;
                    }
                });
            }
        });
    }

    public void saveArrivalTime(View view) {
        Log.i(TAG, arrivalHour + ":" + arrivalMinute);

        Intent intent = new Intent(MainActivity.this, DestinationStopActivity.class);
        intent.putExtra("ArrivalHour", arrivalHour);
        intent.putExtra("ArrivalMinute", arrivalMinute);
        startActivity(intent);
    }
}
