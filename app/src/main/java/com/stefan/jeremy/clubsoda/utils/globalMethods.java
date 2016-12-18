package com.stefan.jeremy.clubsoda.utils;

/**
 * Created by Jeremy on 8/15/2016.
 */
public class globalMethods {
  public static String emailToDatabaseConverter(String email) {
    email = email.replaceAll("\\.", "DOT");
    email = email.replaceAll("@", "AT");
    return email;
  }
}
