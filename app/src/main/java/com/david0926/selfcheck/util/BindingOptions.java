package com.david0926.selfcheck.util;

import android.view.View;
import android.widget.Button;


import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;

import com.david0926.selfcheck.R;

public class BindingOptions {

    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @BindingAdapter("buttonEnabled")
    public static void setButtonEnabled(Button button, Boolean enabled) {
        button.setEnabled(enabled);
        button.setBackgroundTintList(ContextCompat.getColorStateList(button.getContext(),
                enabled ? R.color.colorPrimary : R.color.materialGray5));
    }

}
