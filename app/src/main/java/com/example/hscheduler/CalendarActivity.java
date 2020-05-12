package com.example.hscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";
    private CalendarView calendarView;
    private String doctorID,pacientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendar_view);

        InitialiazeVariables();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String date = (dayOfMonth + 1) + "/" + month + "/" + year;

                System.out.println("Calendar Activity " + date);

                Intent intent = new Intent(getBaseContext(),DoctorProfileActivity.class);
                intent.putExtra("date",date);
                intent.putExtra("ID",doctorID);
                intent.putExtra("USER ID",pacientID);
                startActivity(intent);
            }
        });

    }

    private void InitialiazeVariables() {

        doctorID = (String) getIntent().getSerializableExtra("Doctor ID");
        pacientID = (String) getIntent().getSerializableExtra("Pacient ID");
    }
}
