package com.stefan.jeremy.clubsoda.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class EventRecord {
  public String title;
  public String description;
  public String author;
  public String uid;
  public Object timestamp;

  public EventRecord() { }

  public EventRecord(String title, String author, String uid, String description, Long timestamp) {
    this.title = title;
    this.author = author;
    this.uid = uid;
    this.description = description;
    this.timestamp = timestamp;
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("uid", uid);
    result.put("author", author);
    result.put("description", description);
    result.put("timestamp", timestamp);
    return result;
  }

}
