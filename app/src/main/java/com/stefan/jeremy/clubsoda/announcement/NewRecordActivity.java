package com.stefan.jeremy.clubsoda.announcement;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.AnnouncementRecord;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.finances.NewFinancesRecordActivity;
import com.stefan.jeremy.clubsoda.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stefanlin on 8/16/16.
 */
//TODO: Maybe let NewFinancesRecordActivity and NewAnnouncementActivity Inherited from this class
public class NewRecordActivity extends AppCompatActivity{
  /* Components */
  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;
  private User mCurrentUser;
  private int  mParentFragmentType; /* 0: Announcement, 1: Finance */
  private EditText mTopEditText;
  private EditText mBottomEditText;
  private Button   mAddButton;
  private Button   mRemoveButton;
  private Button   mPostButton;

  //private FloatingActionButton mAddButton;
  //private FloatingActionButton mRemoveButton;

  private void parseInputInIntent(){
    Bundle extras = getIntent().getExtras();
    if(extras.containsKey(Constants.INTENT_PASS_USER)){
      mCurrentUser = (User) extras.getSerializable(Constants.INTENT_PASS_USER);
    }
    else{
      //TODO: handle missing User object
    }
    if(extras.containsKey(Constants.INTENT_FRAGMENT_TYPE)){
      mParentFragmentType = extras.getInt(Constants.INTENT_FRAGMENT_TYPE);
    }
    else{
      //TODO: handle missing parent fragment type
    }
  } // END METHOD parseInputInIntent

  private void updateUI(){
    this.initComponents();
    if(this.mParentFragmentType == Constants.ANNOUNCEMENT_TYPE){
      this.mPostButton.setText(Constants.POST_BUTTON_TEXT);
      /* Temporarily get rid of Remove button */ //// TODO: 8/18/16 take out add/remove buttons
      ViewManager viewManager = ((ViewManager)mRemoveButton.getParent());
      viewManager.removeView(mRemoveButton);
      viewManager.removeView(findViewById(R.id.finances_button_spacer));
      /* Done removal */
      this.mAddButton.setGravity(Gravity.CENTER); /* Reposition addButton */
      this.mTopEditText.setHint(Constants.HINT_TITLE_ANNOUNCEMENT);
      this.mTopEditText.setInputType(InputType.TYPE_CLASS_TEXT);
      this.mBottomEditText.setHint(Constants.HINT_CONTENT_ANNOUNCEMENT);
      this.mAddButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          newRecord();
        } // END METHOD onClick
      }); // END METHOD CALL setOnClickListener
      /* TODO: get rid of add and remove buttons in XML */
      this.mPostButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          newRecord();
        }
      });
    } // END IF
    else if(this.mParentFragmentType == Constants.FINANCE_TYPE){
      //TODO: merge codes from NewFinancesRecordActivity
      this.mTopEditText.setFilters(
        new InputFilter[] {new DecimalDigitsInputFilter(10,2)}
      );
      this.mPostButton.setText(Constants.SUBMIT);
      this.mPostButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
      });
    }
    else{
      //TODO: handle unknown parent fragment type
    }
  } // END METHOD updateUI

  private boolean validateEditTexts(){
    StringBuilder _stringBuilder           = new StringBuilder();
    Editable      _inputFromTopEditText    = mTopEditText.getText();
    Editable      _inputFromBottomEditText = mBottomEditText.getText();
    /* Testing EditText inputs if it's empty */
    if(_inputFromTopEditText.length() == 0){
      _stringBuilder.append(
        Constants.ERROR_FIELDS[mParentFragmentType][Constants.TOP_EDITTEXT]
      );
      _stringBuilder.append(Constants.SPACER);
    }
    if(_inputFromBottomEditText.length() == 0){
      _stringBuilder.append(
        Constants.ERROR_FIELDS[mParentFragmentType][Constants.BOTTOM_EDITTEXT]
      );
      _stringBuilder.append(Constants.SPACER);
    }
    if(_stringBuilder.length() != 0){
      _stringBuilder.append(Constants.ERROR_MESSAGE);
      this.promptSnackbarMessage(_stringBuilder.toString());
      return false;
    }
    return true;
  } // END METHOD validateEditTexts

  private void promptSnackbarMessage(String message){
    // SHOW SNACKBAR
    Snackbar _snackbar = Snackbar.make(
      mAddButton,
      message,
      Snackbar.LENGTH_LONG
    );
    _snackbar.show();
  } // END METHOD promptSnackbarMessage

  /*
  mDatabase.child("announcements").child(mCurrUser.currClub).push()    ---> this returns a database reference
a new node is created with a unique ID.
at announcements->currClub
now at that reference, setValue(MyAnnouncementsObject)
   */

  //String title, String author, String description
  private void newRecord(){
    if(validateEditTexts()){  /* True: EditText inputs not empty */
      String _inputTopEditText    = mTopEditText.getText().toString();
      String _inputBottomEditText = mBottomEditText.getText().toString();
      String _author = mCurrentUser.firstName + " " + mCurrentUser.lastName;
      AnnouncementRecord announcementRecord = new AnnouncementRecord(
        _inputTopEditText.trim(),
        _author.trim(),
        _inputBottomEditText.trim(),
        mAuth.getCurrentUser().getUid()
      );  // END announcementRecord ASSIGNMENT
      DatabaseReference _ref = mDatabase.child(Constants.FIREBASE_ANNOUNCEMENT_TABLE)
                                        .child(mCurrentUser.currClub).push();
      _ref.setValue(announcementRecord);
      /* Close activity */
      finish();
    }
    return; /* If EditText inputs are not valid, do nothing */
  } // END METHOD newRecord - Announcement

  private void newRecord(final Double amount){

  } // END METHOD newRecord - Finances

  private void initComponents(){
    /* Firebase */
    this.mAuth = FirebaseAuth.getInstance();
    this.mDatabase = FirebaseDatabase.getInstance().getReference();
    /* UI */
    this.mTopEditText    = (EditText)findViewById(R.id.finances_dollars);
    this.mBottomEditText = (EditText)findViewById(R.id.finances_description);
    this.mAddButton      = (Button)findViewById(R.id.finances_addButton);
    this.mRemoveButton   = (Button)findViewById(R.id.finances_removeButton);
    //this.mAddButton = (FloatingActionButton)findViewById(R.id.finances_addButton);
    //this.mRemoveButton = (FloatingActionButton)findViewById(R.id.finances_removeButton);
    this.mPostButton     = (Button)findViewById(R.id.post_button);
  } // END METHOD initComponents

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_finances_record);  //TODO: maybe modify name
    this.parseInputInIntent();  /* Retrieve inputs from Intent */
    this.updateUI(); /* modify UIs */
  } // END METHOD onCreate


  /* Uses Regex to make sure that the input is in $XX.XX format */
  public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
      mPattern = Pattern.compile("^-?[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

      Matcher matcher = mPattern.matcher(dest);
      if (!matcher.matches())
        return "";
      return null;
    }
  }
}
