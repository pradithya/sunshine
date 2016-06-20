package net.ariapura.sunshine.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import net.ariapura.sunshine.R;

/**
 * Created by aria on 20/6/16.
 */
public class LocationEditTextPreference extends EditTextPreference {
    private static final String LOG_TAG = LocationEditTextPreference.class.getSimpleName();
    private static final int DEFAULT_MIN_TEXT_LENGTH = 2;
    private int minLength = DEFAULT_MIN_TEXT_LENGTH;

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styleAttr = context
                .obtainStyledAttributes(attrs, R.styleable.LocationEditTextPreference, 0, 0);

        try {
            minLength = styleAttr.getInt(R.styleable.LocationEditTextPreference_minLength, 0);
        } finally {
            styleAttr.recycle();
        }

        Log.d(LOG_TAG, "created a LocationEditTextPreference with minimum length = " + minLength);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        EditText editText = getEditText();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                Dialog d = getDialog();
                if (d instanceof AlertDialog) {
                    Button positiveButton = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
                    if (s.length() >= minLength) {
                        positiveButton.setEnabled(true);
                    } else {
                        positiveButton.setEnabled(false);
                    }
                }
            }
        });
    }
}
