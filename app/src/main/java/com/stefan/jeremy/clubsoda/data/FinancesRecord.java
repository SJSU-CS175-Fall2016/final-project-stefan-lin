package com.stefan.jeremy.clubsoda.data;


import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/* For pushing to Firebase database */
public class FinancesRecord {
  public Double amount;
  public String author;
  public String description;
  public Object timestamp;

  public FinancesRecord() { }

  public FinancesRecord(Double amount, String author, String description) {
    this.amount = amount;
    this.author = author;
    this.description = description;
    this.timestamp = ServerValue.TIMESTAMP;
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("amount", amount);
    result.put("author", author);
    result.put("description", description);
    result.put("timestamp", timestamp);
    return result;
  }

  @Exclude
  public Long getTimestamp() {
    if (timestamp instanceof Long) {
      return (Long) timestamp;
    }
    else return null;
  }
}