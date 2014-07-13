package com.mdlawson.gmailpop3fetcher.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import com.mdlawson.gmailpop3fetcher.R;

import java.util.ArrayList;
import java.util.Arrays;

public class IntervalPreference extends DialogPreference {

    static final ArrayList<String> values = new ArrayList<>(Arrays.asList("5", "10", "15", "20", "25", "30", "45", "60", "90", "120"));
    static final int DEFAULT = 15;
    int valueIndex;

    NumberPicker numberPicker;

    public IntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.interval_picker);
    }

    @Override
    protected void onBindDialogView(View view) {
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.size() - 1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDisplayedValues(values.toArray(new String[values.size()]));
        numberPicker.setValue(valueIndex);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            valueIndex = numberPicker.getValue();
            persistInt(Integer.valueOf(values.get(valueIndex)));
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value;
        if (restorePersistedValue) {
            value = getPersistedInt(DEFAULT);
        } else {
            persistInt(value = (Integer) defaultValue);
        }
        valueIndex = values.indexOf(String.valueOf(value));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT);
    }
}
