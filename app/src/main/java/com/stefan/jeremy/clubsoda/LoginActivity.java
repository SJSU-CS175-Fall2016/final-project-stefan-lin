package com.stefan.jeremy.clubsoda;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stefan.jeremy.clubsoda.createClub.CreateClubActivity;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.login.LoginFragment;
import com.stefan.jeremy.clubsoda.login.SignUpFragment;

import com.stefan.jeremy.clubsoda.utils.globalMethods;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.content.pm.Signature;


public class LoginActivity
             extends    AppCompatActivity
             implements LoginFragment.OnFragmentInteractionListener,
                        SignUpFragment.OnFragmentInteractionListener {
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String TAG = "EmailPassword";
  private ProgressDialog mAuthProgressDialog;
  private DatabaseReference mDatabase;


  String email, firstName, lastName;
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Firebase.setAndroidContext(this);
    setContentView(R.layout.activity_main);



    //this._debug_hashKey(); //TODO: delete this later


    /* TODO: Below comment logic is not implemented yet */
    /* Check if user has credentials to auto-login,
       if not, go to login fragment */
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.replace(R.id.fragmentholder, LoginFragment.newInstance());
    ft.commit();
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    if(mAuth.getCurrentUser() != null) {
      checkIfUserIsInAClub();
    }
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
          Log.d(TAG, "onAuthStateChanged:signed_out");
        }
      }
    };

    ////////////// TESTING //////////////////////
    //Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentholder);
    /////////////////////////////////////////////
  }

  /* Fixing invalid key hash for facebook */
  /* 08-13 02:28:55.354 29800-29800/? E/YourKeyHash :: 2txKQkb39yd9T2o2pqdfZCQ7uS4= */
  //TODO: delete this method later
  private void _debug_hashKey(){
    try {
      PackageInfo info = getPackageManager().getPackageInfo(
        "com.stefan.jeremy.clubsoda",
        PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.e("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {

    } catch (NoSuchAlgorithmException e) {

    }
  }


  /* Interaction with login fragment */
  public void onLogin(String email, String password){
    if (email.isEmpty() || password.isEmpty()) {
      Toast.makeText(getApplicationContext(), "Empty username or password.",
              Toast.LENGTH_SHORT).show();
      return;
    }
    //displayProgressDialog("Logging in..");
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
        if(!task.isSuccessful()) {
          Log.w(TAG, "signInWithEmail:failed", task.getException());
          Toast.makeText(getApplicationContext(), "Username or password incorrect.",
                        Toast.LENGTH_LONG).show();
          return;
        }

        /* Login Successful */
        checkIfUserIsInAClub();

      }
    });
  }

  @Override
  public void onSignUpComplete(String email, String password,
                               String firstName, String lastName) {
    /* Create new user in Firebase */
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
     /****************************************
     *               SIGN UP
     ****************************************/
    if (email.isEmpty() || password.isEmpty()) {
      Toast.makeText(getApplicationContext(), "Username or password empty.",
              Toast.LENGTH_SHORT).show();
      return;
    }
    displayProgressDialog("Signing up..");
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        mAuthProgressDialog.hide();
        Log.d(TAG, "createdUserWithEmail:onComplete:" + task.isSuccessful());
        if (!task.isSuccessful()) {
          Toast.makeText(getApplicationContext(), "Failed to create new account.",
                         Toast.LENGTH_SHORT).show();
          return;
        }
                /* TODO: Go to post-signup fragment */
      }
    });

    /****************************************
     *               SIGN IN
     ****************************************/

    displayProgressDialog("Logging in..");
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        mAuthProgressDialog.hide();
        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
        if (!task.isSuccessful()) {
          Log.w(TAG, "signInWithEmail:failed", task.getException());
          Toast.makeText(getApplicationContext(), "Username or password incorrect.",
                  Toast.LENGTH_LONG).show();
          return;
        }

        /* On successful login, push to to database and switch to club activity */
        String uid = mAuth.getCurrentUser().getUid();
        pushNewUserData(uid);
        mAuthProgressDialog.hide();
        checkIfUserIsInAClub();
      }
    });
  }



  /* HELPER FUNCTIONS */
  private void pushNewUserData(String uid) {
    User newUser = new User(email, firstName, lastName, null);
    Map<String, Object> userValues = newUser.toMap();
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + uid, userValues);
    mDatabase.updateChildren(childUpdates);
    String emailFormat = globalMethods.emailToDatabaseConverter(email);
    mDatabase.child("email-to-uid").child(emailFormat).setValue(uid);
  }

  private void displayProgressDialog(String message) {
    mAuthProgressDialog = new ProgressDialog(this);
    mAuthProgressDialog.setMessage(message);
    mAuthProgressDialog.setCancelable(false);
    mAuthProgressDialog.show();
  }

  ProgressDialog pd;
  private void switchToClubActivity() {
    pd.dismiss();
    Intent clubIntent = new Intent(this, ClubActivity.class);
    clubIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    Bundle b = new Bundle();
    b.putSerializable("mCurrUser", mCurrUser);
    b.putSerializable("mCurrClubName", mCurrClubName);
    b.putSerializable("clubNameList", clubNameList);
    clubIntent.putExtras(b);
    startActivity(clubIntent);
    overridePendingTransition(0,0);
    finish();
  }
  private void switchToNoClubActivity() {
    pd.dismiss();
    Log.e("switchToNoClubActivity", "here");
    Intent clubIntent = new Intent(this, CreateClubActivity.class);
    clubIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(clubIntent);
    overridePendingTransition(0,0);
    finish();
  }

  private void checkIfUserIsInAClub() {
    /* If user is NOT in a club, switch to new club activity */
    /* Check user-club-index */
    String uid = mAuth.getCurrentUser().getUid();
    pd = new ProgressDialog(this);
    mDatabase.child("user-club-index").child(uid).addListenerForSingleValueEvent(
            new ValueEventListener() {
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                 switchToNoClubActivity();
                }
                else
                {
                  pd.setMessage("Loading user data..");
                  pd.show();
                  getAllUserData();
                }
              }
              public void onCancelled(DatabaseError databaseError) { /* Do nothing */ }
            }
    );
  }
  private String mCurrClubName;
  private ArrayList<String> clubNameList;
  private User mCurrUser;
  private void getAllUserData() {
    clubNameList = new ArrayList<>();
    getListOfClubs();
  }

    private void getListOfClubs() {
    final String uid = mAuth.getCurrentUser().getUid();
    mDatabase.child("user-club-index").child(uid).child("clubs").addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                  String clubName = child.getKey();
                  clubNameList.add(clubName);
                }
                getLastVisitedClub();
              }
              @Override
              public void onCancelled(DatabaseError databaseError) { }
            }
     );
   }

    private void getLastVisitedClub() {
    final String uid = mAuth.getCurrentUser().getUid();

    mDatabase.child("user-club-index").child(uid).child("last-visited").addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  mCurrClubName = (String) dataSnapshot.getValue();
                } else {
                  mCurrClubName = clubNameList.get(0);
                  mDatabase.child("user-club-index").child(uid).child("last-visited").setValue(mCurrClubName);
                }
                // Can only setup toolbar AFTER this data is received.
                getUserInformation();
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
              }
            }
    );
  }

    private void getUserInformation() {
    String uid = mAuth.getCurrentUser().getUid();
    mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                String email = null;
                String firstName= null;
                String lastName = null;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                  switch(child.getKey()) {
                    case "email":
                      email = (String) child.getValue();
                      break;
                    case "firstName":
                      firstName = (String) child.getValue();
                      break;
                    case "lastName":
                      lastName = (String) child.getValue();
                      break;
                  }
                }
                mCurrUser = new User(email, firstName, lastName, mCurrClubName);
                switchToClubActivity();
              }
              @Override
              public void onCancelled(DatabaseError databaseError) {
              }
            });
    }






  /**************************************
   * NECESSARY FOR FACEBOOK AND TWITTER *
   **************************************/
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Pass the activity result to the fragment, which will then pass the result to the TwitterAuthClient.
    Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentholder);
    if (fragment != null) {
      fragment.onActivityResult(requestCode, resultCode, data);
    }
  }
}
