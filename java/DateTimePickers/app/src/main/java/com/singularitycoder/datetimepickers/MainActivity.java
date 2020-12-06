package com.singularitycoder.datetimepickers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText etDate, etTime;
    private int mYear, mMonth, mDay;
    private int mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
    }

    public void showDatePicker(View view) {
        // Get Current Date
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set the date u chose in the calendar
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        // (or)
                        String myFormat = "dd/MM/yy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        etDate.setText(sdf.format(cal.getTime()));
                    }
                },
                mYear,
                mMonth,
                mDay).show();
    }

    public void showTimePicker(View view) {
        // Get Current Time
        final Calendar cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // set the time u chose in the calendar
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);

                        // etTime.setText(hourOfDay + ":" + minute);
                        // (or)
                        String myFormat = "hh:mm aa";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        etTime.setText(sdf.format(cal.getTime()));
                    }
                },
                mHour,
                mMinute,
                false).show();
    }
}
