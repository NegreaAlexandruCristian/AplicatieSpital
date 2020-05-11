package com.example.hscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String date = (dayOfMonth + 1) + "/" + month + "/" + year;

                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });

    }
}
