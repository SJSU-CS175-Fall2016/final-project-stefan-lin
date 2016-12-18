package com.stefan.jeremy.clubsoda.events;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.games.event.Events;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.EventRecord;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsFragment extends Fragment {
  private Date mCurrDayClicked;
  private EventAdapter newAdapter;
  private CompactCalendarView mCalendarView;
  private DatabaseReference mDatabase;
  private Long mTimestamp;
  private String mCurrMonthYear;
  private User mCurrUser;
  private FloatingActionButton mNewEventFAB;
  private TextView mMonthET;
  private ListView mEventsListView;
  private ArrayList<Event> mEventsList;
  private OnFragmentInteractionListener mListener;
  public static EventsFragment newInstance(String param1, String param2) {
    EventsFragment fragment = new EventsFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  public static EventsFragment newInstance(User mCurrUser) {
    EventsFragment fragment = new EventsFragment();
    Bundle args = new Bundle();
    args.putSerializable("user", mCurrUser);
    fragment.setArguments(args);
    return fragment;
  }  public EventsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_events, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    final View inflatedCalendarLayout = inflater.inflate(R.layout.calendar_layout, null, false);

    mEventsListView = (ListView) getActivity().findViewById(R.id.events_listView);
    mEventsListView.addHeaderView(inflatedCalendarLayout);
    mEventsList = new ArrayList<>();
    newAdapter = new EventAdapter(getContext(), mEventsList);
    mEventsListView.setAdapter(newAdapter);

    mCurrUser = (User) getArguments().getSerializable("user");
    mCalendarView = (CompactCalendarView) getActivity().findViewById(R.id.compactcalendar_view);
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mMonthET = (TextView) getActivity().findViewById(R.id.event_month);
    /* Transaction to ping for current date from Firebase */
    setupCalendar();

    mNewEventFAB = (FloatingActionButton) getActivity().findViewById(R.id.events_FAB);
    mNewEventFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openNewEventActivity();
      }
    });

    mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
      @Override
      public void onDayClick(Date dateClicked) {
        mCurrDayClicked = dateClicked;
        mEventsListView.setAdapter(null);
        Long timestamp = dateClicked.getTime();
        String mCurrDayMonthYear = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date(timestamp));
        mMonthET.setText(mCurrDayMonthYear);
        List<Event> events = mCalendarView.getEvents(dateClicked.getTime());
        mEventsList.clear();
        mEventsList.addAll(events);
        mEventsListView.setAdapter(newAdapter);
      }

      @Override
      public void onMonthScroll(Date firstDayOfNewMonth) {
        Long timestamp = firstDayOfNewMonth.getTime();
        String mCurrDayMonthYear = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date(timestamp));
        mMonthET.setText(mCurrDayMonthYear);
        populateEvents(timestamp);
      }
    });

  }

  private void setupCalendar() {
       mDatabase.child("curr-timestamp")
                .runTransaction(new Transaction.Handler() {
          @Override
          public Transaction.Result doTransaction(MutableData mutableData) {
            mutableData.setValue(ServerValue.TIMESTAMP);
            return Transaction.success(mutableData);
          }
          @Override
          public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            mTimestamp = (Long) dataSnapshot.getValue();

            /* After we get timestamp, setup Calendar */
            mCalendarView.setCurrentDate(new Date(mTimestamp));

            /* Set month-year */

            mCurrMonthYear = new SimpleDateFormat("MM-yyyy").format(new Date(mTimestamp));

            String mCurrDayMonthYear = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date(mTimestamp));
            mMonthET.setText(mCurrDayMonthYear);

            populateEvents(mTimestamp);
          }
        });

  }

  private void populateEvents(Long timestamp) {
    String monthYear = new SimpleDateFormat("MM'-'yyyy").format(new Date(timestamp));
    mDatabase.child("events").child(mCurrUser.currClub).child(monthYear).addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        EventRecord newChildEvent;
        String author = null;
        Long timestamp = null;
        String description = null;
        String uid = null;
        String title = null;

        for (DataSnapshot data : dataSnapshot.getChildren()) {
          switch(data.getKey()) {
            case "author":
              author = (String) data.getValue();
              break;
            case "timestamp":
              timestamp = (Long) data.getValue();
              break;
            case "uid":
              uid = (String) data.getValue();
              break;
            case "title":
              title = (String) data.getValue();
              break;
            case "description":
              description = (String) data.getValue();
              break;
          }
        }
        newChildEvent = new EventRecord(title,author,uid,description,timestamp);
        List<Event> existing_events = mCalendarView.getEvents(new Date(timestamp));
        for (Event e: existing_events) {
          EventRecord er = (EventRecord) e.getData();
          Long erTimestamp = (Long) er.timestamp;
          Long eTimestamp = (Long) newChildEvent.timestamp;
          /* Check if same event */
          if (er.author.equals(newChildEvent.author) && er.title.equals(newChildEvent.title)
                  && er.description.equals(newChildEvent.description) && eTimestamp.equals(erTimestamp)) {
            return;
          }
        }
        Event newEvent = new Event(Color.GRAY, (Long) newChildEvent.timestamp, newChildEvent);
        mCalendarView.addEvent(newEvent, true);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        /* Future functionality to edit events */
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
  }

  private void openNewEventActivity() {
    Intent intent = new Intent(getActivity(), NewEventActivity.class);
    intent.putExtra("user", mCurrUser);
    intent.putExtra("month-year", mCurrMonthYear);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
  }
  @Override
  public void onResume() {
    super.onResume();
    setupCalendar();
    if (mCurrDayClicked != null) {
      List<Event> events = mCalendarView.getEvents(mCurrDayClicked);
      mEventsList.clear();
      mEventsList.addAll(events);
      mEventsListView.setAdapter(newAdapter);
    }
  }
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onEventsFragmentInteraction();
  }

  /* Array adapter */

  public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.single_event_record, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      Event e = getItem(position);
      EventRecord er = (EventRecord) e.getData();

      if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_event_record, parent, false);
      }
      TextView _recordTime = (TextView) convertView.findViewById(R.id.event_record_time);
      TextView _title = (TextView) convertView.findViewById(R.id.event_record_title);
      TextView _description = (TextView) convertView.findViewById(R.id.event_record_description);

      SimpleDateFormat f = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
      String dateString = f.format(new Date((Long)er.timestamp)) + " by " + er.author;

      _recordTime.setText(dateString);
      _title.setText(er.title);
      _description.setText(er.description);
      return convertView;
    }
  }


}
