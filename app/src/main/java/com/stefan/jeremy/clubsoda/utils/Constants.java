package com.stefan.jeremy.clubsoda.utils;

import com.stefan.jeremy.clubsoda.BuildConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by stefanlin on 8/12/16.
 */

public final class Constants {
  public static final char SPACER = ' ';
  /**
   * Constants for Firebase URL
   */
  public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;

  public static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email");

  public static final String TWITTER_KEY    = BuildConfig.TWITTER_CONSUMER_KEY;
  public static final String TWITTER_SECRET = BuildConfig.TWITTER_CONSUMER_SECRET;


  /* TITLEBAR TITLES */
  public static final String MEMBERS_TITLE         = "Members";
  public static final String EVENTS_TITLE          = "Events";
  public static final String FINANCE_TITLE         = "Finances";
  public static final String ANNOUNCEMENTS_TITLE   = "Announcements";
  public static final String SETTINGS_TITLE        = "Settings";
  public static final String CLUB_MANAGEMENT_TITLE = "Club Mangement";

  /* BUNDLE KEYS */
  public static final String BUNDLE_USER = "usr";

  /* KEYS FOR INTENT */
  public static final String INTENT_PASS_USER      = "user";
  public static final String INTENT_FRAGMENT_VALUE = "value";
  public static final String INTENT_AVATAR_SRC_ID  = "avatar";
  public static final String INTENT_FRAGMENT_TYPE  = "type";
  public static final int ANNOUNCEMENT_TYPE        = 0;
  public static final int FINANCE_TYPE             = 1;

  /* FIREBASE KEYS */
  public static final String FIREBASE_ANNOUNCEMENT_TABLE = "announcement";
  public static final String FIREBASE_TABLE_RECORDS      = "records";


  /* DATE FORMAT */
  public static final String DATE_FORMAT = "MMMM' 'dd' 'yyyy' @ 'hh:mm z";
  public static final String OUTPUT_FORMAT = "By %s %s";

  /* HINT STRING FOR EDITTEXT IN RECORD */
  public static final String HINT_TITLE_ANNOUNCEMENT   = "Announcement Title";
  public static final String HINT_CONTENT_ANNOUNCEMENT = "Announcement Content \nLimited to 300 characters";


  /* ERROR MESSAGES FOR SNACKBAR */
  public static final int TOP_EDITTEXT = 0;
  public static final int BOTTOM_EDITTEXT = 1;
  public static final String ERROR_MESSAGE = "can not be empty.";
  public static final String[][] ERROR_FIELDS = {
    {"Title", "Content"},
    {},
    {"Amount", "Description"}
  };


  /* FINANCES */
  public static final String FINANCE_RED = "#E53935";
  public static final String FINANCE_GREEN = "#4CAF50";
  public static final String SUBMIT = "SUBMIT";

  /* ANNOUNCEMENT */
  public static final String POST_BUTTON_TEXT = "POST";
  /* Back button text */
  public static final String BACK_BUTTON = "BACK";


  /* User profile */
  public static final String USER_PROFILE_TITLE = "User Profile";
  public static final int PROFILE_PIC_HEIGHT = 200;
  public static final int PROFILE_PIC_WIDTH = 200;
}
