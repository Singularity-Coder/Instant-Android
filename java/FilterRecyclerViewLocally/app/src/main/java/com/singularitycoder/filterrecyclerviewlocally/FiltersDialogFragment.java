package com.singularitycoder.filterrecyclerviewlocally;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static java.lang.String.valueOf;

public final class FiltersDialogFragment extends DialogFragment {

    @NonNull
    private final String TAG = "FiltersDialogFragment";

    @NonNull
    private final String[] categoryArray = new String[]{"All Categories", "Robots", "Light Sabers", "Space Ships", "Quantum Computers", "Scouters", "Capsules"};

    @NonNull
    private final String[] priceArray = new String[]{"All Prices", "1000", "5000", "9000", "Its Over 9000"};

    @Nullable
    private AlertDialogListener alertDialogListener;

    public FiltersDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (null != getArguments() && ("activity").equals(getArguments().getString("KEY_CONTEXT_TYPE"))) {
            try {
                alertDialogListener = (AlertDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(getActivity().toString() + " must implement AlertDialogListener");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (null != getArguments() && ("FilterDialog").equals(getArguments().getString("KEY_DIALOG_ALERT_TYPE"))) {
            buildFilterDialog(builder);
        }

        return builder.create();
    }

    @UiThread
    public final void buildFilterDialog(@NonNull final AlertDialog.Builder builder) {
        final String title = getArguments().getString("KEY_TITLE");
        final String message = getArguments().getString("KEY_MESSAGE");
        final String positiveButtonText = getArguments().getString("KEY_POSITIVE_BUTTON_TEXT");
        final String negativeButtonText = getArguments().getString("KEY_NEGATIVE_BUTTON_TEXT");
        final String neutralButtonText = getArguments().getString("KEY_NEUTRAL_BUTTON_TEXT");
        final String contextType = getArguments().getString("KEY_CONTEXT_TYPE");
        final String contextObject = getArguments().getString("KEY_CONTEXT_OBJECT");

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_filters, null);

        final TextView tvCategory = view.findViewById(R.id.tv_category);
        final TextView tvPrice = view.findViewById(R.id.tv_price);
        final TextView tvSeekBarProgress = view.findViewById(R.id.tv_seekbar_value);
        final TextView tvDate = view.findViewById(R.id.tv_date);
        final TextView tvTime = view.findViewById(R.id.tv_time);
        final Spinner spinnerCategory = view.findViewById(R.id.spinner_category);
        final Spinner spinnerPrice = view.findViewById(R.id.spinner_price);
        final RadioGroup rgPrice = view.findViewById(R.id.radio_group_price_range);
        final RadioGroup rgAlphabet = view.findViewById(R.id.radio_group_alphabetic);
        final SeekBar seekBarProximity = view.findViewById(R.id.seekbar_proximity);
        final TextView tvPlaceholderPrice = view.findViewById(R.id.tv_placeholder_price);
        final ConstraintLayout clPrice = view.findViewById(R.id.cl_price);

        final Bundle bundle = new Bundle();
        bundle.putString("KEY_DIALOG_ALERT_TYPE", "FilterDialog");
        bundle.putString("KEY_PROXIMITY", "0");
        bundle.putString("KEY_CATEGORY", "All Categories");
        bundle.putString("KEY_PRICE", "All Prices");
        bundle.putString("KEY_ALPHABET", "NONE");
        bundle.putString("KEY_PRICE_RANGE", "NONE");
        bundle.putString("KEY_DATE", "NONE");
        bundle.putString("KEY_TIME", "NONE");

        tvPlaceholderPrice.setVisibility(View.VISIBLE);
        clPrice.setVisibility(View.VISIBLE);

        setUpCategoryFilter(spinnerCategory, tvCategory, bundle);
        setUpPriceFilter(spinnerPrice, tvPrice, bundle);
        setUpProximityFilter(seekBarProximity, tvSeekBarProgress, bundle);
        setUpPriceRangeFilter(rgPrice, tvPlaceholderPrice, clPrice, bundle);
        setUpAlphabetFilter(rgAlphabet, bundle);

        tvCategory.setOnClickListener(view1 -> {
            spinnerCategory.setVisibility(View.VISIBLE);
            tvCategory.setVisibility(View.GONE);
            spinnerCategory.performClick();
        });
        tvPrice.setOnClickListener(view1 -> {
            spinnerPrice.setVisibility(View.VISIBLE);
            tvPrice.setVisibility(View.GONE);
            spinnerPrice.performClick();
        });
        tvDate.setOnClickListener(view12 -> showDatePickerOldStyle(tvDate, bundle));
        tvTime.setOnClickListener(view12 -> showTimePickerOldStyle(tvTime, bundle));

        builder.setView(view);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder
                .setPositiveButton(positiveButtonText, (dialog1, id) -> alertDialogListener.onAlertDialogPositiveClick("DIALOG_TYPE_SIMPLE_ALERT", FiltersDialogFragment.this, bundle))
                .setNegativeButton(negativeButtonText, (dialog12, id) -> alertDialogListener.onAlertDialogNegativeClick("DIALOG_TYPE_SIMPLE_ALERT", FiltersDialogFragment.this, bundle))
                .setNeutralButton(neutralButtonText, (dialog12, id) -> alertDialogListener.onAlertDialogNeutralClick("DIALOG_TYPE_SIMPLE_ALERT", FiltersDialogFragment.this, bundle));
    }

    private void setUpAlphabetFilter(RadioGroup rgAlphabet, Bundle bundle) {
        rgAlphabet.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_a_to_z) {
                bundle.putString("KEY_ALPHABET", "AToZ");
            }

            if (checkedId == R.id.radio_z_to_a) {
                bundle.putString("KEY_ALPHABET", "ZToA");
            }

            if (checkedId == R.id.radio_alphabetic_none) {
                bundle.putString("KEY_ALPHABET", "NONE");
            }
        });
    }

    private void setUpPriceRangeFilter(RadioGroup rgPrice, TextView tvPlaceholderPrice, ConstraintLayout clPrice, Bundle bundle) {
        rgPrice.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_low_to_high) {
                tvPlaceholderPrice.setVisibility(View.GONE);
                clPrice.setVisibility(View.GONE);

                bundle.putString("KEY_PRICE_RANGE", "LowToHigh");
                bundle.putString("KEY_PRICE", "All Prices");
            }

            if (checkedId == R.id.radio_high_to_low) {
                tvPlaceholderPrice.setVisibility(View.GONE);
                clPrice.setVisibility(View.GONE);

                bundle.putString("KEY_PRICE_RANGE", "HighToLow");
                bundle.putString("KEY_PRICE", "All Prices");
            }

            if (checkedId == R.id.radio_price_range_none) {
                tvPlaceholderPrice.setVisibility(View.VISIBLE);
                clPrice.setVisibility(View.VISIBLE);

                bundle.putString("KEY_PRICE_RANGE", "NONE");
            }
        });
    }

    private void setUpProximityFilter(@NonNull final SeekBar seekBarProximity, @NonNull final TextView tvSeekBarProgress, @NonNull Bundle bundle) {
        seekBarProximity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekBarProgress.setText(valueOf(seekBar.getProgress() * 20)); // when progress changed.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvSeekBarProgress.setText(valueOf(seekBar.getProgress() * 20)); // when touch started.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvSeekBarProgress.setText(valueOf(seekBar.getProgress() * 20)); // when touch stopped.
            }
        });

        tvSeekBarProgress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bundle.putString("KEY_PROXIMITY", valueOf(editable));
            }
        });
    }

    private void setUpCategoryFilter(@NonNull final Spinner spinnerCategory, @NonNull final TextView tvCategory, Bundle bundle) {
        final ArrayList<String> categoryList = new ArrayList<>(Arrays.asList(categoryArray));
        final ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(arrayAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    tvCategory.setVisibility(View.VISIBLE);
                    tvCategory.setText(parentView.getItemAtPosition(position).toString());
                    bundle.putString("KEY_CATEGORY", valueOf(parentView.getItemAtPosition(position)));
                } else {
                    spinnerCategory.setVisibility(View.GONE);
                    tvCategory.setVisibility(View.VISIBLE);
                    tvCategory.setText(parentView.getItemAtPosition(position).toString());
                    bundle.putString("KEY_CATEGORY", valueOf(parentView.getItemAtPosition(position)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setUpPriceFilter(@NonNull final Spinner spinnerPrice, @NonNull final TextView tvPrice, Bundle bundle) {
        final ArrayList<String> priceList = new ArrayList<>(Arrays.asList(priceArray));
        final ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, priceList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(arrayAdapter);
        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPrice.setText(parentView.getItemAtPosition(position).toString());
                    bundle.putString("KEY_PRICE", valueOf(parentView.getItemAtPosition(position)));
                } else {
                    spinnerPrice.setVisibility(View.GONE);
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPrice.setText(parentView.getItemAtPosition(position).toString());
                    bundle.putString("KEY_PRICE", valueOf(parentView.getItemAtPosition(position)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void showDatePickerOldStyle(@NonNull final TextView tvDate, @NonNull final Bundle bundle) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        final DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, year, monthOfYear, dayOfMonth) -> {
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd/MM/yy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    tvDate.setText(sdf.format(c.getTime()));
                    bundle.putString("KEY_DATE", sdf.format(c.getTime()));
                },
                mYear,
                mMonth,
                mDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showTimePickerOldStyle(@NonNull final TextView tvTime, @NonNull final Bundle bundle) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        final TimePickerDialog dialog = new TimePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, hourOfDay, minute) -> {
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);

                    String my12HrFormat = "hh:mm aa";
                    String my24HrFormat = "HH:mm";
                    SimpleDateFormat sdf = new SimpleDateFormat(my24HrFormat, Locale.US);
                    tvTime.setText(sdf.format(c.getTime()));
                    bundle.putString("KEY_TIME", sdf.format(c.getTime()));
                },
                mHour,
                mMinute,
                false);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public interface AlertDialogListener {
        void onAlertDialogPositiveClick(final String dialogType, final DialogFragment dialog, final Bundle bundle);

        void onAlertDialogNegativeClick(final String dialogType, final DialogFragment dialog, final Bundle bundle);

        void onAlertDialogNeutralClick(final String dialogType, final DialogFragment dialog, final Bundle bundle);
    }
}