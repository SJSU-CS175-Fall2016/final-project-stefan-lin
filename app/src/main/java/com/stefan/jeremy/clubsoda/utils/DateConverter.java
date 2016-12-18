package com.stefan.jeremy.clubsoda.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by stefanlin on 8/17/16.
 */

public class DateConverter {
  static final private DateFormat mDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
  //static final private Formatter  mFormatter  = new Formatter();
  static public String convertMillisecondToDate(String authorName, Long millisecond){
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(millisecond);
    //return mDateFormat.format(calendar.getTime());
    return String.format(
             Constants.OUTPUT_FORMAT,
             authorName,
             mDateFormat.format(calendar.getTime())
           );
  }
}
