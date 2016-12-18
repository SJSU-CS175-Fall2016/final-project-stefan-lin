package com.stefan.jeremy.clubsoda.data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* For pushing to Firebase database */
public class User implements Serializable {
  public String email;
  public String firstName;
  public String lastName;

  @Exclude
  public String currClub;

  public User() { }

  public User(String email, String firstName, String lastName, String currClub) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;

    this.currClub = currClub;
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("email", email);
    result.put("firstName", firstName);
    result.put("lastName", lastName);
    return result;
  }
}
