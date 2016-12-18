package com.stefan.jeremy.clubsoda.data;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stefanlin on 8/17/16.
 */


public class AnnouncementRecord{
  public String author;
  public Object timestamp;
  public String description;
  public String title;
  public String uid;

  public AnnouncementRecord(){ super(); }

  public AnnouncementRecord(String title, String author, String description, String uid){
    //super(author, description, ServerValue.TIMESTAMP);
    this.title = title;
    this.author      = author;
    this.timestamp   = ServerValue.TIMESTAMP;
    this.description = description;
    this.uid = uid;
  } // END CONSTRUCTOR

  @Exclude
  public Map<String, Object> toMap(){
    HashMap<String, Object> result = new HashMap<>();
    result.put("title",     title);
    result.put("author",    author);
    result.put("content",   description);
    result.put("timestamp", timestamp);
    result.put("uid", uid);

    return result;
  } // END METHOD toMap

  @Exclude
  public  Long getTimestamp(){
    if(timestamp instanceof Long){
      return (Long) timestamp;
    }
    return Long.parseLong("1471487532554");  //// TODO: 8/17/16 fix this 
  } // END METHOD getTimestamp
}
