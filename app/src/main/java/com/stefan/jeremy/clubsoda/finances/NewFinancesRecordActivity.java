package com.stefan.jeremy.clubsoda.finances;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.stefan.jeremy.clubsoda.ClubActivity;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.FinancesRecord;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewFinancesRecordActivity extends AppCompatActivity {

  private EditText mDollarET;
  private EditText mDescriptionET;
  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;
  private Button mAddButton;
  private Button mRemoveButton;

  private String mFirstName;
  private String mLastName;

  private User mCurrUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_finances_record);
    //mCurrUser = (User) savedInstanceState.getSerializable("user");
    Bundle b = getIntent().getExtras();
    mCurrUser = (User) b.getSerializable("user");

    /* Firebase */
    mAuth = FirebaseAuth.getInstance();
    mDatabase = FirebaseDatabase.getInstance().getReference();

    mDollarET = (EditText) findViewById(R.id.finances_dollars);
    mDollarET.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});

    mDescriptionET = (EditText) findViewById(R.id.finances_description);

    mAddButton = (Button) findViewById(R.id.post_button);
    mAddButton.setText(Constants.SUBMIT);
    mAddButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //TODO: handle if mDollarET.getText().length() == 0
        Double amount = Double.parseDouble(mDollarET.getText().toString());
        newRecord(amount);
      }
    });
  }

  /* Create a new record and push to database */
  private void newRecord(final Double amount) {
        String description = mDescriptionET.getText().toString();
        String full_name = mCurrUser.firstName + " " + mCurrUser.lastName;
        FinancesRecord fr = new FinancesRecord(amount, full_name, description);
        DatabaseReference ref = mDatabase.child("finances")
                                .child(mCurrUser.currClub).child("records").push();
        ref.setValue(fr);

        /* Transaction */

        mDatabase.child("finances").child(mCurrUser.currClub).child("current-balance")
                .runTransaction(new Transaction.Handler() {
          @Override
          public Transaction.Result doTransaction(MutableData mutableData) {
            Double balance = mutableData.getValue(Double.class);
            balance += amount;
            mutableData.setValue(balance);
            return Transaction.success(mutableData);
          }
          @Override
          public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) { }
        });

        /* Close activity */
        finish();
  }

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
