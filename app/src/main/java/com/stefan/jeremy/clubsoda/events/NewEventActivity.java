package com.stefan.jeremy.clubsoda.events;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.EventRecord;
import com.stefan.jeremy.clubsoda.data.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewEventActivity extends AppCompatActivity {

  private Calendar mUserSelectedDate;
  private static int mHourOfDay, mDay, mMonth, mYear, mMinute;
  private DatabaseReference mDatabase;
  private User mCurrUser;
  private FirebaseAuth mAuth;
  private Button mTimeButton;
  private Button mDateButton;
  private EditText mTitle;
  private EditText mDescription;
  private Button mPostButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_event);

    mDateButton = (Button) findViewById(R.id.event_chooseDate);
    mTimeButton = (Button) findViewById(R.id.event_chooseTime);

    mCurrUser = (User) getIntent().getSerializableExtra("user");
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();
    mTitle = (EditText) findViewById(R.id.event_title);
    mDescription = (EditText) findViewById(R.id.event_description);

    mMonth = -1;
    mYear = -1;

    mPostButton = (Button) findViewById(R.id.post_button);
    mPostButton.setText("POST");

    mPostButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        /* Create timestamp */
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth, mDay, mHourOfDay, mMinute);
        Timestamp _timestamp = new Timestamp(cal.getTimeInMillis());
        Long timestamp = _timestamp.getTime();

        /* Create new Event */
        String author = mCurrUser.firstName + " " + mCurrUser.lastName;
        String uid = mAuth.getCurrentUser().getUid();
        String description = mDescription.getText().toString();
        String title = mTitle.getText().toString();
        EventRecord newEvent = new EventRecord(title, author, uid, description, timestamp);

        /* Get month-year */
        if (mMonth == -1 || mYear == -1) {
          Toast.makeText(getApplicationContext(), "Please provide a month and date.", Toast.LENGTH_SHORT).show();
          return;
        }
        String monthYear = new SimpleDateFormat("MM-yyyy").format(new Date(timestamp));

        /* Write to database */
        DatabaseReference ref = mDatabase.child("events").child(mCurrUser.currClub).child(monthYear).push();
        ref.setValue(newEvent);

        finish();
      }
    });
  }


  public void showTimePickerDialog(View v) {
    DialogFragment newFragment = new TimePickerFragment();
    newFragment.show(getSupportFragmentManager(), "timePicker");
  }
  public void showDatePickerDialog(View v) {
    DialogFragment newFragment = new DatePickerFragment();
    newFragment.show(getSupportFragmentManager(), "datePicker");
  }
  public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Calendar c = Calendar.getInstance();
      int hour = c.get(Calendar.HOUR_OF_DAY);
      int minute = c.get(Calendar.MINUTE);

      return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      mHourOfDay = hourOfDay;
      mMinute = minute;

      /* Update button */
      Button timeButton = (Button) getActivity().findViewById(R.id.event_chooseTime);
      /* Lazy */
      String timeText;
      if (minute > 9) {
        timeText = hourOfDay + ":" + minute;
      } else {
        timeText = hourOfDay + ":0" + minute;
      }
      timeButton.setText(timeText);
      timeButton.invalidate();
    }
  }

  public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
      mYear = year;
      mMonth = month;
      mDay = day;

      /* Update button */
      Button dateButton = (Button) getActivity().findViewById(R.id.event_chooseDate);
      String dateText = day + "/" + month + "/" + year;
      dateButton.setText(dateText);
      dateButton.invalidate();
    }
  }


}
