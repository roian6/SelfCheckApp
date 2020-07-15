package com.david0926.selfcheck.util;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;

import com.david0926.selfcheck.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @BindingAdapter("spinnerItem")
    public static void bindSpinnerItem(Spinner spinner, String[] items) {
        if (items == null) return;
        spinner.setAdapter(new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_dropdown_item, items));
    }

    @BindingAdapter("spinnerList")
    public static void bindSpinnerItem(Spinner spinner, List<String> items) {
        if (items == null) return;
        spinner.setAdapter(new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_dropdown_item, items));
    }

}
