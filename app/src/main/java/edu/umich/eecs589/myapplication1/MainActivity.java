package edu.umich.eecs589.myapplication1;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private TextView mTextView;
    private UserInfo userInfo;
    private NumberPicker hourPicker;
    private NumberPicker minuPicker;
    private int departHour;
    private int departMinute;
    private final int MIN_HOUR = 0;
    private final int MAX_HOUR = 23;
    private final int MIN_MINU = 0;
    private final int MAX_HINU = 59;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = new UserInfo();
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
                departHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                hourPicker.setValue(departHour);
                hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal,
                                              int newVal) {
                        // TODO Auto-generated method stub
                        departHour = newVal;
                    }
                });

                minuPicker.setMinValue(MIN_MINU);
                minuPicker.setMaxValue(MAX_HINU);
                departMinute = Calendar.getInstance().get(Calendar.MINUTE);
                minuPicker.setValue(departMinute);
                minuPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal,
                                              int newVal) {
                        // TODO Auto-generated method stub
                        departMinute = newVal;
                    }
                });
            }
        });
    }

    public void saveDepartureTime(View view) {
        Log.i("Hour is:", "" + departHour);
        Log.i("Minu is:", "" + departMinute);
    }
}
