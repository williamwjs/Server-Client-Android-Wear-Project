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
    private String selectedBus;

    private final static String TAG = "BUS";
    private final static String[] BUS_NAME = {"NW", "BB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                busPicker = (NumberPicker) stub.findViewById(R.id.bus);

                busPicker.setMinValue(0);
                busPicker.setMaxValue(BUS_NAME.length - 1);
                busPicker.setValue(0);
                selectedBus = BUS_NAME[0];
                busPicker.setDisplayedValues(BUS_NAME);
                busPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal,
                                              int newVal) {
                        selectedBus = BUS_NAME[newVal];
                    }
                });
            }
        });
        Intent intent = getIntent();
    }

    public void saveSelectedBus(View view) {
        Intent intent = new Intent(BusActivity.this, DepartStopActivity.class);
        Intent lastIntent = getIntent();
        intent.putExtra("DepartHour", lastIntent.getIntExtra("DepartHour", 0));
        intent.putExtra("DepartMinute", lastIntent.getIntExtra("DepartMinute", 0));
        intent.putExtra("SelectedBus", selectedBus);
        Log.i(TAG, selectedBus);
        startActivity(intent);
    }


}
