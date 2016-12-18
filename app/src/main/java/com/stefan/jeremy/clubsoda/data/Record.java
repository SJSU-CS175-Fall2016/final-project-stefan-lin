package com.stefan.jeremy.clubsoda.data;

import com.google.firebase.database.Exclude;

/**
 * Created by stefanlin on 8/17/16.
 */

public class Record {
  public String author;
  public Object timestamp;
  public String description;

  public Record(){}
  public Record(String author, String description, Object timestamp){
    this.author      = author;
    this.timestamp   = timestamp;
    this.description = description;
  } // END CONSTRUCTOR

  //@Exclude
  //public  Long getTimestamp(){
  //  if(timestamp instanceof Long){
  //    return (Long) timestamp;
  //  }
  //  return null;
  //} // END METHOD getTimestamp
}
